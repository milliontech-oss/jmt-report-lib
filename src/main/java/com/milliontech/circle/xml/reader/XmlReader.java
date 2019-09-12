package com.milliontech.circle.xml.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.TableHeaderHelper;
import com.milliontech.circle.model.Report;
import com.milliontech.circle.xml.converter.ReportSettingXmlNodeConverter;
import com.milliontech.circle.xml.converter.SummaryRowXmlNodeConverter;
import com.milliontech.circle.xml.converter.TableHeaderGroupXmlNodeConverter;
import com.milliontech.circle.xml.converter.TableHeaderXmlNodeConverter;
import com.milliontech.circle.xml.converter.XmlNodeConverter;

public class XmlReader {

	private static final Logger log = LoggerFactory.getLogger(XmlReader.class);
	public static final String BASE_PATH ="com.milliontech.test";
	
	private Map xmlNodeConverterMap;
	
	public XmlReader(){
		xmlNodeConverterMap = new HashMap();
		xmlNodeConverterMap.put("TableHeader", new TableHeaderXmlNodeConverter());
		xmlNodeConverterMap.put("TableHeaderGroup", new TableHeaderGroupXmlNodeConverter());
		xmlNodeConverterMap.put("SummaryRow", new SummaryRowXmlNodeConverter());
		xmlNodeConverterMap.put("ReportSetting", new ReportSettingXmlNodeConverter());		
	}
	
	public Report readXmlToReportObject(Map parameter, ParameterData data, Class clazz) throws ParserConfigurationException, SAXException, IOException{
		Report report = new Report();				
		if(clazz!=null){
			String xmlPath = clazz.getName().replace('.', '/');
			if(data.getRemapXmlPath()!=null){
				xmlPath = data.getRemapXmlPath();
			}
			xmlPath = "/" + xmlPath + ".xml";
			log.info("Process Report Xml : "+xmlPath);			
			
			InputStream in = XmlReader.class.getResourceAsStream(xmlPath);			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(in);
			doc.getDocumentElement().normalize();
		
			for(int i=0; i<doc.getDocumentElement().getChildNodes().getLength(); i++){
				Node node = doc.getDocumentElement().getChildNodes().item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE){
					XmlNodeConverter converter = (XmlNodeConverter)xmlNodeConverterMap.get(node.getNodeName());
					
					if("TableHeader".equalsIgnoreCase(node.getNodeName())){
						
						converter.convertAndAddToList(report.getTableHeaderList(), (Element)node, parameter, data);
						
						Collections.sort(report.getTableHeaderList());
						TableHeaderHelper.patchTableHeaderForExtractClass(data, report.getTableHeaderList());
						TableHeaderHelper.setTitleForNoMessageKeyDefinied(data, report.getTableHeaderList());
						
					}else if("TableHeaderGroup".equalsIgnoreCase(node.getNodeName())){
						
						converter.convertAndAddToList(report.getTableHeaderGroupList(), (Element)node, parameter, data);
						TableHeaderHelper.setTitleForNoMessageKeyDefinied(data, report.getFullTableHeaderList());
						
					}else if("SummaryRow".equalsIgnoreCase(node.getNodeName())){
						
						converter.convertAndAddToList(report.getSummaryRowList(), (Element)node, parameter, data);
						
					}else if("ReportSetting".equalsIgnoreCase(node.getNodeName())){
						
						converter.convertToObject(report.getReportSetting(), (Element)node, parameter, data);
					}
				}					
			}
			
//			if(!clazz.getName().startsWith(ReportPreprocessorImpl.BASE_PATH)){
//				report.getSummaryRowList().clear();
//				report.getTableHeaderGroupList().clear();
//				report.getTableHeaderList().clear();
//				report.setReportSetting(null);
//			}
			
		}
		
		if(report.getTableHeaderList().isEmpty() && report.getTableHeaderGroupList().isEmpty()){
			TableHeaderHelper.createTableHeaderForNoXmlDefinition(data, report);			
		}
		
		return report;
	}
	
}
