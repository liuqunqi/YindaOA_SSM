package com.service.impl;

import com.dao.*;
import com.model.*;
import com.service.IAskLeaveExcelService;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 队标：一篇代码，最好不要超过200行，尽量不要超过300行，一定不能超过500行
 */
@Service
public class AskLeaveExcelServiceImpl implements IAskLeaveExcelService {

    @Autowired
    public AskForLeaveMapper askForLeaveMapper;

    /**
     * 该方法实现对表头的校验，至于剩余内容的校验，在插入方法中完成
     * 表头不符合规范或者发生了空指针异常，皆视为校验失败
     * 为了方便，暂时将Map的格式统一为String+Object
     */
    public Map<String, Object> checkExcelTitle(String fileDir) throws IOException {
        Map<String, Object> errorMap = new HashMap<String, Object>();
        File file = new File(fileDir);
        InputStream inputStream = new FileInputStream(file);
        // Java的规定，有了输入流才能按照格式读取excel文件
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        // 得到当前文件的总表数
        int sheetTotal = hssfWorkbook.getNumberOfSheets();

        // 接下来对每一张表都进行操作
        for (int sheetNo=0; sheetNo<sheetTotal; sheetNo++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheetNo);
            // 在表头校验方法中，我们只玩第一行！
            HSSFRow hssfRow = hssfSheet.getRow(0);
            try {
                // int也是一个对象，大括号结束后会释放掉
                int cellNo = 0;
                // 这里用一个大胆的做法，先执行函数再自加。虽然++i听说效率更高，但想必也高不到哪里去
                if (hssfRow.getCell(cellNo++).toString().equals("审批编号")
                        && hssfRow.getCell(cellNo++).toString().equals("标题")
                        && hssfRow.getCell(cellNo++).toString().equals("审批状态")
                        && hssfRow.getCell(cellNo++).toString().equals("审批结果")
                        && hssfRow.getCell(cellNo++).toString().equals("审批发起时间")
                        && hssfRow.getCell(cellNo++).toString().equals("审批完成时间")
                        && hssfRow.getCell(cellNo++).toString().equals("发起人工号")
                        && hssfRow.getCell(cellNo++).toString().equals("发起人姓名")
                        && hssfRow.getCell(cellNo++).toString().equals("发起人部门")
                        && hssfRow.getCell(cellNo++).toString().equals("历史审批人姓名")
                        && hssfRow.getCell(cellNo++).toString().equals("审批记录")
                        && hssfRow.getCell(cellNo++).toString().equals("当前处理人姓名")
                        && hssfRow.getCell(cellNo++).toString().equals("审批耗时")
                        && hssfRow.getCell(cellNo++).toString().equals("请假类型")
                        && hssfRow.getCell(cellNo++).toString().equals("开始日期")
                        && hssfRow.getCell(cellNo++).toString().equals("开始时间")
                        && hssfRow.getCell(cellNo++).toString().equals("结束日期")
                        && hssfRow.getCell(cellNo++).toString().equals("结束时间")
                        && hssfRow.getCell(cellNo++).toString().equals("请假天数")
                        && hssfRow.getCell(cellNo++).toString().equals("请假事由")
                        && hssfRow.getCell(cellNo++).toString().equals("图片")
                        ) {
                    // 如果验证通过了，就打印成功信息（额，要不然什么都不做的话显得不太好= =）
                    System.out.println("表头审核成功！通过审核的表格页数 = "+sheetNo+1);
                }
                else {
                    errorMap.put("row", "表头");
                    errorMap.put("column", "表头");
                    errorMap.put("reason", "表头名称错误，与模板不相符");
                    return errorMap;
                }
            } catch (NullPointerException e) {
                errorMap.put("row", "表头");
                errorMap.put("column", "表头");
                errorMap.put("reason", "表头名称错误，与模板不相符");
                return errorMap;
            }
        }

        return errorMap;
    }

    /**
     * 循环地操作excel中的每一行数据
     * 如果审批编号相同，就更新数据，如果为新数据则插入
     */
    public Map<String, Object> insertAndUpdate(String fileDir) throws IOException {
        Map<String, Object> reportMap = new HashMap<String, Object>();
        List<AskForLeave> listFail = new ArrayList<AskForLeave>();

        File file = new File(fileDir);
        InputStream inputStream = new FileInputStream(file);
        // Java的规定，有了输入流才能按照格式读取excel文件
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        // 得到当前文件的总表数
        int sheetTotal = hssfWorkbook.getNumberOfSheets();

        // 设定一个变量，记录for循环当中操作成功的条目数目
        int successAmount = 0;
        // 接下来对每一张表都进行操作
        for (int sheetNo=0; sheetNo<sheetTotal; sheetNo++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(sheetNo);
            // 这里不要用物理行数，要用最后一行的编号，不然很容易跳坑
            int rowLastNo = hssfSheet.getLastRowNum();

            // 从第二行开始，对每一张表中的每一行进行操作
            for (int rowNo=1; rowNo<=rowLastNo; rowNo++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNo);
                // 同理，这里也不要用单元格的总数，要用最后一个单元格的序号
                int cellLastNo = hssfRow.getLastCellNum();

                /*
                现在开始做真正的功能！
                第一步，把得到的hssfRow对象中的每一个cell都设为文本类型，确保每一个整形数值在toString后不会自动加上.0
                */
                for (int cellNo=0; cellNo<=cellLastNo; cellNo++) {
                    // cell不为空时才操作，为空也就不用管他是什么类型了
                    // 想管也管不起，因为会报NullPointerException，而我们编程时应当避免异常，而不是积极处理异常
                    if (hssfRow.getCell(cellNo) != null) hssfRow.getCell(cellNo).setCellType(1);
                }

                /*
                第二步，检查当前行是不是空行，如果是就跳过后面的，操作下一行
                检查方法：空的单元格的数目是否等于最后单元格序号+1
                 */
                int emptyCellAmount = 0;
                for (int cellNo=0; cellNo<=cellLastNo; cellNo++) {
                    // cell不为空时才操作，为空也就不用管他是什么类型了
                    // 想管也管不起，因为会报NullPointerException，而我们编程时应当避免异常，而不是积极处理异常
                    if (hssfRow.getCell(cellNo)==null || hssfRow.getCell(cellNo).equals("")) {
                        // 这里就可以用++i了，听说运算速度更快= =
                        ++emptyCellAmount;
                    }
                }
                if (emptyCellAmount == cellLastNo+1) continue;

                /*
                第三步，对于不为空的行，将数据注入引用过来的实体对象
                在完成后面的函数之后，再对变量自加，方便下一行的判断。再玩一把火！
                 */
                int cellNo = 0;
                AskForLeave askForLeave = new AskForLeave();
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoApproveNo(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoTitle(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoApproveState(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoApproveResult(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoApproveBegin(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoApproveEnd(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoAskStaffId(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoAskStaffName(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoAskStaffDepart(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoHistoryApproveName(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoApproveRecord(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoNowApproveName(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoCost(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoType(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoAskBeginDate(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoAskBeginTime(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoAskEndDate(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoAskEndTime(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoAskSustain(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoAskReason(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) askForLeave.setYoPicture(hssfRow.getCell(cellNo++).toString());

                /*
                第四步，检查数据库中是否有相同的审批编号，如果没有，说明是一个新的条目，执行插入操作
                之所以有失败的可能性，是因为单元格内容有可能超过数据库长度
                 */
                String approveNo = hssfRow.getCell(0).toString();
                AskForLeaveExample askForLeaveExample = new AskForLeaveExample();
                askForLeaveExample.createCriteria().andYoApproveNoEqualTo(approveNo);
                List<AskForLeave> listExist = askForLeaveMapper.selectByExample(askForLeaveExample);

                if (listExist.size() == 0) {
                    try {
                        askForLeaveMapper.insert(askForLeave);
                    } catch (Exception e) {
                        listFail.add(askForLeave);
                        continue;
                    }
                /*
                第五步，如果有相同的编号，说明数据库中有元数据
                那么，就覆盖这一条数据
                同样，也有失败的可能性
                 */
                }
                else {
                    int sequenceNo = listExist.get(0).getSequenceNo();
                    askForLeave.setSequenceNo(sequenceNo);
                    try {
                        askForLeaveMapper.updateByPrimaryKey(askForLeave);
                    } catch (Exception e) {
                        listFail.add(askForLeave);
                        continue;
                    }
                }

                // 到了这一步，说明插入或更新成功，数目自加！
                successAmount++;
            }
        }

        // for循环之后，把成功数目和失败列表返回到map
        reportMap.put("successAmount", successAmount);
        reportMap.put("listFail", listFail);
        return reportMap;
    }

}