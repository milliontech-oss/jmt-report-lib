package com.milliontech.circle.helper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.constants.ExcelConstants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.model.ReportSetting;

public class ExcelHelper {

	private static final Map<String, IndexedColors> highlightColorMap;
	private static final Map<IndexedColors, java.awt.Color> colorMap;

	static {
	    Map<String, IndexedColors> _highlightColorMap = new HashMap<String, IndexedColors>();
		_highlightColorMap.put("blue", IndexedColors.LIGHT_BLUE);
		_highlightColorMap.put("yellow", IndexedColors.YELLOW);
		_highlightColorMap.put("purple", IndexedColors.VIOLET);
		_highlightColorMap.put("red", IndexedColors.RED);
		_highlightColorMap.put("pink", IndexedColors.PINK);
		_highlightColorMap.put("orange", IndexedColors.ORANGE);
		_highlightColorMap.put("default", IndexedColors.LIGHT_GREEN);
		highlightColorMap = Collections.unmodifiableMap(_highlightColorMap);

		Map<IndexedColors, java.awt.Color> _colorMap = new HashMap<IndexedColors, java.awt.Color>();
		_colorMap.put(IndexedColors.LIGHT_BLUE, new java.awt.Color(70, 130, 180));
		_colorMap.put(IndexedColors.YELLOW, new java.awt.Color(250, 249, 182));
		_colorMap.put(IndexedColors.VIOLET, new java.awt.Color(240, 158, 247));
		_colorMap.put(IndexedColors.RED, new java.awt.Color(205, 0, 0));
		_colorMap.put(IndexedColors.PINK, new java.awt.Color(250, 195, 182));
		_colorMap.put(IndexedColors.ORANGE, new java.awt.Color(255, 127, 0));
		_colorMap.put(IndexedColors.LIGHT_GREEN, new java.awt.Color(34, 139, 34));
		colorMap = Collections.unmodifiableMap(_colorMap);
	}

	public static void resetColorPalette(HSSFWorkbook wb){
		HSSFPalette palette = wb.getCustomPalette();
		for (Map.Entry<IndexedColors, java.awt.Color> entry : colorMap.entrySet()){
			IndexedColors index = entry.getKey();
			java.awt.Color color = entry.getValue();
			palette.setColorAtIndex(index.getIndex(), (byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue());
		}
	}

	public static int createSeperatorRow(int index, Sheet sheet, int numOfColumns, CellStyle style){
		Row row = sheet.createRow(index);
		row.setHeightInPoints(5);
		index++;
		for(int i=0; i<numOfColumns; i++){
			Cell cell = row.createCell(i);
			cell.setCellStyle(style);
		}
		return index;
	}

	public static Object setCellValue(Class clazz, Object obj, String method, String property, Cell cell, DataFormat format, String align, Map methodValMapMap) throws Exception{
		Object value = ValueHelper.getDataValue(clazz, obj, method, property, methodValMapMap);

		if(value==null){
            cell.setCellValue("");
		}else if(value instanceof java.util.Date){
            /* javal.sql.Date and java.sql.Timestamp also extends from java.util.Date */
            java.util.Date utilDate = (java.util.Date) value;
            String excelCellFormat = cell.getCellStyle().getDataFormatString();
            boolean needTruncateToSecond = !excelCellFormat.endsWith("0");
            if (needTruncateToSecond) {
                cell.setCellValue(DateUtils.truncate(utilDate, Calendar.SECOND));
            } else {
                cell.setCellValue(utilDate);
            }
		}else if(value instanceof java.time.LocalDate){
			java.time.LocalDate d = (java.time.LocalDate)value;
			cell.setCellValue(java.util.Date.from(d.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		}else if(value instanceof Instant){
		    Instant i = (Instant)value;
		    String excelCellFormat = cell.getCellStyle().getDataFormatString();
		    boolean needTruncateToSecond = !excelCellFormat.endsWith("0");
		    if (needTruncateToSecond) {
		        cell.setCellValue(java.util.Date.from(i.truncatedTo(ChronoUnit.SECONDS)));
		    } else {
		        cell.setCellValue(java.util.Date.from(i.truncatedTo(ChronoUnit.MILLIS)));
		    }
		}else if(value instanceof BigDecimal){
			BigDecimal b = (BigDecimal)value;
			cell.setCellValue(b.doubleValue());
			if (StringUtils.isBlank(align)) {
				cell.getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
			}
			if(StringUtils.equalsIgnoreCase(cell.getCellStyle().getDataFormatString(), "General")){
				cell.getCellStyle().setDataFormat(format.getFormat("0.0#"));
			}
		}else if(value instanceof Integer){
			cell.setCellValue(Integer.parseInt(value+""));
			if (StringUtils.isBlank(align)) {
				cell.getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
			}
			if(StringUtils.equalsIgnoreCase(cell.getCellStyle().getDataFormatString(), "General")){
				cell.getCellStyle().setDataFormat(format.getFormat("0"));
			}
		}else if(value instanceof Long){
			cell.setCellValue(Integer.parseInt(value+""));
			if (StringUtils.isBlank(align)) {
				cell.getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
			}
			if(StringUtils.equalsIgnoreCase(cell.getCellStyle().getDataFormatString(), "General")){
				cell.getCellStyle().setDataFormat(format.getFormat("0"));
			}
		}else if(value instanceof Double){
			cell.setCellValue(Double.parseDouble(value+""));
			if (StringUtils.isBlank(align)) {
				cell.getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
			}
			if(StringUtils.equalsIgnoreCase(cell.getCellStyle().getDataFormatString(), "General")){
				cell.getCellStyle().setDataFormat(format.getFormat("0.0#"));
			}
		}else{
			cell.setCellValue(value.toString());
		}

		return value;
	}

	public static void setHightlightColor(CellStyle style, String color){
		IndexedColors indexColor = highlightColorMap.get(StringUtils.lowerCase(color));
		if (indexColor == null){
			indexColor = highlightColorMap.get("default");
		}

		if(style instanceof HSSFCellStyle){
			style.setFillForegroundColor(indexColor.getIndex());
		} else {
			XSSFCellStyle xcs = (XSSFCellStyle) style;
			xcs.setFillForegroundColor(new XSSFColor(colorMap.get(indexColor), new DefaultIndexedColorMap()));
		}

		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	}

	/**********************************
	 * Styles in Excel
	 **********************************/

	public static CellStyle createSeperatorStyle(Workbook wb){
		CellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return style;
	}

	public static CellStyle createCriteriaStyle(Workbook wb, ParameterData data, ReportSetting setting){
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		short fontSize = data.getFontSizeCriteria()!=null ? data.getFontSizeCriteria().shortValue() : ExcelConstants.DEFAULT_FONT_SIZE;
		Font font = wb.findFont(false, IndexedColors.BLACK.getIndex(), fontSize, setting.getExcelFontName(), false, false, Font.SS_NONE, Font.U_NONE);
		if(font == null){
			font = wb.createFont();
			font.setFontHeightInPoints(fontSize);
			font.setFontName(setting.getExcelFontName());
		}
		style.setFont(font);
		return style;
	}

	public static CellStyle createTableTitleStyle(Workbook wb, ParameterData data, ReportSetting setting){
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);

		if(wb instanceof HSSFWorkbook){

			if(setting.getTableHeaderColorArray()!=null){
				int[] colorArray = setting.getTableHeaderColorArray();
				HSSFPalette palette = ((HSSFWorkbook )wb).getCustomPalette();
				palette.setColorAtIndex(IndexedColors.LIGHT_YELLOW.getIndex(),(byte)colorArray[0],(byte)colorArray[1],(byte)colorArray[2]);
			}

			if(setting.isHightlightHeader()){
				style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			}

		} else if (wb instanceof XSSFWorkbook || wb instanceof SXSSFWorkbook){

			if(setting.isHightlightHeader()){
				XSSFCellStyle xs = (XSSFCellStyle) style;
				if(setting.getTableHeaderColorArray() != null){
					int[] colorArray = setting.getTableHeaderColorArray();
					xs.setFillForegroundColor(new XSSFColor(new java.awt.Color(colorArray[0], colorArray[1], colorArray[2]), new DefaultIndexedColorMap()));
				} else {
					xs.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
				}
				xs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			}

		} else {
			throw new RuntimeException("Unsupported spreadsheet format: " + wb.getClass());
		}

		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());

		short fontSize = data.getFontSizeColumnHeader()!=null ? data.getFontSizeColumnHeader().shortValue() : ExcelConstants.DEFAULT_FONT_SIZE;
		Font font = wb.findFont(true, IndexedColors.BLACK.getIndex(), fontSize, setting.getExcelFontName(), false, false, Font.SS_NONE, Font.U_NONE);
		if(font == null){
			font = wb.createFont();
			font.setFontHeightInPoints(fontSize);
			font.setFontName(setting.getExcelFontName());
			font.setBold(true);
		}
		style.setFont(font);
		return style;
	}

	public static CellStyle createContentWrapStyle(Workbook wb, ParameterData data, ReportSetting setting){
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);

		short fontSize = data.getFontSizeContent()!=null ? data.getFontSizeContent().shortValue() : ExcelConstants.DEFAULT_FONT_SIZE;
		Font font = wb.findFont(false, IndexedColors.BLACK.getIndex(), fontSize, setting.getExcelFontName(), false, false, Font.SS_NONE, Font.U_NONE);
		if(font == null){
			font = wb.createFont();
			font.setFontHeightInPoints(fontSize);
			font.setFontName(setting.getExcelFontName());
		}
		style.setFont(font);
		return style;
	}

	public static CellStyle createContentStyle(Workbook wb, ParameterData data, ReportSetting setting, String format,
			boolean isHighlight, String highlightColor, boolean isItalicsFont, boolean isWrapText, String align){
		CellStyle style = wb.createCellStyle();

		style.setAlignment(getPoiAlign(align, HorizontalAlignment.LEFT));
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());

		if(StringUtils.isNotBlank(format)){
			// handle excel 2013+ format changes
			String escapedFormat = format.replace("/", "\\/");
			style.setDataFormat(wb.createDataFormat().getFormat(escapedFormat));
		}
		if(isHighlight){
			ExcelHelper.setHightlightColor(style, highlightColor);
		}

		short fontSize = data.getFontSizeContent()!=null ? data.getFontSizeContent().shortValue() : ExcelConstants.DEFAULT_FONT_SIZE;
		Font font = wb.findFont(false, IndexedColors.BLACK.getIndex(), fontSize, setting.getExcelFontName(), isItalicsFont, false, Font.SS_NONE, Font.U_NONE);
		if(font == null){
			font = wb.createFont();
			font.setFontHeightInPoints(fontSize);
			font.setFontName(setting.getExcelFontName());
			font.setItalic(isItalicsFont);
		}
		style.setFont(font);
		style.setWrapText(isWrapText);

		return style;
	}

	public static CellStyle createSummaryCellStyle(Workbook wb, ParameterData data, ReportSetting setting, String align, boolean hightlight){
		CellStyle style = wb.createCellStyle();
		style.setAlignment(getPoiAlign(align, HorizontalAlignment.RIGHT));
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		if(hightlight){
			ExcelHelper.setSummaryRowHightlight(style);
		}

		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());

		short fontSize = data.getFontSizeContent()!=null ? data.getFontSizeContent().shortValue() : ExcelConstants.DEFAULT_FONT_SIZE;
		Font font = wb.findFont(false, IndexedColors.BLACK.getIndex(), fontSize, setting.getExcelFontName(), false, false, Font.SS_NONE, Font.U_NONE);
		if(font == null){
			font = wb.createFont();
			font.setFontHeightInPoints(fontSize);
			font.setFontName(setting.getExcelFontName());
		}
		style.setFont(font);
		return style;
	}

	private static void setSummaryRowHightlight(CellStyle style){
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	}

	private static HorizontalAlignment getPoiAlign(String align, HorizontalAlignment defaultPoiAlign) {
		HorizontalAlignment poiAlign = defaultPoiAlign;

		if(Constants.ALIGN_LEFT.equals(align)) {
			poiAlign = HorizontalAlignment.LEFT;
		} else if(Constants.ALIGN_CENTER.equals(align)) {
			poiAlign = HorizontalAlignment.CENTER;
		} else if(Constants.ALIGN_RIGHT.equals(align)) {
			poiAlign = HorizontalAlignment.RIGHT;
		}

		return poiAlign;
	}


}
