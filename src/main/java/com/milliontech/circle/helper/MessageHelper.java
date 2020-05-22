package com.milliontech.circle.helper;

import java.util.Map;

import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.model.MessageKeyTitleElement;
import com.milliontech.circle.model.TableHeader;

public class MessageHelper {

    public static void setTitleByMessageKey(Map parameter, ParameterData data, MessageKeyTitleElement elmt) {
        if(elmt.getMsgKey() == null) {
            return;
        }
        boolean resolved = false;
        if (data.getMsgKeyPropMap() != null && !data.getMsgKeyPropMap().isEmpty()) {
            resolved = resolveAndSetTitle(data.getMsgKeyPropMap(), elmt);
        }
        if (resolved) {
            return;
        }
        // legacy fallback
        resolved = resolveAndSetTitle(parameter, elmt);
    }
    
    private static boolean resolveAndSetTitle(Map parameter, MessageKeyTitleElement elmt) {
        Object val = parameter.get(elmt.getMsgKey());
        if (val instanceof Map) {
            TableHeader tableHeader = (TableHeader)elmt;
            tableHeader.setTitleMap((Map)val);
            return true;
        } else if (val instanceof String) {
            elmt.setTitle((String)val);
            return true;
        }
        return false;
    }
    
}
