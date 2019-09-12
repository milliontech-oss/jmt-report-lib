package com.milliontech.circle.helper;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.model.Report;
import com.milliontech.circle.model.TableHeader;
import com.milliontech.circle.model.TableHeaderGroup;

public class TableHeaderHelper {

	public static void patchTableHeaderForExtractClass(ParameterData data, List list){
		if(list.size()==0 || StringUtils.isEmpty(data.getDataTableTitleListStr())){
			return;
		}
		
		String[] titles = StringUtils.split(data.getDataTableTitleListStr(), ",");
		TableHeader header = (TableHeader)list.get(list.size()-1);		
		if(header.isExtractClass() && header.isExtractHonz() && titles.length > list.size()){
			for(int i=0; i<titles.length-list.size(); i++){
				TableHeader newHeader = new TableHeader();
				newHeader.setAlign(header.getAlign());
				newHeader.setColumn(header.getColumn()+i+1);
				newHeader.setExtractClass(true);
				newHeader.setExtractHonz(true);				
				newHeader.setFormat(header.getFormat());
				newHeader.setMsgKey(null);
				newHeader.setTitle(titles[list.size()+i+1]);
				
				list.add(newHeader);
			}
			
			for(int i=0; i<titles.length; i++){				
				TableHeader tableHeader = (TableHeader)list.get(i);
				tableHeader.setTitle(titles[i]);
			}
		}
	}
	
	public static void setTitleForNoMessageKeyDefinied(ParameterData data, List list){
		if(list.size()==0 || StringUtils.isEmpty(data.getDataTableTitleListStr())){
			return;
		}
		
		String[] titles = StringUtils.split(data.getDataTableTitleListStr(), ",");		
		int index = 0;
		for(Iterator iter = list.iterator(); iter.hasNext();){
			Object obj = iter.next();
			if(obj instanceof TableHeader){
				
				TableHeader header = (TableHeader)obj;
				index = setTitleForTableHeader(titles, index, header);
				
			}
			
		}	
	}
	
	private static int setTitleForTableHeader(String[] titles, int index, TableHeader header) {		
		if(header.getMsgKey()==null && index < titles.length){
			header.setTitle(titles[index]);			
		}
		index++;
		return index;
	}
	
	public static void createTableHeaderForNoXmlDefinition(ParameterData data, Report report) {
		String[] tableTitleArray = StringUtils.split(data.getDataTableTitleListStr(),",");
		for(int i=0; i<tableTitleArray.length; i++){
			TableHeader h = new TableHeader();
			h.setColumn(i);
			h.setAlign(Constants.ALIGN_LEFT);
			h.setTitle(tableTitleArray[i]);
			report.getTableHeaderList().add(h);
		}
	}
	
	
}
