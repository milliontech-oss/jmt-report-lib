package com.milliontech.circle.xml.converter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.helper.MessageHelper;
import com.milliontech.circle.model.TableHeaderGroup;

public class TableHeaderGroupXmlNodeConverter implements XmlNodeConverter{
	
	public void convertAndAddToList(List dataList, Element elmt, Map parameter, ParameterData data) {
		TableHeaderGroup grp = new TableHeaderGroup();
		this.convertToObject(grp, elmt, parameter, data);		
		dataList.add(grp);
	}

	public void convertToObject(Object object, Element elmt, Map parameter, ParameterData data) {
		TableHeaderGroup grp = (TableHeaderGroup)object;
		
		if(elmt.hasAttribute("title")){
			grp.setTitle(DataHelper.getString(elmt.getAttribute("title"), ""));
		}
		
		if(elmt.hasAttribute("column")){
			grp.setColumn(DataHelper.getInteger(elmt.getAttribute("column"), 999));
		}
		
		if(elmt.hasAttribute("msgKey")){
			grp.setMsgKey(DataHelper.getString(elmt.getAttribute("msgKey"), ""));
			MessageHelper.setTitleByMessageKey(parameter, grp);
		}
		
		for(int i=0; i<elmt.getChildNodes().getLength();i++){
			Node node = elmt.getChildNodes().item(i);
			
			if("TableHeaderGroup".equals(node.getNodeName())){
				
				convertAndAddToList(grp.getTableHeaderGroupList(), (Element)node, parameter, data);
				
			}else if("TableHeader".equals(node.getNodeName())){
				
				TableHeaderXmlNodeConverter converter = new TableHeaderXmlNodeConverter();
				converter.convertAndAddToList(grp.getTableHeaderList(), (Element)node, parameter, data);				
				Collections.sort(grp.getTableHeaderList());				
				
			}
			
		}	
	}

}
