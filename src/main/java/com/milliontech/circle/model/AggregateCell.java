package com.milliontech.circle.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AggregateCell {

	private int column;
	private String mode;
	private String format;
	private String align;
	private List calValList;
	
	public AggregateCell(){
		calValList = new ArrayList();
	}
	
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}			
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public List getCalValList() {
		return calValList;
	}
	public void setCalValList(List calValList) {
		this.calValList = calValList;
	}
	public List getNonEmptyValList(){
		List list = new ArrayList();
		for(Iterator iter = this.getCalValList().iterator(); iter.hasNext();){
			Object obj = iter.next();
			if(obj!=null){
				if(obj instanceof BigDecimal || obj instanceof Integer || obj instanceof Long){
					list.add(obj);	
				}
			}
		}
		return list;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
