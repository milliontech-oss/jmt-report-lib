package com.milliontech.circle.constants;

import java.util.ArrayList;
import java.util.List;

public class ReportType {

	public static final ReportType PDF;
	public static final ReportType EXCEL;
	public static final ReportType XLS;
	public static final ReportType XLSX;
	public static final ReportType SXSSF;
	public static final ReportType CSV;

	private static final List<ReportType> reportTypeList;

	static {
		PDF = new ReportType("PDF");
		EXCEL = new ReportType("EXCEL");
		XLS = new ReportType("XLS");
		XLSX = new ReportType("XLSX");
		SXSSF = new ReportType("SXSSF");
		CSV = new ReportType("CSV");

		reportTypeList = new ArrayList<ReportType>();
		reportTypeList.add(PDF);
		reportTypeList.add(EXCEL);
		reportTypeList.add(XLS);
		reportTypeList.add(XLSX);
		reportTypeList.add(SXSSF);
		reportTypeList.add(CSV);
	}

	private final String type;

	public ReportType(String type){
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static ReportType getReportType(String type){
		for (ReportType rt : reportTypeList) {
			if(rt.getType().equalsIgnoreCase(type)){
				return rt;
			}
		}
		return null;
	}

}
