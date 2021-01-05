package com.milliontech.circle.model.pdf;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFHeader;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.milliontech.circle.constants.ExcelConstants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.helper.PdfHelper;
import com.milliontech.circle.model.ReportSetting;

public class HeaderFooter extends PdfPageEventHelper {
		
	private ParameterData data;
	private ReportSetting setting;
	private String printDate;
	private PdfStyle headerStyle;
	private PdfStyle footerStyle;
	
	public HeaderFooter(ParameterData data, ReportSetting setting, PdfStyle headerStyle, PdfStyle footerStyle){
		this.data = data;
		this.setting = setting;
		this.headerStyle = headerStyle;
		this.footerStyle = footerStyle;
		printDate = "";
	}

	public void onOpenDocument(PdfWriter writer, Document document) {
        List<String> rightHeaderList = new ArrayList<String>();

        String restrictedLabel = null;
        if (setting.isShowRestrictedLabel() == null && data.getDefaultShowRestrictedLabel()) {
            restrictedLabel = data.getDefaultRestrictedLabel();
        } else if (setting.isShowRestrictedLabel() != null && setting.isShowRestrictedLabel().booleanValue()){
            restrictedLabel = setting.getRestrictedLabel();
        }
        if(StringUtils.isNotBlank(restrictedLabel)) {
            rightHeaderList.add(restrictedLabel);
        }
        
        if(setting.isShowPrintDate()){
            rightHeaderList.add(String.format("%s: %s", 
                    data.getPrintTimeLabel(),
                    DataHelper.getPrintDateStr(data.getPrintDate(), StringUtils.defaultIfEmpty(setting.getPrintDateFormat(), data.getDefaultPrintDateFormat()))
            ));
        }
        if(setting.isShowPrintBy()){
            rightHeaderList.add(String.format("%s: %s", 
                    data.getPrintByLabel(), 
                    data.getPrintBy())
            );
        }
        if(setting.isShowReportId()){
            rightHeaderList.add(String.format("Report ID: %s", setting.getReportId()));
        }
        
        printDate = StringUtils.join(rightHeaderList, "\n");
    }
	
	public void onEndPage(PdfWriter writer, Document document) {
        //Header            
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100f);
        headerTable.setTotalWidth(document.getPageSize().getWidth()-8);            
        headerTable.getDefaultCell().setFixedHeight(16);
        headerTable.getDefaultCell().setBorder(Rectangle.BOX);            
        headerTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        PdfPCell printDateCell = new PdfPCell();            
        Paragraph pdate = PdfHelper.createDisplayParagraph(printDate, headerStyle.getFontList(), false);
        printDateCell.setPhrase(pdate);
        printDateCell.setBorder(Rectangle.NO_BORDER);
        printDateCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);                        
        headerTable.addCell(printDateCell);
        
        headerTable.writeSelectedRows(0, -1, 0, document.getPageSize().getHeight()-10, writer.getDirectContent());                              
	}
}
