package com.milliontech.circle.helper;

import java.util.Map;

import com.milliontech.circle.model.MessageKeyTitleElement;
import com.milliontech.circle.model.TableHeader;

public class MessageHelper {
		
	public static void setTitleByMessageKey(Map parameter, Object obj) {
		if(obj instanceof MessageKeyTitleElement){
			MessageKeyTitleElement elmt = (MessageKeyTitleElement)obj;
			if(elmt.getMsgKey()!=null && parameter.get(elmt.getMsgKey())!=null){
				if(parameter.get(elmt.getMsgKey()) instanceof Map){
					TableHeader tableHeader = (TableHeader)elmt;
					tableHeader.setTitleMap((Map)parameter.get(elmt.getMsgKey()));
				} else if(parameter.get(elmt.getMsgKey()) instanceof String){
					elmt.setTitle((String)parameter.get(elmt.getMsgKey()));
				}
			}				
		}
	}
	
}
