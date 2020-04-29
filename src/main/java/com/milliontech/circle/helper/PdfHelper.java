package com.milliontech.circle.helper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;
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
		String textValue;
		if(value==null){
			textValue = "";
		}else if(value instanceof java.util.Date){
			Date d = (Date)value;
			textValue = format != null? DataHelper.formatDate(d, format) : DataHelper.formatDate(d, Constants.DEFAULT_DATE_FORMAT);
		}else if(value instanceof java.sql.Date){
			java.sql.Date d = (java.sql.Date)value;
			textValue = format != null? DataHelper.formatDate(d, format) : DataHelper.formatDate(d, Constants.DEFAULT_DATE_FORMAT);
		}else if(value instanceof java.time.LocalDate){
			java.time.LocalDate d = (java.time.LocalDate) value;
			textValue = format != null ? DataHelper.formatDate(d, format) : DataHelper.formatDate(d, Constants.DEFAULT_DATE_FORMAT);
		}else if(value instanceof Timestamp){
			Timestamp t = (Timestamp)value;
			textValue = format != null? DataHelper.formatDate(t, format) : DataHelper.formatDate(t, Constants.DEFAULT_TIMESTAMP_FORMAT);
		}else if(value instanceof Instant){
		    Instant i = (Instant)value;
		    textValue = format != null? DataHelper.formatInstant(i, format) : DataHelper.formatInstant(i, Constants.DEFAULT_TIMESTAMP_FORMAT);
		}else if(value instanceof BigDecimal){
			BigDecimal b = (BigDecimal)value;
			if(format!=null){
				DecimalFormat df = new DecimalFormat(format);
				textValue = df.format(b.doubleValue());
			}else{
				textValue = b.toString();
			}
		}else if(value instanceof Integer){
			if(format!=null){
				DecimalFormat df = new DecimalFormat(format);
				textValue = df.format(value);
			}else{
				textValue = String.valueOf(value);
			}
		}else if(value instanceof Long){
			if(format!=null){
				DecimalFormat df = new DecimalFormat(format);
				textValue = df.format(value);
			}else{
				textValue = String.valueOf(value);
			}
		}else if(value instanceof Double){
			if(format!=null){
				DecimalFormat df = new DecimalFormat(format);
				textValue = df.format(value);
			}else{
				textValue = String.valueOf(value);
			}
		}else{
		    textValue = value.toString();
        }

		return textValue;
	}

	public static Paragraph createDisplayParagraph(String value, List<Font> fonts, boolean underLine) {
		return PdfHelper.createDisplayParagraph(value, fonts, underLine, null);
	}
	
	public static Paragraph createDisplayParagraph(String value, List<Font> fonts, boolean underLine, TableHeader header) {
        if (StringUtils.isBlank(value)) {
            return new Paragraph(new Chunk());
        }
        
        FontSelector selector = new FontSelector();
        for (Font f : fonts) {
            selector.addFont(f);
        }
        Phrase phrase = selector.process(value);
        
        Paragraph p = new Paragraph();
        
        float actualWidth = 0f;
        for (Chunk c : phrase.getChunks()) {
            if (underLine) {
                c.setUnderline(1, -3);
            }
            actualWidth += c.getWidthPoint();
        }

        p.add(phrase);

        if(header!=null){
            if(header.getWidth()<=0 && header.getPdfWidth()<=0){
                if(actualWidth>header.getCalcWidth()){
                    header.setCalcWidth(actualWidth);
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

}
