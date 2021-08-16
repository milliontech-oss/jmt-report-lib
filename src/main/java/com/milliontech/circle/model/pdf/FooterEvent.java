package com.milliontech.circle.model.pdf;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.PdfHelper;
import com.milliontech.circle.model.ReportSetting;

public class FooterEvent implements IEventHandler {
    private PdfFormXObject placeholder;
    private float side = 20;
    private float y = 10;
    private float space = 4.5f;
    private float descent = 3;

    private PdfStyle footerStyle;
    private ParameterData data;
    private ReportSetting setting;
    private PdfFontSetting fontSetting;

    public FooterEvent(ParameterData data, ReportSetting setting, PdfStyle footerStyle, PdfFontSetting fontSetting) {
        placeholder = new PdfFormXObject(new Rectangle(0, 0, side, side));
        this.data = data;
        this.setting = setting;
        this.footerStyle = footerStyle;
        this.fontSetting = fontSetting;
    }

    @Override
    public void handleEvent(Event event) {
        if(setting.isShowPageNumber()) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNumber = pdf.getPageNumber(page);
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(
                page.getLastContentStream(), page.getResources(), pdf);
            Canvas canvas = new Canvas(pdfCanvas, pageSize);
            canvas.setFontProvider(fontSetting.getFontProvider());
            canvas.setFontFamily(fontSetting.getDefaultFontFamily());
            
            Paragraph p = PdfHelper.createDisplayParagraph(String.format(data.getPageLabel() + " %d  /", pageNumber), footerStyle.getFontInfoList(), false, false);

            float x = pageSize.getWidth() / 2;
            canvas.showTextAligned(p, x, y, TextAlignment.RIGHT);
            pdfCanvas.addXObjectAt(placeholder, x + space, y - descent);
            pdfCanvas.release();
        }
    }

    public void writeTotal(PdfDocument pdf) {
        if(setting.isShowPageNumber()) {
            Canvas canvas = new Canvas(placeholder, pdf);
            canvas.setFontProvider(fontSetting.getFontProvider());
            canvas.setFontFamily(fontSetting.getDefaultFontFamily());
            
            Paragraph p = PdfHelper.createDisplayParagraph(String.valueOf(pdf.getNumberOfPages()), footerStyle.getFontInfoList(), false, false);
            canvas.showTextAligned(p, 0, descent, TextAlignment.LEFT);
        }
    }
}
