package com.milliontech.circle.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TableHeaderGroup implements Comparable, TableColumn, MessageKeyTitleElement{

	private int column;
	private int subColumn;
	private String title;
	private String msgKey;
	private List tableHeaderList;
	private List tableHeaderGroupList;
	private List fullTableHeaderList;	
	
	public TableHeaderGroup(){
		tableHeaderGroupList = new ArrayList();
		tableHeaderList = new ArrayList();
		column = 999;
		subColumn = 0;
	}

	public List getTableHeaderList() {
		return tableHeaderList;
	}

	public void setTableHeaderList(List tableHeaderList) {
		this.tableHeaderList = tableHeaderList;
	}

	public List getTableHeaderGroupList() {
		return tableHeaderGroupList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getSubColumn() {
		return subColumn;
	}

	public void setSubColumn(int subColumn) {
		this.subColumn = subColumn;
	}

	public String getMsgKey() {
		return msgKey;
	}

	public void setMsgKey(String msgKey) {
		this.msgKey = msgKey;
	}

	public List getFullTableHeaderList() {		
		fullTableHeaderList = new ArrayList();
		fullTableHeaderList.addAll(tableHeaderList);
		for(Iterator iter = tableHeaderGroupList.iterator(); iter.hasNext();){
			TableHeaderGroup group = (TableHeaderGroup)iter.next();
			fullTableHeaderList.addAll(group.getFullTableHeaderList());
		}
		return fullTableHeaderList;
	}
	
	public List getSortedFullList(){		
		List fullList = new ArrayList();
		fullList.addAll(getTableHeaderList());
		fullList.addAll(getTableHeaderGroupList());
		int i=0;
		for(Iterator fIter = fullList.iterator(); fIter.hasNext();){
			TableColumn column = (TableColumn)fIter.next();
			if(column.getColumn()==999){
				column.setColumn(i);
			}
			i++;
		}
		Collections.sort(fullList);
		return fullList;
	}

	public void setTableHeaderGroupList(List tableHeaderGroupList) {
		this.tableHeaderGroupList = tableHeaderGroupList;
	}
	
	public int numOfColumns(){
		int total = 0;
		for(Iterator iter = this.getTableHeaderGroupList().iterator(); iter.hasNext();){
			TableHeaderGroup group = (TableHeaderGroup)iter.next();
			total += group.numOfColumns();
		}
		return total + tableHeaderList.size();
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
	public int compareTo(Object o) {
		TableColumn h = (TableColumn)o;
		int value = this.getColumn()-h.getColumn();
		return value != 0 ? value : (this.getSubColumn()-h.getSubColumn());
	}
}
