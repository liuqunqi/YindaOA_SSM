package com.service.impl;

import com.dao.StaffInfoMapper;
import com.model.StaffInfo;
import com.service.IExcelStaffInfoService;
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
public class ExcelStaffInfoServiceImpl implements IExcelStaffInfoService {

    @Autowired
    public StaffInfoMapper staffInfoMapper;

    /**
     * 该方法实现对表头的校验，至于剩余内容的校验，在插入方法中完成
     * 表头不符合规范或者发生了空指针异常，皆视为校验失败
     */
    public String validateExcelTitle(String fileDir) throws IOException {
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
                if (hssfRow.getCell(cellNo++).toString().equals("员工UserID")
                        && hssfRow.getCell(cellNo++).toString().equals("部门")
                        && hssfRow.getCell(cellNo++).toString().equals("职位")
                        && hssfRow.getCell(cellNo++).toString().equals("姓名")
                        && hssfRow.getCell(cellNo++).toString().equals("性别")
                        && hssfRow.getCell(cellNo++).toString().equals("工号")
                        && hssfRow.getCell(cellNo++).toString().equals("是否此部门主管(是/否)")
                        && hssfRow.getCell(cellNo++).toString().equals("手机号")
                        && hssfRow.getCell(cellNo++).toString().equals("邮箱")
                        && hssfRow.getCell(cellNo++).toString().equals("分机号")
                        && hssfRow.getCell(cellNo++).toString().equals("办公地点")
                        && hssfRow.getCell(cellNo++).toString().equals("备注")
                        && hssfRow.getCell(cellNo++).toString().equals("合同类型")
                        && hssfRow.getCell(cellNo++).toString().equals("音达认证")
                        && hssfRow.getCell(cellNo++).toString().equals("备注2")
                        && hssfRow.getCell(cellNo++).toString().equals("常驻地")
                        && hssfRow.getCell(cellNo++).toString().equals("社保地")
                        && hssfRow.getCell(cellNo++).toString().equals("分公司")
                        && hssfRow.getCell(cellNo++).toString().equals("户籍地")
                        && hssfRow.getCell(cellNo++).toString().equals("身份证号")
                        && hssfRow.getCell(cellNo++).toString().equals("网元")
                        && hssfRow.getCell(cellNo++).toString().equals("RSO认证")
                        && hssfRow.getCell(cellNo++).toString().equals("基本工资")
                        && hssfRow.getCell(cellNo++).toString().equals("项目工资")
                        && hssfRow.getCell(cellNo++).toString().equals("民族")
                        && hssfRow.getCell(cellNo++).toString().equals("年龄")
                        && hssfRow.getCell(cellNo++).toString().equals("最新合同")
                        && hssfRow.getCell(cellNo++).toString().equals("最新合同起始日期")
                        && hssfRow.getCell(cellNo++).toString().equals("最新合同结束日期")
                        && hssfRow.getCell(cellNo++).toString().equals("入职时间")
                        && hssfRow.getCell(cellNo++).toString().equals("工作年限")
                        && hssfRow.getCell(cellNo++).toString().equals("工资卡")
                        && hssfRow.getCell(cellNo++).toString().equals("毕业院校")
                        && hssfRow.getCell(cellNo++).toString().equals("最高学历")
                        && hssfRow.getCell(cellNo++).toString().equals("毕业日期")
                        && hssfRow.getCell(cellNo++).toString().equals("报销卡")
                        && hssfRow.getCell(cellNo++).toString().equals("项目")
                        && hssfRow.getCell(cellNo++).toString().equals("订单")
                        && hssfRow.getCell(cellNo++).toString().equals("员工状态")
                        && hssfRow.getCell(cellNo++).toString().equals("在职状态")
                        && hssfRow.getCell(cellNo++).toString().equals("离职日期")
                        ) {
                    // 如果验证通过了，就打印成功信息（额，要不然什么都不做的话显得不太好= =）
                    // sheetNo+1必须用括号括起来，否则+1会被认为是字符串拼接，在此再次感叹Java语法的强大！
                    System.out.println("表头校验成功！通过校验的表格页数 = "+(sheetNo+1));
                }
                else {
                    return "表头名称错误，与模板不相符";
                }
            } catch (NullPointerException e) {
                return "表头名称错误，与模板不相符";
            }
        }

        return "表头校验成功！";
    }

    /**
     * 循环地操作excel中的每一行数据
     * 如果审批编号相同，就更新数据，如果为新数据则插入
     * 为了方便，暂时将Map的格式统一为String+Object
     */
    public Map<String, Object> insertAndUpdate(String fileDir) throws IOException {
        Map<String, Object> mapInsert = new HashMap<String, Object>();
        List<StaffInfo> listFail = new ArrayList<StaffInfo>();

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
                第三步，对于不为空的行，看看员工UserId，部门，姓名，手机号，身份证号这5个字段是否为空
                如果为空的话，暂时定为在失败列表里随便添加一条数据
                 */
                if (hssfRow.getCell(0) == null
                        || hssfRow.getCell(1) == null
                        || hssfRow.getCell(3) == null
                        || hssfRow.getCell(7) == null
                        || hssfRow.getCell(19) == null
                        ) {
                    listFail.add(new StaffInfo());
                    continue;
                }

                /*
                第四步，对于不为空的行，将数据注入引用过来的实体对象
                在完成后面的函数之后，再对变量自加，方便下一行的判断。再玩一把火！
                 */
                int cellNo = 0;
                StaffInfo staffInfo = new StaffInfo();
                if (hssfRow.getCell(cellNo) != null) staffInfo.setStaffUserId(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setDepartment(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setPosition(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setName(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setSex(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setStaffId(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setWhetherLeader(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setCellphone(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setEmail(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setBranchPhone(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setWorkAddress(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setComment1(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setContractType(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setYindaIdentify(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setComment2(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setOrdinaryAddress(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setSocialSecurityAddress(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setBranchCompany(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setHouseholdAddress(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setIdNo(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setNetUnit(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setRsoIdentify(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setBaseSalary(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setItemSalary(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setNation(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setAge(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setLastContract(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setLastContractBegin(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setLastContractEnd(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setEnterTime(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setWorkYear(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setSalaryCard(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setGraduateSchool(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setSchoolRecord(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setGraduateDate(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setExpenseCard(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setItem(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setYoOrder(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setStaffState(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setWorkState(hssfRow.getCell(cellNo++).toString());
                if (hssfRow.getCell(cellNo) != null) staffInfo.setLeaveDate(hssfRow.getCell(cellNo++).toString());

                /*
                第五步，检查数据库中是否有相同的审批编号，如果没有，说明是一个新的条目，执行插入操作
                之所以有失败的可能性，是因为单元格内容有可能超过数据库长度
                 */
                String staffUserId = hssfRow.getCell(0).toString();
                StaffInfo staffInfo1 = staffInfoMapper.selectByPrimaryKey(staffUserId);

                if (staffInfo1 == null) {
                    try {
                        staffInfoMapper.insert(staffInfo);
                    } catch (Exception e) {
                        listFail.add(staffInfo);
                        continue;
                    }
                }
                /*
                第六步，如果有相同的编号，说明数据库中有元数据
                那么，就覆盖查询到的第一条数据
                同样，也有失败的可能性
                 */
                else {
                    try {
                        staffInfoMapper.updateByPrimaryKey(staffInfo);
                    } catch (Exception e) {
                        listFail.add(staffInfo);
                        continue;
                    }
                }

                // 到了这一步，说明插入或更新成功，数目自加！
                successAmount++;
            }
        }

        // for循环之后，把成功数目和失败列表返回到map
        mapInsert.put("successAmount", successAmount);
        mapInsert.put("listFail", listFail);
        System.out.println("successAmount = "+successAmount);
        System.out.println("listFail.size() = "+listFail.size());
        return mapInsert;
    }

}
