package com.milliontech.circle.xml.reader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milliontech.circle.xml.converter.AggregateCellXmlNodeConverter;
import com.milliontech.circle.xml.converter.ObjectCellXmlNodeConverter;
import com.milliontech.circle.xml.converter.RangeCellXmlNodeConverter;
import com.milliontech.circle.xml.converter.ReportSettingXmlNodeConverter;
import com.milliontech.circle.xml.converter.SummaryRowXmlNodeConverter;
import com.milliontech.circle.xml.converter.TableHeaderGroupXmlNodeConverter;
import com.milliontech.circle.xml.converter.TableHeaderXmlNodeConverter;
import com.milliontech.circle.xml.converter.XmlNodeConverter;

public class XmlReaderNodeConverterRepository {
    
    private static final Logger log = LoggerFactory.getLogger(XmlReaderNodeConverterRepository.class);

    private static final Map<String, XmlNodeConverter<?>> xmlNodeConverterMap;
    
    static {
        Map<String, XmlNodeConverter<?>> map = new HashMap<String, XmlNodeConverter<?>>();
        map.put("TableHeader", new TableHeaderXmlNodeConverter());
        map.put("TableHeaderGroup", new TableHeaderGroupXmlNodeConverter());
        map.put("SummaryRow", new SummaryRowXmlNodeConverter());
        map.put("ReportSetting", new ReportSettingXmlNodeConverter());
        map.put("RangeCell", new RangeCellXmlNodeConverter());
        map.put("ObjectCell", new ObjectCellXmlNodeConverter());
        map.put("AggregateCell", new AggregateCellXmlNodeConverter());
        xmlNodeConverterMap = Collections.unmodifiableMap(map);
    }
    
    public static XmlNodeConverter<?> getConverterByName(String name){
        XmlNodeConverter<?> converter = xmlNodeConverterMap.get(name);
        if (converter == null) {
            throw new RuntimeException("Unknown converter name: " + name);
        }
        return converter;
    }
    
    public static XmlNodeConverter<?> getConverterByType(Class<?> type){
        for (XmlNodeConverter<?> converter : xmlNodeConverterMap.values()) {
            if(converter.getClass().equals(type)) {
                return converter;
            }
        }
        throw new RuntimeException("Unknown converter type: " + type.getName());
    }
    
}
