package com.milliontech.circle.model.pdf;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.helper.PdfHelper;
import com.milliontech.circle.model.ReportSetting;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HeaderEvent implements IEventHandler {
    private Logger log = LoggerFactory.getLogger(HeaderEvent.class);

    private PdfStyle headerStyle;
    private PdfFontSetting fontSetting;
    private String printDate = "";
    private int noOfRows = 1;

    public HeaderEvent(ParameterData data, ReportSetting setting, PdfStyle headerStyle, PdfFontSetting fontSetting) {
        this.headerStyle = headerStyle;
        this.printDate = getPrintDate(data, setting);
        this.fontSetting = fontSetting;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(
            page.getLastContentStream(), page.getResources(), pdf);
        Canvas canvas = new Canvas(pdfCanvas, pageSize);
        canvas.setFontProvider(fontSetting.getFontProvider());
        canvas.setFontFamily(fontSetting.getDefaultFontFamily());
        
        Paragraph p = PdfHelper.createDisplayParagraph(printDate, headerStyle.getFontInfoList(), false, false);
        p.setMultipliedLeading(0.8f);

        canvas.showTextAligned(p, pageSize.getWidth() - 10, pageSize.getHeight() - 14 - 8 * noOfRows, TextAlignment.RIGHT);
        pdfCanvas.release();
    }

    private String getPrintDate(ParameterData data, ReportSetting setting) {
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

        if(!rightHeaderList.isEmpty()) {
            noOfRows = rightHeaderList.size();
        }
        return StringUtils.join(rightHeaderList, "\n");
    }
}
