package com.milliontech.circle.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SummaryRow{
	
	private String key;	
	private boolean hightlight;
	private List rangeCellList;
	private List aggregateCellList;
	private List objectCellList;	
	private Map map;
	
	public SummaryRow(){
		rangeCellList = new ArrayList();
		aggregateCellList = new ArrayList();
		objectCellList = new ArrayList();
		map = new HashMap();
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}		
	public List getRangeCellList() {
		return rangeCellList;
	}
	public void setRangeCellList(List rangeCellList) {
		this.rangeCellList = rangeCellList;
	}
	public List getAggregateCellList() {
		return aggregateCellList;
	}
	public void setAggregateCellList(List aggregateCellList) {
		this.aggregateCellList = aggregateCellList;
	}
	public List getObjectCellList() {
		return objectCellList;
	}
	public void setObjectCellList(List objectCellList) {
		this.objectCellList = objectCellList;
	}			
	public boolean isHightlight() {
		return hightlight;
	}
	public void setHightlight(boolean hightlight) {
		this.hightlight = hightlight;
	}
	
	public Map getAggregateCellMap(){
		if(!getAggregateCellList().isEmpty() && map.isEmpty()){
			for(Iterator iter = this.getAggregateCellList().iterator(); iter.hasNext();){
				AggregateCell cell = (AggregateCell)iter.next();
				map.put(new Integer(cell.getColumn()), cell);
			}
		}		
		return map;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
