package com.milliontech.circle.xml.reader;

import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.milliontech.circle.constants.ParameterDataConstants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.helper.TableHeaderHelper;
import com.milliontech.circle.model.Report;
import com.milliontech.circle.model.ReportSetting;
import com.milliontech.circle.xml.converter.XmlNodeConverter;

public class XmlReader {

    private static final Logger log = LoggerFactory.getLogger(XmlReader.class);

    public Report readXmlToReportObject(Map parameter, ParameterData data, Class clazz) {
        Report report = new Report();
        String xmlPath = clazz.getName().replace('.', '/');
        if(data.getRemapXmlPath()!=null){
            xmlPath = data.getRemapXmlPath();
        }
        xmlPath = "/" + xmlPath + ".xml";
        log.info("Process Report Xml : "+xmlPath);

        try (InputStream in = XmlReader.class.getResourceAsStream(xmlPath)){
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(in);
            doc.getDocumentElement().normalize();

            boolean hasReportSettingNode = false;

            for(int i=0; i<doc.getDocumentElement().getChildNodes().getLength(); i++){
                Node node = doc.getDocumentElement().getChildNodes().item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    XmlNodeConverter converter = XmlReaderNodeConverterRepository.getConverterByName(node.getNodeName());

                    if("TableHeader".equalsIgnoreCase(node.getNodeName())){

                        converter.convertAndAddToList(report.getTableHeaderList(), (Element)node, parameter, data, clazz);

                        //Collections.sort(report.getTableHeaderList());
                        TableHeaderHelper.patchTableHeaderForExtractClass(data, report.getTableHeaderList());
                        TableHeaderHelper.setTitleForNoMessageKeyDefinied(data, report.getTableHeaderList());

                    }else if("TableHeaderGroup".equalsIgnoreCase(node.getNodeName())){

                        converter.convertAndAddToList(report.getTableHeaderGroupList(), (Element)node, parameter, data, null);
                        TableHeaderHelper.setTitleForNoMessageKeyDefinied(data, report.getFullTableHeaderList());

                    }else if("SummaryRow".equalsIgnoreCase(node.getNodeName())){

                        converter.convertAndAddToList(report.getSummaryRowList(), (Element)node, parameter, data, null);

                    }else if("ReportSetting".equalsIgnoreCase(node.getNodeName())){

                        converter.convertToObject(report.getReportSetting(), (Element)node, parameter, data);
                        hasReportSettingNode = true;
                    }
                }
            }

            if(report.getTableHeaderList().isEmpty() && report.getTableHeaderGroupList().isEmpty()){
                TableHeaderHelper.createTableHeaderForNoXmlDefinition(data, report);
            }

            /*
             * in general this should be configured directly via `ReportSetting` node
             * legacy project implemented before 2012 does not have `ReportSetting` node defined the XML file
             * this if-clause is to "patch" the `ReportSetting` from various parameter data map
             * e.g. CTMS, BSFE, CSMS
             */
            if (!hasReportSettingNode){
                ReportSetting setting = report.getReportSetting();
                
                if (parameter.containsKey(ParameterDataConstants.PDF_DEFAULT_SHOW_ROW_NO)) {
                    setting.setShowRowNo(DataHelper.isTrue((String)parameter.get(ParameterDataConstants.PDF_DEFAULT_SHOW_ROW_NO), true));
                }
                if (parameter.containsKey(ParameterDataConstants.EXCEL_DEFAULT_CELL_WRAP_TEXT)) {
                    setting.setExcelDefaultCellWrapText(DataHelper.isTrue((String)parameter.get(ParameterDataConstants.EXCEL_DEFAULT_CELL_WRAP_TEXT), false));
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return report;
    }

}
