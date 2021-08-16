package com.milliontech.circle.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.layout.font.FontProvider;
import com.milliontech.circle.custom.CustomFontProvider;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.model.pdf.PdfFontSetting;

public class PdfFontRepository {
	
	private static final Logger log = LoggerFactory.getLogger(PdfFontRepository.class);
	
    public static final List<PdfFont> SANS_SERIF_FONT_LIST;
    public static final List<PdfFont> SERIF_FONT_LIST;

    static {
        SANS_SERIF_FONT_LIST = getSansSerifFontList();
        SERIF_FONT_LIST = getSerifFontList();
    }
    
    public static List<StandardFontInfo> getSansSerifFontInfoList(){
    	try {
    		return Arrays.asList(
    				new StandardFontInfo(StandardFonts.HELVETICA, PdfEncodings.CP1252),
    				new StandardFontInfo(StandardFonts.ZAPFDINGBATS, PdfEncodings.CP1252),
    				new StandardFontInfo("MHei-Medium", "UniCNS-UCS2-H"),
    				new StandardFontInfo("STSong-Light", "UniGB-UCS2-H"),
    				new StandardFontInfo("HeiseiKakuGo-W5", "UniJIS-UCS2-H"),
    				new StandardFontInfo("HYGoThic-Medium", "UniKS-UCS2-H")
				);    		
    	} catch (Exception ex) {
            throw new IllegalArgumentException("cannot initialize the basefont info list", ex);
        }
    }
    
    public static List<StandardFontInfo> getSerifFontInfoList(){
    	try {
    		return Arrays.asList(
    				new StandardFontInfo(StandardFonts.TIMES_ROMAN, PdfEncodings.CP1252),
                    new StandardFontInfo(StandardFonts.ZAPFDINGBATS, PdfEncodings.CP1252),
                    new StandardFontInfo("MSung-Light", "UniCNS-UCS2-H"),
                    new StandardFontInfo("STSong-Light", "UniGB-UCS2-H"),
                    new StandardFontInfo("HeiseiMin-W3", "UniJIS-UCS2-H"),
                    new StandardFontInfo("HYSMyeongJo-Medium", "UniKS-UCS2-H")
				);
    	} catch (Exception ex) {
            throw new IllegalArgumentException("cannot initialize the basefont info list", ex);
        }
    }
    
    public static List<PdfFont> getSansSerifFontList() {
    	return createStandardFontList(getSansSerifFontInfoList());
    }

    public static List<PdfFont> getSerifFontList() {
    	return createStandardFontList(getSerifFontInfoList());
    }
    
    private static List<PdfFont> createStandardFontList(List<StandardFontInfo> fontInfoList){
    	return fontInfoList.stream().map(fi -> {
			try {
				return PdfFontFactory.createFont(fi.getFontName(), fi.getEncoding(), EmbeddingStrategy.PREFER_NOT_EMBEDDED);
			} catch (Exception ex) {
	            throw new IllegalArgumentException("cannot initialize the basefont list - check itext-asian exists in classpath", ex);
	        }
		}).collect(Collectors.toList());
    }

    public static List<PdfFont> createPdfFontList(List<String> fontFilePathList){
        List<PdfFont> bfList = new ArrayList<PdfFont>();
        for (String fontPath : fontFilePathList) {
            try {
                PdfFont bf = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, EmbeddingStrategy.PREFER_EMBEDDED);
                bfList.add(bf);
            } catch (Exception ex) {
                throw new RuntimeException(String.format("unable to load the specified font: [%s]", fontPath), ex);
            }
        }
        return bfList;
    }

    public static List<PdfFontInfo> createPdfFontInfoList(List<PdfFont> pfList, float size, boolean bold){
        List<PdfFontInfo> fontInfos = new ArrayList<PdfFontInfo>();
        for (PdfFont pf : pfList) {
            PdfFontInfo info = new PdfFontInfo(pf, size);
            info.setBold(bold);
            fontInfos.add(info);
        }
        return Collections.unmodifiableList(fontInfos);
    }
    
    public static PdfFontSetting getPdfFontSetting(ParameterData data) {
    	List<PdfFont> fontList = data.getFontFilePathList().isEmpty() ?
                getSansSerifFontList() :
                createPdfFontList(data.getFontFilePathList());
    	
    	FontProvider fontProvider = data.getFontFilePathList().isEmpty() ? 
    			getStandardFontProvider() : 
				getFileFontProvider(data.getFontFilePathList());
    	
    	String dfltFontFamily = StringUtils.isBlank(data.getDefaultFontFamily()) ? 
    			getFontFamily(fontList.get(0)) : data.getDefaultFontFamily();
    	
    	return new PdfFontSetting(fontList, fontProvider, dfltFontFamily);
    }
    
    private static FontProvider getStandardFontProvider(){
    	FontProvider fp = new CustomFontProvider(false);
    		
		getSansSerifFontInfoList().forEach(fi -> {
			try {
				fp.addFont(FontProgramFactory.createFont(fi.getFontName()), fi.getEncoding());
			} catch (IOException e) {
				throw new IllegalArgumentException("cannot create FontProgram", e);
			}
		});

		return fp;
    }
    
    private static FontProvider getFileFontProvider(List<String> fontFilePathList){
    	FontProvider fp = new CustomFontProvider(true);
		fontFilePathList.forEach(f -> fp.addFont(f, PdfEncodings.IDENTITY_H));
		return fp;
    }
    
    private static String getFontFamily(PdfFont font) {
    	String fontFamilyName = null;
    	if(font != null && font.getFontProgram() != null && font.getFontProgram().getFontNames() != null && 
    			font.getFontProgram().getFontNames().getFamilyName() != null) {
    		
    		String[][] familyName = font.getFontProgram().getFontNames().getFamilyName();
    		for(int x = 0; x < familyName.length; x++) {
    			for(int y = 0; y < familyName[x].length; y++) {
    				fontFamilyName = familyName[x][y];
    			}
    		}
    	}
    	return fontFamilyName;
    }
    
}
