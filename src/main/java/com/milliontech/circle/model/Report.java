package com.milliontech.circle.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Report {
	
	private ReportSetting reportSetting;
	private List tableHeaderGroupList;
	private List tableHeaderList;
	private List summaryRowList;
	
	public Report(){
		reportSetting = new ReportSetting();
		tableHeaderGroupList = new ArrayList();
		tableHeaderList = new ArrayList();
		summaryRowList = new ArrayList();
	}
	
	public ReportSetting getReportSetting() {
		return reportSetting;
	}
	public void setReportSetting(ReportSetting reportSetting) {
		this.reportSetting = reportSetting;
	}
	public List getTableHeaderGroupList() {
		return tableHeaderGroupList;
	}
	public void setTableHeaderGroupList(List tableHeaderGroupList) {
		this.tableHeaderGroupList = tableHeaderGroupList;
	}
	public List getTableHeaderList() {
		return tableHeaderList;
	}
	public void setTableHeaderList(List tableHeaderList) {
		this.tableHeaderList = tableHeaderList;
	}
	public List getSummaryRowList() {
		return summaryRowList;
	}
	public void setSummaryRowList(List summaryRowList) {
		this.summaryRowList = summaryRowList;
	}
	
	public List getFullTableHeaderList(){
		List list = new ArrayList();
		list.addAll(tableHeaderList);
		for(Iterator iter = tableHeaderGroupList.iterator(); iter.hasNext();){
			TableHeaderGroup group = (TableHeaderGroup)iter.next();
			list.addAll(group.getFullTableHeaderList());
		}
		Collections.sort(list);
		return list;
	}
	
	public int numOfColumns(){
		if(!tableHeaderList.isEmpty()){
			return tableHeaderList.size();
		}else{
			int total = 0;
			for(Iterator iter = this.getTableHeaderGroupList().iterator(); iter.hasNext();){
				TableHeaderGroup group = (TableHeaderGroup)iter.next();
				total += group.numOfColumns();
			}
			return total;
		}		
	}
	
}
