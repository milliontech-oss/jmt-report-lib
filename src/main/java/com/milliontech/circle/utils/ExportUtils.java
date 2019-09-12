package com.milliontech.circle.utils;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milliontech.circle.constants.ReportType;
import com.milliontech.circle.data.converter.ParameterDataConverter;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.model.Report;
import com.milliontech.circle.model.SummaryRow;
import com.milliontech.circle.writer.MTCsvWriter;
import com.milliontech.circle.writer.MTExcelWriter;
import com.milliontech.circle.writer.MTPdfWriter;
import com.milliontech.circle.xml.reader.XmlReader;

public class ExportUtils {

	private static final Logger log = LoggerFactory.getLogger(ExportUtils.class);

	public static void exportReport(List dataList, Map parameter ,OutputStream out, ReportType reportType){
		Class clazz = null;
		if(!dataList.isEmpty()){
			clazz = dataList.get(0).getClass();
		}
		ExportUtils.exportReport(dataList, parameter, out, reportType, clazz, null);
	}

	public static void exportReport(List dataList, Map parameter ,OutputStream out, ReportType reportType, Class clazz){
		ExportUtils.exportReport(dataList, parameter, out, reportType, clazz, null);
	}

	public static void exportReport(List dataList, Map parameter ,OutputStream out, ReportType reportType, Class clazz, Workbook templateExcel){
		ParameterData data = ParameterDataConverter.convertToParameterData(parameter);
		Report report = null;

		XmlReader reader = new XmlReader();
		try{
			report = reader.readXmlToReportObject(parameter, data, clazz);
			ParameterDataConverter.convertToParameterDataByReportSetting(data, parameter, report.getReportSetting());
		}catch(Exception e){
			log.error("Read XML Error : ",e);
		}

		for(Iterator iter = report.getSummaryRowList().iterator(); iter.hasNext();){
			SummaryRow sRow = (SummaryRow)iter.next();
			if(sRow.getKey()!=null){
				data.getSummaryRowDataMap().put(sRow.getKey(), parameter.get(sRow.getKey()));
			}
		}

		try{
			if(ReportType.PDF.getType().equalsIgnoreCase(reportType.getType())){

				MTPdfWriter pdfWriter = new MTPdfWriter(data, report.getReportSetting());
				pdfWriter.writer(dataList, data, report, out);

			}else if(ReportType.EXCEL.getType().equalsIgnoreCase(reportType.getType())
					|| ReportType.XLS.getType().equalsIgnoreCase(reportType.getType())
					|| ReportType.XLSX.getType().equalsIgnoreCase(reportType.getType())
					|| ReportType.SXSSF.getType().equalsIgnoreCase(reportType.getType())){

				MTExcelWriter excelWriter;
				if(templateExcel!=null){
					excelWriter = new MTExcelWriter(data, report.getReportSetting(), templateExcel);
				}else{
					excelWriter = new MTExcelWriter(data, report.getReportSetting(), reportType);
				}
				excelWriter.writer(dataList, data, report, out);

			}else if(ReportType.CSV.getType().equalsIgnoreCase(reportType.getType())){

				MTCsvWriter csvWriter = new MTCsvWriter();
				csvWriter.writer(dataList, data, report, out);

			}
		}catch(Exception e){
			log.error("Writer Report Error : ",e);
		}


	}
}
