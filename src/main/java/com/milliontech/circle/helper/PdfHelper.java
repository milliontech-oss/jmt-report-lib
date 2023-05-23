package com.milliontech.circle.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.splitting.KeepAllSplitCharacters;
import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.constants.PaperSizeConstants;
import com.milliontech.circle.constants.PdfConstants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.model.ReportSetting;
import com.milliontech.circle.model.TableHeader;
import com.milliontech.circle.model.pdf.PdfStyle;


public class PdfHelper {

	private static final Logger log = LoggerFactory.getLogger(PdfHelper.class);

	public static void createPdfHeaderCell(Table table, String text, PdfStyle style, TableHeader header, BorderType border, String align, Color color){
        PdfHelper.createPdfHeaderCell(table, text, style, header, border, align, color, 1, 1, null);
    }

    public static void createPdfCell(Table table, String text, PdfStyle style, TableHeader header, BorderType border, String align, Color color){
        PdfHelper.createPdfCell(table, text, style, header, border, align, color, 1, 1, null);
    }

    public static void createPdfHeaderCell(Table table, String text, PdfStyle style, TableHeader header, BorderType border, String align, Color color, Float grey){
        PdfHelper.createPdfHeaderCell(table, text, style, header, border, align, color, 1, 1, grey);
    }

    public static void createPdfCell(Table table, String text, PdfStyle style, TableHeader header, BorderType border, String align, Color color, Float grey){
        PdfHelper.createPdfCell(table, text, style, header, border, align, color, 1, 1, grey);
    }

    public static void createPdfHeaderCell(Table table, String text, PdfStyle style, TableHeader header, BorderType border, String align, Color color, int colspan, int rowspan){
        PdfHelper.createPdfHeaderCell(table, text, style, header, border, align, color, colspan, rowspan, null);
    }

    public static void createPdfCell(Table table, String text, PdfStyle style, TableHeader header, BorderType border, String align, Color color, int colspan, int rowspan){
        PdfHelper.createPdfCell(table, text, style, header, border, align, color, colspan, rowspan, null);
    }

    public static void createPdfHeaderCell(Table table, String text, PdfStyle style, TableHeader header, BorderType border, String align, Color color, int colspan, int rowspan, Float grey){
        PdfHelper.createPdfHeaderCell(table, text, style, header, new ArrayList<>(Collections.singletonList(border)), align, color, colspan, rowspan, grey);
    }

    public static void createPdfCell(Table table, String text, PdfStyle style, TableHeader header, BorderType border, String align, Color color, int colspan, int rowspan, Float grey){
        PdfHelper.createPdfCell(table, text, style, header, new ArrayList<>(Collections.singletonList(border)), align, color, colspan, rowspan, grey);
    }

    public static void createPdfHeaderCell(Table table, String text, PdfStyle style, TableHeader header, List<BorderType> border, String align, Color color, int colspan, int rowspan, Float grey){
        //set breakAllSplit to true for report table header as workaround for iText7 TextRenderer bold text width calculation bug
    	PdfHelper.createPdfCell(table, text, style, header, border, align, color, true, colspan, rowspan, grey, true);
    }

    public static void createPdfCell(Table table, String text, PdfStyle style, TableHeader header, List<BorderType> border, String align, Color color, int colspan, int rowspan, Float grey){
        PdfHelper.createPdfCell(table, text, style, header, border, align, color, false, colspan, rowspan, grey, false);
    }

    public static void createPdfCell(Table table, String text, PdfStyle style, TableHeader header, List<BorderType> border, String align, Color color, boolean breakAllSplit, int colspan, int rowspan, Float grey, boolean isHeaderCell){
        Cell cell = new Cell(rowspan, colspan);
        cell.setKeepTogether(true);
        Paragraph pv = PdfHelper.createDisplayParagraph(text, style.getFontInfoList(), false, breakAllSplit, header);
        cell.add(pv);
        setBorder(cell, border);
        if(Constants.ALIGN_CENTER.equals(align)){
            cell.setTextAlignment(TextAlignment.CENTER);
        }else if(Constants.ALIGN_RIGHT.equals(align)){
            cell.setTextAlignment(TextAlignment.RIGHT);
        }else{
            cell.setTextAlignment(TextAlignment.LEFT);
        }
        if(color!=null){
            cell.setBackgroundColor(color);
        }
        if(grey!=null){
            cell.setBackgroundColor(new DeviceGray(grey.floatValue()));
        }

        cell.setPadding(2f);
        cell.setMinHeight(PdfConstants.DEFAULT_TABLE_CONTENT_FONT_SIZE);

        if(isHeaderCell) {
            table.addHeaderCell(cell);
        } else {
            table.addCell(cell);
        }
    }

    private static void setBorder(Cell cell, List<BorderType> border) {
        Border defaultBorder = new SolidBorder(PdfConstants.DEFAULT_TABLE_BORDER_WIDTH);
        if(border.contains(BorderType.ALL)) {
            cell.setBorder(defaultBorder);
        } else if(border.contains(BorderType.NONE)) {
            cell.setBorder(Border.NO_BORDER);
        } else {
            if (border.contains(BorderType.TOP)) {
                cell.setBorderTop(defaultBorder);
            }
            if (border.contains(BorderType.BOTTOM)) {
                cell.setBorderBottom(defaultBorder);
            }
            if (border.contains(BorderType.LEFT)) {
                cell.setBorderLeft(defaultBorder);
            }
            if (border.contains(BorderType.RIGHT)) {
                cell.setBorderRight(defaultBorder);
            }
        }
    }

    public static DeviceRgb getHighlightColor(String color){
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
            java.util.Date d = (java.util.Date)value;
            String defaultFormat = (value instanceof java.sql.Timestamp) ? Constants.DEFAULT_TIMESTAMP_FORMAT : Constants.DEFAULT_DATE_FORMAT;
			textValue = format != null? DataHelper.formatDate(d, format) : DataHelper.formatDate(d, defaultFormat);
		}else if(value instanceof java.time.LocalDate){
			java.time.LocalDate d = (java.time.LocalDate) value;
			textValue = format != null ? DataHelper.formatDate(d, format) : DataHelper.formatDate(d, Constants.DEFAULT_DATE_FORMAT);
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

	public static Paragraph createDisplayParagraph(String value, List<PdfFontInfo> fonts, boolean underLine, boolean breakAllSplit) {
        return PdfHelper.createDisplayParagraph(value, fonts, underLine, breakAllSplit, null);
    }

    public static Paragraph createDisplayParagraph(String value, List<PdfFontInfo> fonts, boolean underLine, boolean breakAllSplit, TableHeader header) {
    	if (StringUtils.isBlank(value)) {
            return new Paragraph();
        }

    	Text t = new Text(value);
    	if(breakAllSplit) {
            t.setSplitCharacters(new KeepAllSplitCharacters());
    	}

    	Paragraph p = new Paragraph(t);
    	p.setFontSize(fonts.get(0).getFontSize());

    	if(underLine) {
    		p = p.setUnderline(1, -3);
    	}
    	if(fonts.get(0).isBold()) {
    		p = p.setBold();
    	}

        return p;
    }

    /* Replaced by FontProvider
     *
    private static Paragraph processText(String text, List<PdfFontInfo> fontInfos, boolean underline) {
        Paragraph p = new Paragraph();
        StringBuffer sb = new StringBuffer();
        PdfFont currentFont = null;
        PdfFontInfo currentFontInfo = null;

        if(text != null) {
            char[] cc = text.toCharArray();

            for(int i = 0; i < cc.length; i++) {
                char c = cc[i];

                if (c == '\n' || c == '\r') {
                    sb.append(c);
                } else {
                    PdfFont font;
                    boolean isSurrogatePair = TextUtil.isSurrogatePair(cc, i);
                    int u = isSurrogatePair ? TextUtil.convertToUtf32(cc, i) : (int) c;

                    for (PdfFontInfo info : fontInfos) {
                        if (info.getFont().containsGlyph(u) || Character.getType(c) == Character.FORMAT) {
                            font = info.getFont();
                            if(currentFont != font) {
                                if(sb.length() > 0 && currentFont != null) {
                                    Text t = new Text(sb.toString());
                                    t.setFont(currentFont);
                                    t.setFontSize(currentFontInfo.getFontSize() > 0 ? currentFontInfo.getFontSize() : 12);
                                    if (currentFontInfo.isBold()) {
                                        t.setBold();
                                    }
                                    if (underline) {
                                        t.setUnderline(1, -3);
                                    }
                                    p.add(t);
                                    sb.setLength(0);
                                }
                                currentFont = font;
                                currentFontInfo = info;
                            }
                            sb.append(c);
                            if(isSurrogatePair) {
                                sb.append(cc[i+1]);
                            }
                            break;
                        }
                    }
                }
            }

            if (sb.length() > 0) {
                Text t = new Text(sb.toString());
                PdfFontInfo info = currentFontInfo != null ? currentFontInfo : fontInfos.get(0);
                t.setFont(info.getFont());
                t.setFontSize(info.getFontSize() > 0 ? info.getFontSize() : 12);
                if (info.isBold()) {
                    t.setBold();
                }
                if (underline) {
                    t.setUnderline(1, -3);
                }
                p.add(t);
            }
        }
        return p;
    }
    */

    public static void calcWidth(String value, List<PdfFontInfo> fonts, TableHeader header) {
        if (StringUtils.isBlank(value)) {
            return;
        }

        if(header!=null){
            float actualWidth = calcWidthAndSetMaxFontSize(value, fonts, header);

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
    }

    private static float calcWidthAndSetMaxFontSize(String text, List<PdfFontInfo> fontInfos, TableHeader header) {
        float width = 0f;

        StringBuffer sb = new StringBuffer();
        PdfFont currentFont = null;
        PdfFontInfo currentFontInfo = null;

        if(text != null) {
            char[] cc = text.toCharArray();

            for(int i = 0; i < cc.length; i++) {
                char c = cc[i];

                if (c == '\n' || c == '\r') {
                    sb.append(c);
                } else {
                    PdfFont font;
                    boolean isSurrogatePair = TextUtil.isSurrogatePair(cc, i);
                    int u = isSurrogatePair ? TextUtil.convertToUtf32(cc, i) : (int) c;

                    for (PdfFontInfo info : fontInfos) {
                        if (info.getFont().containsGlyph(u) || Character.getType(c) == Character.FORMAT) {
                            font = info.getFont();
                            if(currentFont != font) {
                                if(sb.length() > 0 && currentFont != null) {
                                    float fontSize = currentFontInfo.getFontSize() > 0 ? currentFontInfo.getFontSize() : 12;
                                    width += currentFont.getWidth(sb.toString(), fontSize);
                                    sb.setLength(0);
                                }
                                currentFont = font;
                                currentFontInfo = info;

                                if(header.getMaxFontSize() < currentFontInfo.getFontSize()) {
                                    header.setMaxFontSize(currentFontInfo.getFontSize());
                                }
                            }
                            sb.append(c);
                            if(isSurrogatePair) {
                                sb.append(cc[i+1]);
                            }
                            break;
                        }
                    }
                }
            }

            if (sb.length() > 0) {
                PdfFontInfo info = currentFontInfo != null ? currentFontInfo : fontInfos.get(0);
                float fontSize = info.getFontSize() > 0 ? info.getFontSize() : 12;
                width += info.getFont().getWidth(sb.toString(), fontSize);
            }
        }
        return width;
    }

    public static Document createDocument(PdfDocument pdf, ParameterData data, ReportSetting setting){
        String pageSize = data.getPageSize()!=null ? data.getPageSize() : setting.getPageSize();
        if(setting.isHorizontal()){
            if(PaperSizeConstants.A4.equalsIgnoreCase(pageSize)){
                pdf.setDefaultPageSize(PageSize.A4.rotate());
            }else if(PaperSizeConstants.A3.equalsIgnoreCase(pageSize)){
                pdf.setDefaultPageSize(PageSize.A3.rotate());
            }else if(PaperSizeConstants.A2.equalsIgnoreCase(pageSize)){
                pdf.setDefaultPageSize(PageSize.A2.rotate());
            }else if(PaperSizeConstants.A1.equalsIgnoreCase(pageSize)){
                pdf.setDefaultPageSize(PageSize.A1.rotate());
            }
        }else{
            if(PaperSizeConstants.A4.equalsIgnoreCase(pageSize)){
                pdf.setDefaultPageSize(PageSize.A4);
            }else if(PaperSizeConstants.A3.equalsIgnoreCase(pageSize)){
                pdf.setDefaultPageSize(PageSize.A3);
            }else if(PaperSizeConstants.A2.equalsIgnoreCase(pageSize)){
                pdf.setDefaultPageSize(PageSize.A2);
            }else if(PaperSizeConstants.A1.equalsIgnoreCase(pageSize)){
                pdf.setDefaultPageSize(PageSize.A1);
            }
        }
        return new Document(pdf);
    }

}
