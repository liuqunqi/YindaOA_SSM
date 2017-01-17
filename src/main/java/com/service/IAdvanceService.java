package com.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.model.YoAdvance;

public interface IAdvanceService {
	
	/**
	 * poi实现excel导入
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	public List<String []> readExcel(InputStream is) throws IOException;
	
	/**
	 * 预付款界面展示条件查询
	 * @param advance
	 * @return
	 */
	public List<YoAdvance> search(YoAdvance advance);
	/**
	 * 预付款导出
	 * @param advanceList
	 * @param excelHeader 表头
	 */
	public void advanceExport(List<YoAdvance> advanceList,String []excelHeader,HttpServletResponse response);
	/**
	 * 预付款导出查询--下载
	 * @param advance
	 * @return
	 */
	public List<YoAdvance> searchForExport(YoAdvance advance);
    
}