package com.milliontech.circle.helper;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.constants.PaperSizeConstants;
import com.milliontech.circle.constants.PdfConstants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.model.ReportSetting;
import com.milliontech.circle.model.TableHeader;
import com.milliontech.circle.model.pdf.PdfStyle;

import jodd.bean.BeanUtil;

public class PdfHelper {

	public static void createPdfCell(PdfPTable table, String text, PdfStyle style, TableHeader header, int border, String align, BaseColor color){
		PdfHelper.createPdfCell(table, text, style, header, border, align, color, 1, 1, null);
	}

	public static void createPdfCell(PdfPTable table, String text, PdfStyle style, TableHeader header, int border, String align, BaseColor color, Float grey){
		PdfHelper.createPdfCell(table, text, style, header, border, align, color, 1, 1, grey);
	}

	public static void createPdfCell(PdfPTable table, String text, PdfStyle style, TableHeader header, int border, String align, BaseColor color, int colspan, int rowspan){
		PdfHelper.createPdfCell(table, text, style, header, border, align, color, colspan, rowspan, null);
	}

	public static void createPdfCell(PdfPTable table, String text, PdfStyle style, TableHeader header, int border, String align, BaseColor color, int colspan, int rowspan, Float grey){
		PdfPCell cell = new PdfPCell();
		Paragraph pv = PdfHelper.createDisplayParagraph(text, style.getFontList(), false, header);
		cell.setPhrase(pv);
		cell.setBorder(border);
		if(Constants.ALIGN_CENTER.equals(align)){
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		}else if(Constants.ALIGN_RIGHT.equals(align)){
			cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		}else{
			cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		}
		if(color!=null){
			cell.setBackgroundColor(color);
		}
		if(grey!=null){
			cell.setGrayFill(grey.floatValue());
		}
		if(colspan>1){
			cell.setColspan(colspan);
		}
		if(rowspan>1){
			cell.setRowspan(rowspan);
		}
		cell.setPadding(2);
		table.addCell(cell);
	}

	public static Object getCellValue(Class clazz, Object obj, String method, String property, Map methodValMapMap) throws Exception{
		Object value = null;
		
		if(method!=null){
			value = MethodUtils.invokeExactMethod(obj, method);
		}

		if(property!=null){
			try{
				value = BeanUtil.pojo.getProperty(obj, property);
			}catch(Exception e){}
		}

		if(methodValMapMap!=null && value!=null){
			value = DataHelper.getRemapValue(value, methodValMapMap, method, property);
		}

		return value;
	}

	public static boolean isHighlight(Class clazz, Object obj, String property){
		boolean result = false;
		if(property!=null && property.trim()!=null){
			try{
				Boolean b = (Boolean)PdfHelper.getCellValue(clazz, obj, null, property, null);
				if(b!=null){
					result = b.booleanValue();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
	}

	public static BaseColor getHighlightColor(String color){
		if("blue".equalsIgnoreCase(color)){
			return PdfConstants.HIGHLIGHT_BLUE_COLOR;
		}else if("yellow".equalsIgnoreCase(color)){
			return PdfConstants.HIGHLIGHT_YELLOW_COLOR;
		}else if("purple".equalsIgnoreCase(color)){
			return PdfConstants.HIGHLIGHT_PURPLE_COLOR;
		}else if("red".equalsIgnoreCase(color)){
			return PdfConstants.HIGHLIGHT_RED_COLOR;
		}else if("pink".equalsIgnoreCase(color)){
			return PdfConstants.HIGHLIGHT_PINK_COLOR;
		}else{
			return PdfConstants.HIGHLIGHT_GREEN_COLOR;
		}
	}

	public static String getCellStringValue(Object value, String format) throws Exception{
		if(value==null){
			value = "";
		}else if(value instanceof java.util.Date){
			Date d = (Date)value;
			value = format != null? DataHelper.formatDate(d, format) : DataHelper.formatDate(d, Constants.DEFAULT_DATE_FORMAT);
		}else if(value instanceof java.sql.Date){
			java.sql.Date d = (java.sql.Date)value;
			value = format != null? DataHelper.formatDate(d, format) : DataHelper.formatDate(d, Constants.DEFAULT_DATE_FORMAT);
		}else if(value instanceof Timestamp){
			Timestamp t = (Timestamp)value;
			value = format != null? DataHelper.formatDate(t, format) : DataHelper.formatDate(t, Constants.DEFAULT_TIMESTAMP_FORMAT);
		}else if(value instanceof Instant){
		    Instant i = (Instant)value;
		    value = format != null? DataHelper.formatInstant(i, format) : DataHelper.formatInstant(i, Constants.DEFAULT_TIMESTAMP_FORMAT);
		}else if(value instanceof BigDecimal){
			BigDecimal b = (BigDecimal)value;
			if(format!=null){
				DecimalFormat df = new DecimalFormat(format);
				value = df.format(b.doubleValue());
			}else{
				value = b.toString();
			}
		}else if(value instanceof Integer){
			if(format!=null){
				DecimalFormat df = new DecimalFormat(format);
				value = df.format(value);
			}else{
				value = String.valueOf(value);
			}
		}else if(value instanceof Long){
			if(format!=null){
				DecimalFormat df = new DecimalFormat(format);
				value = df.format(value);
			}else{
				value = String.valueOf(value);
			}
		}else if(value instanceof Double){
			if(format!=null){
				DecimalFormat df = new DecimalFormat(format);
				value = df.format(value);
			}else{
				value = String.valueOf(value);
			}
		}

		return (String)value;
	}

	public static Paragraph createDisplayParagraph(String value, Font font[], boolean underLine) {
		return PdfHelper.createDisplayParagraph(value, font, underLine, null);
	}

	public static Paragraph createDisplayParagraph(String value, Font font[], boolean underLine, TableHeader header) {
		if(value==null)
			return new Paragraph(new Chunk());
		Paragraph p = new Paragraph();
		Font prvFont = font[font.length - 1];
		Chunk chunk = new Chunk("");
		chunk.setFont(prvFont);
		if (underLine)
			chunk.setUnderline(1, -3);
		char chars[] = value.toCharArray();
		// for(char c:value.toCharArray()){
		for (int x = 0; x < chars.length; x++) {
			char c = chars[x];
			if (prvFont.getBaseFont().charExists(c)) {
				chunk.append(String.valueOf(c));
			} else {
				// find a proper font
				for (int i = 0; i < font.length; i++) {
					Font f = font[i];
					if (f.getBaseFont().charExists(c) || i == font.length - 1) {
						if (prvFont == f) {
							// same
							chunk.append(String.valueOf(c));
						} else {
							p.add(chunk);
							prvFont = f;
							chunk = new Chunk(String.valueOf(c));
							chunk.setFont(prvFont);
							if (underLine)
								chunk.setUnderline(1, -3);
						}
						break;
					}
				}
			}

		}
		p.add(chunk);

		if(header!=null){
			if(header.getWidth()<=0 && header.getPdfWidth()<=0){
				float width = chunk.getWidthPoint();
				if(width>header.getCalcWidth()){
					header.setCalcWidth(width);
				}
			}else if(header.getWidth() > 0){
				header.setCalcWidth(header.getWidth());
			}else if(header.getPdfWidth() > 0){
				header.setCalcWidth(header.getPdfWidth());
			}
		}

		return p;

	}

	public static Document createDocument(ParameterData data, ReportSetting setting){
		String pageSize = data.getPageSize()!=null ? data.getPageSize() : setting.getPageSize();
		if(setting.isHorizontal()){
			if(PaperSizeConstants.A4.equalsIgnoreCase(pageSize)){
				return new Document(PageSize.A4.rotate());
			}else if(PaperSizeConstants.A3.equalsIgnoreCase(pageSize)){
				return new Document(PageSize.A3.rotate());
			}else if(PaperSizeConstants.A2.equalsIgnoreCase(pageSize)){
				return new Document(PageSize.A2.rotate());
			}else if(PaperSizeConstants.A1.equalsIgnoreCase(pageSize)){
				return new Document(PageSize.A1.rotate());
			}
		}else{
			if(PaperSizeConstants.A4.equalsIgnoreCase(pageSize)){
				return new Document(PageSize.A4);
			}else if(PaperSizeConstants.A3.equalsIgnoreCase(pageSize)){
				return new Document(PageSize.A3);
			}else if(PaperSizeConstants.A2.equalsIgnoreCase(pageSize)){
				return new Document(PageSize.A2);
			}else if(PaperSizeConstants.A1.equalsIgnoreCase(pageSize)){
				return new Document(PageSize.A1);
			}
		}
		return new Document();
	}


	//Pdf Style
	public static Font[] createFontArray() throws DocumentException, IOException{
		Font[] fontArray = new Font[5];
		fontArray[0] = new Font(BaseFont.createFont("MHei-Medium", "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED));
		fontArray[1] = new Font(BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
		fontArray[2] = new Font(BaseFont.createFont("HeiseiMin-W3", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED));
		fontArray[3] = new Font(BaseFont.createFont("HYGoThic-Medium", "UniKS-UCS2-H", BaseFont.NOT_EMBEDDED));
		fontArray[4] = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED));
		return fontArray;
	}

	public static PdfStyle createHeaderFooterStyle(ParameterData data, ReportSetting setting) throws DocumentException, IOException{
		PdfStyle style = new PdfStyle();
		Font[] fontArray = createFontArray();
		for (int i = 0; i < fontArray.length; i++) {
			Font f = fontArray[i];
			f.setSize(8);
			f.setStyle(Font.NORMAL);
		}
		style.setFontList(fontArray);
		return style;
	}

	public static PdfStyle createCriteriaStyle(ParameterData data, ReportSetting setting) throws DocumentException, IOException{
		PdfStyle style = new PdfStyle();
		Font[] fontArray = createFontArray();
		for (int i = 0; i < fontArray.length; i++) {
			Font f = fontArray[i];
			float fontSize = data.getFontSizeCriteria()!=null ?  data.getFontSizeCriteria().floatValue() : PdfConstants.DEFAULT_CRITERIA_FONT_SIZE;
			f.setSize(fontSize);
			f.setStyle(Font.NORMAL);
		}
		style.setFontList(fontArray);
		return style;
	}

	public static PdfStyle createHeaderTitleStyle(ParameterData data, ReportSetting setting) throws DocumentException, IOException{
		PdfStyle style = new PdfStyle();
		Font[] fontArray = createFontArray();
		for (int i = 0; i < fontArray.length; i++) {
			Font f = fontArray[i];
			float fontSize = data.getFontSizeTitle()!=null ? data.getFontSizeTitle().floatValue() :
						PdfConstants.DEFAULT_TITLE_FONT_SIZE;
			f.setSize(fontSize);
			f.setStyle(Font.BOLD);
		}
		style.setFontList(fontArray);
		return style;
	}

	public static PdfStyle createTableTitleStyle(ParameterData data, ReportSetting setting) throws DocumentException, IOException{
		PdfStyle style = new PdfStyle();
		Font[] fontArray = createFontArray();
		for (int i = 0; i < fontArray.length; i++) {
			Font f = fontArray[i];
			float fontSize = data.getFontSizeColumnHeader()!=null? data.getFontSizeColumnHeader().floatValue() :  PdfConstants.DEFAULT_TABLE_HEADER_SIZE;
			f.setSize(fontSize);
			f.setStyle(Font.BOLD);
		}
		style.setFontList(fontArray);
		return style;
	}

	public static PdfStyle createTableContentStyle(ParameterData data, ReportSetting setting) throws DocumentException, IOException{
		PdfStyle style = new PdfStyle();
		Font[] fontArray = createFontArray();
		for (int i = 0; i < fontArray.length; i++) {
			Font f = fontArray[i];
			float fontSize = data.getFontSizeContent()!=null ? data.getFontSizeContent().floatValue() : PdfConstants.DEFAULT_TABLE_CONTENT_FONT_SIZE;
			f.setSize(fontSize);
			f.setStyle(Font.NORMAL);
		}
		style.setFontList(fontArray);
		return style;
	}

}
