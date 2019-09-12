package com.milliontech.circle.xml.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.exception.MTReportException;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.helper.MessageHelper;
import com.milliontech.circle.model.TableHeader;

public class TableHeaderXmlNodeConverter implements XmlNodeConverter{

	private static final Logger log = LoggerFactory.getLogger(TableHeaderXmlNodeConverter.class);

	public void convertAndAddToList(List dataList, Element elmt, Map parameter, ParameterData data) {
		TableHeader header = new TableHeader();
		this.convertToObject(header, elmt, parameter, data);
		if(header.isExtractClass() && header.isExtractHonz() && header.getMsgKey()!=null){
			List<String> titleList = new ArrayList<String>();
			
			if (StringUtils.isNotEmpty(header.getTitle())){
				String[] titles = StringUtils.split(header.getTitle(),",");
				if(titles != null && titles.length > 0){
					titleList = Arrays.asList(titles);
				}
			} else {
				Object value = parameter.get(header.getMsgKey());
				if(value != null && value instanceof List<?>){
					titleList.addAll((List<String>) value);
				}
			}
			
			TableHeader current = null;
			for (int i=0; i<titleList.size(); i++){
				if(i==0){
					current = header;
				}else{
					TableHeader subheader = new TableHeader();
					convertToObject(subheader, elmt, parameter, data);
					current = subheader;
				}
				if(current.getProperty()!=null && current.getProperty().contains(".")){
					String[] ps = StringUtils.split(current.getProperty(),".");
					current.setProperty(ps[0]+"["+i+"]."+ps[1]);
					if(ps.length>2){
						log.warn("Cannot support extra class more than 2 level. Cannot guess how many level apply to the titles.");
					}
					current.setMethod("get"+ps[0].substring(0,1).toUpperCase()+ps[0].substring(1));
				}
				current.setSubColumn(i);
				current.setTitle(titleList.get(i));
				dataList.add(current);
			}
			
		}else{
		    if (header.getColumn() == -1) {
		       header.setColumn(dataList.size()); 
		    }
			dataList.add(header);
		}
	}

	public void convertToObject(Object object, Element elmt, Map parameter, ParameterData data) {
		TableHeader header = (TableHeader)object;
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
		header.setWrapText(DataHelper.isTrue(elmt.getAttribute("wrapText"), false));
		header.setHidden(DataHelper.isTrue(elmt.getAttribute("hidden"), false));

		MessageHelper.setTitleByMessageKey(parameter, header);

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
