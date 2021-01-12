package com.milliontech.circle.helper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataHelper {

	private static final Logger log = LoggerFactory.getLogger(DataHelper.class);

	private static final Pattern INDEXED_PROPERTY_PATTERN;

	static {
		INDEXED_PROPERTY_PATTERN = Pattern.compile("^(.+)\\[(.+)\\]\\.(.+)$");
	}
	
	public static String formatInstant(Instant i, String format){
        return i==null?"":DataHelper.formatTimestamp(Timestamp.from(i), format);
    }

	public static String formatDate(Date d, String format){
		if(d==null){
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.ENGLISH);
		return sdf.format(d);
	}

	public static String formatDate(java.time.LocalDate d, String format) {
		if (d == null) {
			return "";
		}

		return d.format(DateTimeFormatter.ofPattern(format));
	}

	public static String formatTimestamp(Timestamp d, String format){
		if(d==null){
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.ENGLISH);
		return sdf.format(d);
	}

	public static short getShort(String value, short defaultValue) {
		if (value == null)
			return defaultValue;

		try {
			return (short)Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float getFloat(String value, float defaultValue) {
		if (value == null)
			return defaultValue;

		try {
			return (float)Float.parseFloat(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static int getInteger(String value, int defaultValue) {
		if (value == null)
			return defaultValue;

		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static String getString(String value, String defaultValue) {
		if (value == null || value.length()==0)
			return defaultValue;
		return value;
	}

	public static double getDoublue(String value, double defaultValue) {
		if (value == null)
			return defaultValue;

		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return defaultValue;
		}

	}
	
	public static boolean isTrue(String value) {
	    return "true".equalsIgnoreCase(value) || "Y".equalsIgnoreCase(value);
	}

	public static boolean isTrue(String value, boolean defaultValue) {
		if (value == null)
			return defaultValue;
		return isTrue(value);
	}

	public static String getPrintDateStr(java.util.Date date, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
		return sdf.format(date);
	}

	public static Object getRemapValue(Object value, Map methodValMapMap, String method, String property){
		if(value!=null){

			Map valMap = null;

			if(method != null){
				valMap = (Map)methodValMapMap.get(method);
			}

			if(valMap == null){
				if(property != null){
					String targetPropertyName = property;

					if(StringUtils.contains(property, ".")){
						if(StringUtils.indexOf(property, ".") == StringUtils.lastIndexOf(property, ".")){
							/* remap existing property indexer */
							Matcher m = INDEXED_PROPERTY_PATTERN.matcher(property);
							if(m.matches() && m.groupCount() == 3){
								targetPropertyName = m.group(1)+"."+m.group(3);
							}
						} else {
							log.warn("More than 2 level of property, ignoring the guess work");
						}

					}

					valMap = (Map)methodValMapMap.get(targetPropertyName);

				}
			}

			if(valMap != null){
				String str = value.toString();
				if(valMap.containsKey(str)){
					return valMap.get(str);
				}
				return str;
			}
		}

		return value;
	}

}
