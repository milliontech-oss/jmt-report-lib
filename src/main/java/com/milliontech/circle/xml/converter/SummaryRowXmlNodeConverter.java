package com.milliontech.circle.xml.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.model.SummaryRow;
import com.milliontech.circle.xml.reader.XmlReaderNodeConverterRepository;

public class SummaryRowXmlNodeConverter implements XmlNodeConverter<SummaryRow>{
	
	public SummaryRow convertAndAddToList(List list, Element elmt, Map parameter, ParameterData data, Class clazz) {
		SummaryRow row = new SummaryRow();
		this.convertToObject(row, elmt, parameter, data);	
		list.add(row);
		return row;
	}

	public void convertToObject(SummaryRow row, Element elmt, Map parameter, ParameterData data) {
		if(elmt.hasAttribute("key")){
			row.setKey(DataHelper.getString(elmt.getAttribute("key"), ""));
		}
		
		if(elmt.hasAttribute("hightlight")){
			row.setHightlight(DataHelper.isTrue(elmt.getAttribute("hightlight"), false));
		}
		
		for(int i=0; i<elmt.getChildNodes().getLength();i++){
			Node node = elmt.getChildNodes().item(i);
            if(node.getNodeType() != Node.ELEMENT_NODE){
                continue;
            }
			XmlNodeConverter converter = XmlReaderNodeConverterRepository.getConverterByName(node.getNodeName());
			List mutableList = null;
			
			if("RangeCell".equalsIgnoreCase(node.getNodeName())){
			    mutableList = row.getRangeCellList();
			}else if("ObjectCell".equalsIgnoreCase(node.getNodeName())){
			    mutableList = row.getObjectCellList();
			}else if("AggregateCell".equalsIgnoreCase(node.getNodeName())){
			    mutableList = row.getAggregateCellList();
			} else {
			    throw new RuntimeException("unknown summary row defined: " + node.getNodeName());
			}
			converter.convertAndAddToList(mutableList, (Element)node, parameter, data, null);
		}	
	}

}
