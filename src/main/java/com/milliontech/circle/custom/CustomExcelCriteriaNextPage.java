package com.milliontech.circle.custom;

import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.milliontech.circle.data.converter.ParameterDataConverter;
import com.milliontech.circle.data.model.CriteriaData;
import com.milliontech.circle.data.model.ParameterData;

public class CustomExcelCriteriaNextPage implements CustomExcelCriteria{

	public int writeCriteriaAndReturnRowNum(ParameterData data, Workbook wb, Sheet dataSheet, int currentRowNum, CellStyle defaultStyle){
		Sheet sheet = wb.createSheet("Criteria");
		List criteriaList = ParameterDataConverter.convertToCriteriaDataList(data);
		int index = 0;
		for (Iterator iter = criteriaList.iterator(); iter.hasNext();) {
			CriteriaData criteriaData = (CriteriaData)iter.next();
			Row row = sheet.createRow(index);
			index++;

			Cell cell0 = row.createCell(0);
			Cell cell1 = row.createCell(1);
			cell0.setCellValue(criteriaData.getName());			
			cell0.setCellStyle(defaultStyle);
			cell1.setCellValue(criteriaData.getValue());
			cell1.setCellStyle(defaultStyle);
			
		}
		
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		
		return 0;
	}
	
}
