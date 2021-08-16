package com.milliontech.circle.xml.converter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.exception.MTReportException;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.helper.MessageHelper;
import com.milliontech.circle.model.Report;
import com.milliontech.circle.model.TableHeader;
import com.milliontech.circle.xml.reader.XmlReader;

public class TableHeaderXmlNodeConverter implements XmlNodeConverter<TableHeader>{

    private static final Logger log = LoggerFactory.getLogger(TableHeaderXmlNodeConverter.class);

    public TableHeader convertAndAddToList(List dataList, Element elmt, Map parameter, ParameterData data, Class clazz) {
        TableHeader header = new TableHeader();
        this.convertToObject(header, elmt, parameter, data);
        if (header.getColumn() == -1){
            header.setColumn(dataList.size()); 
        }
        
        if (header.isExtractHonz() && header.isExtractClass()) {
            List<TableHeader> childHeaders = handleExtractClassAndExtractHonzColumn(header, parameter, data, clazz);
            for (TableHeader childHeader : childHeaders) {
                childHeader.setColumn(header.getColumn());
                dataList.add(childHeader);
            }
            
        } else if (header.isExtractHonz()) {
            List<TableHeader> childHeaders = handleExtractHonzColumn(header, parameter, data, clazz);
            for (TableHeader childHeader : childHeaders) {
                childHeader.setColumn(header.getColumn());
                dataList.add(childHeader);
            }
            
        } else if (header.isExtractClass()) {
            List<TableHeader> childHeaders = handleExtractClassColumn(header, parameter, data, clazz);
            for (TableHeader childHeader : childHeaders) {
                childHeader.setColumn(header.getColumn());
                dataList.add(childHeader);
            }
        } else {
            dataList.add(header);
        }
        
        return header;
    }

    private List<String> createTitleListForSubHeader(TableHeader header, ParameterData data, Map parameter) {
        List<String> titleList = new ArrayList<String>();
        
        if (StringUtils.isNotEmpty(header.getTitle())){
            String[] titles = StringUtils.split(header.getTitle(),",");
            if(titles != null && titles.length > 0){
                titleList = Arrays.asList(titles);
            }
            return titleList;
        }
        
        // test twice for msgKey source
        boolean resolved = false;
        
        if (data.getMsgKeyPropMap() != null && !data.getMsgKeyPropMap().isEmpty()) {
            Object value = data.getMsgKeyPropMap().get(header.getMsgKey());
            if(value != null && value instanceof List<?>){
                titleList.addAll((List<String>) value);
                resolved = true;
            }
        }
        
        if (resolved) {
            return titleList;
        }
        
        Object value = parameter.get(header.getMsgKey());
        if(value != null && value instanceof List<?>){
            titleList.addAll((List<String>) value);
        }
        return titleList;
    }
    
    private Type discoverType(TableHeader header, Class clazz) {
        try {
            Method getter = null;
            if (StringUtils.isNotBlank(header.getProperty())) {
            	// cast the first parameter to String to workaround JDK issue: https://bugs.openjdk.java.net/browse/JDK-8212636
                PropertyDescriptor pd = new PropertyDescriptor((String)header.getProperty(), clazz);
                getter = pd.getReadMethod();
            } else {
                getter = clazz.getMethod(header.getMethod(), (Class) null);
            }
            Type type = getter.getGenericReturnType();
            return type;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<TableHeader> handleExtractClassColumn(TableHeader header, Map parameter, ParameterData data, Class clazz) {
        List<TableHeader> childHeaderList = new ArrayList<TableHeader>();
        Type type = discoverType(header, clazz);
        
        if (!(type instanceof Class)) {
            throw new UnsupportedOperationException("error in discoverring the class: " + type);
        }
        
        Report nestedReport = parseNestedReport(parameter, data, header, (Class) type);
        List<TableHeader> nestedHeaders = (List<TableHeader>) nestedReport.getTableHeaderList();
        Collections.sort(nestedHeaders);
        
        List<String> titleList = createTitleListForSubHeader(header, data, parameter);
        
        for (int i=0; i<nestedHeaders.size(); ++i) {
            TableHeader nestedHeader = nestedHeaders.get(i);
            if (StringUtils.isNotBlank(nestedHeader.getMethod())) {
                if (StringUtils.isBlank(header.getMethod())) {
                    throw new IllegalArgumentException("parent and nested report must use same method or property access path: " + header.getProperty());
                }
                nestedHeader.setMethod(String.format("%s.%s", header.getMethod(), nestedHeader.getMethod()));
            } else {
                if (StringUtils.isBlank(header.getProperty())) {
                    throw new IllegalArgumentException("parent and nested report must use same method or property access path: " + header.getProperty());
                }
                nestedHeader.setProperty(String.format("%s.%s", header.getProperty(), nestedHeader.getProperty()));
            }
            
            nestedHeader.setTitle(titleList.get(i));
            nestedHeader.setSubColumn(nestedHeader.getColumn());
            nestedHeader.setColumn(-1);
            childHeaderList.add(nestedHeader);
        }
        return childHeaderList;
    }
    
    private List<TableHeader> handleExtractHonzColumn(TableHeader header, Map parameter, ParameterData data, Class clazz) {
        List<TableHeader> childHeaderList = new ArrayList<TableHeader>();
        String[] ps = StringUtils.split(header.getProperty(),".");
        if (ps.length>2) {
            log.warn("Cannot support extra class more than 2 level. Cannot guess how many level apply to the titles.");
        }
        
        List<String> titleList = createTitleListForSubHeader(header, data, parameter);
        
        for (int i=0; i<titleList.size(); ++i) {
            TableHeader subHeader = new TableHeader();
            jodd.bean.BeanCopy.from(header).to(subHeader).copy();
            
            subHeader.setProperty(String.format("%s[%s].%s", ps[0], i, ps[1]));
            subHeader.setTitle(titleList.get(i));
            subHeader.setSubColumn(i);
            subHeader.setColumn(-1);
            childHeaderList.add(subHeader);
        }
        
        return childHeaderList;
    }
    
    private List<TableHeader> handleExtractClassAndExtractHonzColumn(TableHeader header, Map parameter, ParameterData data, Class clazz) {
        List<TableHeader> childHeaderList = new ArrayList<TableHeader>();
        Type type = discoverType(header, clazz);
        
        if (!(type instanceof ParameterizedType)) {
            throw new RuntimeException("currently only support generic java.util.List: " + type);
        }
        ParameterizedType pt = (ParameterizedType) type;
        if (!java.util.List.class.isAssignableFrom((Class)pt.getRawType())) {
            throw new RuntimeException("currently only support java.util.List: " + type);
        }
        
        Class nestedType = (Class) pt.getActualTypeArguments()[0];
        Report nestedReport = parseNestedReport(parameter, data, header, (Class) nestedType);
        List<TableHeader> nestedHeaders = (List<TableHeader>) nestedReport.getTableHeaderList();
        Collections.sort(nestedHeaders);
        
        List<String> titleList = createTitleListForSubHeader(header, data, parameter);
        
        /* the subtitle list will determine the no. of column exported */ 
        int numOfInstance = titleList.size() / nestedHeaders.size();
        int titleCounter = 0;
        for (int i=0; i<numOfInstance; i++){
            for (TableHeader nestedHeader : nestedHeaders) {
                TableHeader subHeader = new TableHeader();
                jodd.bean.BeanCopy.from(nestedHeader).to(subHeader).copy();
                
                if (StringUtils.isBlank(header.getProperty())){
                    throw new RuntimeException("property must not be blank for extractHonz: " + ReflectionToStringBuilder.toString(header));
                }
                if (StringUtils.isBlank(nestedHeader.getProperty())){
                    throw new RuntimeException("property must not be blank for extractHonz: " + ReflectionToStringBuilder.toString(nestedHeader));
                }
                subHeader.setProperty(String.format("%s[%s].%s", header.getProperty(), i, nestedHeader.getProperty()));
                
                subHeader.setTitle(titleList.get(titleCounter));
                subHeader.setSubColumn(titleCounter);
                subHeader.setColumn(-1);
                childHeaderList.add(subHeader);
                ++titleCounter;
            }
        }
        
        return childHeaderList;
    }

    private Report parseNestedReport(Map parameter, ParameterData data, TableHeader rootColumnHeader, Class nestedClass) {
        XmlReader reader = new XmlReader();
        ParameterData cloneData = new ParameterData();
        jodd.bean.BeanCopy.from(data).to(cloneData).copy();
        cloneData.setRemapXmlPath(rootColumnHeader.getXmlRemap());
        
        Report nestedReport = reader.readXmlToReportObject(parameter, cloneData, nestedClass);
        return nestedReport;
    }
    

    public void convertToObject(TableHeader header, Element elmt, Map parameter, ParameterData data) {
        header.setColumn(DataHelper.getInteger(elmt.getAttribute("column"), -1));
        header.setWidth(DataHelper.getInteger(elmt.getAttribute("width"), -1));
        header.setPdfWidth(DataHelper.getFloat(elmt.getAttribute("pdfWidth"), 0f));
        header.setXlsWidth(DataHelper.getInteger(elmt.getAttribute("xlsWidth"), -1));
        header.setTitle(DataHelper.getString(elmt.getAttribute("title"), ""));
        header.setMethod(DataHelper.getString(elmt.getAttribute("method"), null));
        header.setProperty(DataHelper.getString(elmt.getAttribute("property"), null));
        if(elmt.hasAttribute("align")){
            header.setAlign(DataHelper.getString(elmt.getAttribute("align"), Constants.ALIGN_LEFT));
        }
        header.setFormat(DataHelper.getString(elmt.getAttribute("format"), null));
        header.setMsgKey(DataHelper.getString(elmt.getAttribute("msgKey"), null));
        header.setExtractClass(DataHelper.isTrue(elmt.getAttribute("extractClass"), false));
        header.setExtractHonz(DataHelper.isTrue(elmt.getAttribute("extractHonz"), false));

        header.setMethod(this.getReplaceLangValue(header.getMethod(), data.getLang()));
        header.setProperty(this.getReplaceLangValue(header.getProperty(), data.getLang()));
        if(elmt.hasAttribute("mergeIfSame")){
            header.setMergeIfSame(DataHelper.isTrue(elmt.getAttribute("mergeIfSame"), false));
            if(elmt.hasAttribute("mergeKey")){
                header.setMergeKey(elmt.getAttribute("mergeKey"));
            }

        }
        header.setMultiTableDynFld(DataHelper.isTrue(elmt.getAttribute("multiTableDynFld"), false));
        header.setHighlightFld(DataHelper.getString(elmt.getAttribute("highlightFld"), null));
        header.setHighlightColor(DataHelper.getString(elmt.getAttribute("highlightColor"), null));

        header.setItalicsFld(DataHelper.getString(elmt.getAttribute("italicsFld"), null));

        if(data.isUseWidthIfEmpty()){
            if(header.getWidth()> 0 && header.getPdfWidth() <=0 ){
                header.setPdfWidth(header.getWidth());
            }

            if(header.getWidth()> 0 && header.getXlsWidth() <=0 ){
                header.setXlsWidth(header.getWidth());
            }
        }
        if (elmt.hasAttribute("wrapText")) {
            header.setWrapText(DataHelper.isTrue(elmt.getAttribute("wrapText"), false));
        }
        header.setHidden(DataHelper.isTrue(elmt.getAttribute("hidden"), false));
        
        header.setXmlRemap(DataHelper.getString(elmt.getAttribute("xmlRemap"), null));

        MessageHelper.setTitleByMessageKey(parameter, data, header);

        if(header.getMethod()==null && header.getProperty()==null){
            throw new MTReportException("Column ["+header.getColumn()+"] does not set method/property. Please check config file.");
        }
    }

    private String getReplaceLangValue(String value, String lang){
        if(StringUtils.contains(value, "${") && StringUtils.contains(value, "}")){
            String r = StringUtils.substringBetween(value, "${", "}");
            if(StringUtils.containsIgnoreCase(r, "LANG")){
                value = StringUtils.replace(value, "${"+r+"}", lang);
                //log.debug("replace value = "+value);
            }
        }
        return value;
    }

}
