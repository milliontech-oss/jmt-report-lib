package com.milliontech.circle.xml.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.model.SummaryRow;

public class SummaryRowXmlNodeConverter implements XmlNodeConverter{
	
	private Map xmlNodeConverterMap;
	
	public SummaryRowXmlNodeConverter(){
		xmlNodeConverterMap = new HashMap();
		xmlNodeConverterMap.put("RangeCell", new RangeCellXmlNodeConverter());
		xmlNodeConverterMap.put("ObjectCell", new ObjectCellXmlNodeConverter());
		xmlNodeConverterMap.put("AggregateCell", new AggregateCellXmlNodeConverter());
	}
	
	public void convertAndAddToList(List list, Element elmt, Map parameter, ParameterData data) {
		SummaryRow row = new SummaryRow();
		this.convertToObject(row, elmt, parameter, data);	
		list.add(row);
	}

	public void convertToObject(Object object, Element elmt, Map parameter, ParameterData data) {
		SummaryRow row = (SummaryRow)object;
		if(elmt.hasAttribute("key")){
			row.setKey(DataHelper.getString(elmt.getAttribute("key"), ""));
		}
		
		if(elmt.hasAttribute("hightlight")){
			row.setHightlight(DataHelper.isTrue(elmt.getAttribute("hightlight"), false));
		}
		
		for(int i=0; i<elmt.getChildNodes().getLength();i++){
			Node node = elmt.getChildNodes().item(i);
			XmlNodeConverter converter = (XmlNodeConverter)xmlNodeConverterMap.get(node.getNodeName());			
			if("RangeCell".equalsIgnoreCase(node.getNodeName())){				
				converter.convertAndAddToList(row.getRangeCellList(), (Element)node, parameter, data);				
			}else if("ObjectCell".equalsIgnoreCase(node.getNodeName())){				
				converter.convertAndAddToList(row.getObjectCellList(), (Element)node, parameter, data);				
			}else if("AggregateCell".equalsIgnoreCase(node.getNodeName())){				
				converter.convertAndAddToList(row.getAggregateCellList(), (Element)node, parameter, data);
			}				
		}	
	}

}
