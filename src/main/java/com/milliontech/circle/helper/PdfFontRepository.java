package com.milliontech.circle.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

public class PdfFontRepository {
    
    public static final List<BaseFont> SANS_SERIF_FONT_LIST;
    public static final List<BaseFont> SERIF_FONT_LIST;
    
    static {
        
        try {
            SANS_SERIF_FONT_LIST = Collections.unmodifiableList(Arrays.asList(
                    BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED),
                    BaseFont.createFont(BaseFont.ZAPFDINGBATS, BaseFont.CP1252, BaseFont.NOT_EMBEDDED),
                    BaseFont.createFont("MHei-Medium", "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED),
                    BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED),
                    BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED),
                    BaseFont.createFont("HYGoThic-Medium", "UniKS-UCS2-H", BaseFont.NOT_EMBEDDED)
            ));
            
            SERIF_FONT_LIST = Collections.unmodifiableList(Arrays.asList(
                    BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED),
                    BaseFont.createFont(BaseFont.ZAPFDINGBATS, BaseFont.CP1252, BaseFont.NOT_EMBEDDED),
                    BaseFont.createFont("MSung-Light", "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED),
                    BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED),
                    BaseFont.createFont("HeiseiMin-W3", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED),
                    BaseFont.createFont("HYSMyeongJo-Medium", "UniKS-UCS2-H", BaseFont.NOT_EMBEDDED)
                ));
            
        } catch (Exception ex) {
            throw new IllegalArgumentException("cannot initialize the basefont list - check itext-asian exists in classpath", ex);
        }
        
    }
    
    public static List<BaseFont> createBaseFontList(List<String> fontFilePathList){
        List<BaseFont> bfList = new ArrayList<BaseFont>();
        for (String fontPath : fontFilePathList) {
            try {
                BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, true);
                bfList.add(bf);
            } catch (Exception ex) {
                throw new RuntimeException(String.format("unable to load the specified font: [%s]", fontPath), ex);
            }
        }
        return bfList;
    }
    
    public static List<Font> createFontList(List<BaseFont> bfList, float size, int style){
        List<Font> fonts = new ArrayList<Font>();
        for (BaseFont bf : bfList) {
            Font f = new Font(bf);
            f.setSize(size);
            f.setStyle(style);
            fonts.add(f);
        }
        return Collections.unmodifiableList(fonts);
    }

}
