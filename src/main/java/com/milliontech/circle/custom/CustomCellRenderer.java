package com.milliontech.circle.custom;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomCellRenderer extends CellRenderer {
    private static final Logger log = LoggerFactory.getLogger(CustomCellRenderer.class);

    private int pageNo = 0;

    public CustomCellRenderer(Cell modelElement) {
        super(modelElement);
    }

    public CustomCellRenderer(Cell modelElement, int pageNo) {
        super(modelElement);
        this.pageNo = pageNo;
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutResult result = super.layout(layoutContext);
        if (LayoutResult.FULL != result.getStatus()) {
            if(pageNo == 0) {
                result.setStatus(LayoutResult.NOTHING);
                result.setOverflowRenderer(this);
                pageNo = layoutContext.getArea().getPageNumber();
            }
        }

        return result;
    }

    @Override
    public IRenderer getNextRenderer() {
        return new CustomCellRenderer((Cell)getModelElement(), pageNo);
    }
}
