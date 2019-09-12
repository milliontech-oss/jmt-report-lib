package com.milliontech.circle.custom;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.milliontech.circle.data.model.ParameterData;

public interface CustomExcelCriteria {

	public int writeCriteriaAndReturnRowNum(ParameterData data, Workbook wb, Sheet dataSheet, int currentRowNum, CellStyle defaultStyle);
	
}
