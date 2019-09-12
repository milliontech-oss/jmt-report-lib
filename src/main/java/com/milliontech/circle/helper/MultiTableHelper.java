package com.milliontech.circle.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milliontech.circle.model.TableHeader;
import com.milliontech.circle.model.TableHeaderGroup;

import jodd.bean.BeanCopy;

public class MultiTableHelper {

	private static final Logger log = LoggerFactory.getLogger(MultiTableHelper.class);
	
	public void createTableHeaderForMultiTableDynFld(List tableHeaderGroupList, String splitBy) throws Exception{
		for(Iterator iter = tableHeaderGroupList.iterator(); iter.hasNext();){
			TableHeaderGroup grp = (TableHeaderGroup)iter.next();
						
			List newList = new ArrayList();
			for(Iterator tIter = grp.getTableHeaderList().iterator(); tIter.hasNext();){
				TableHeader header = (TableHeader)tIter.next();				
				if(header.isMultiTableDynFld()){
					float pdfWidth = header.getPdfWidth();
					String[] titles = StringUtils.split(header.getTitleMap().get(splitBy).toString(),",");
					TableHeader current = null;
					for(int i=0; i<titles.length; i++){
						if(i==0){								
							current = header;					
						}else{
							TableHeader subheader = new TableHeader();
							BeanCopy.from(header).to(subheader).copy();
							subheader.setMutiTableDummyFld(true);							
							current = subheader;							
						}	
						if(current.getProperty()!=null && current.getProperty().contains(".")){
							if(pdfWidth > 0){
								current.setPdfWidth(pdfWidth/titles.length);
							}
							String[] ps = StringUtils.split(current.getProperty(),".");
							if(i==0){
								current.setProperty(ps[0]+"["+i+"]."+ps[1]);
								if(ps.length>2){
									log.warn("Cannot support extra class more than 2 level. Cannot guess how many level apply to the titles.");
								}
								current.setMethod("get"+ps[0].substring(0,1).toUpperCase()+ps[0].substring(1));
							}else{
								current.setProperty(StringUtils.replace(current.getProperty(), "[0]", "["+i+"]"));
								current.setMethod(null);
							}
						}						
						current.setSubColumn(i);
						current.setTitle(titles[i]);
						newList.add(current);
					}					
				}else{
					newList.add(header);
				}
			}			
			grp.setTableHeaderList(newList);
			createTableHeaderForMultiTableDynFld(grp.getTableHeaderGroupList(), splitBy);
			
		}
	}
	
	public void resetTableHeaderForMultiTableDynFld(List tableHeaderGroupList, String splitBy) throws Exception{
		for(Iterator iter = tableHeaderGroupList.iterator(); iter.hasNext();){
			TableHeaderGroup grp = (TableHeaderGroup)iter.next();
						
			List newList = new ArrayList();
			for(Iterator tIter = grp.getTableHeaderList().iterator(); tIter.hasNext();){
				TableHeader header = (TableHeader)tIter.next();
				if(!header.isMutiTableDummyFld()){
					if(header.isMultiTableDynFld()){						
						if(StringUtils.contains(header.getProperty(), "[0]")){
							header.setProperty(StringUtils.replace(header.getProperty(), "[0]", ""));
						}						
					}
					newList.add(header);				
				}
			}
			
			grp.setTableHeaderList(newList);
			resetTableHeaderForMultiTableDynFld(grp.getTableHeaderGroupList(), splitBy);
			
		}
	}
	
}
