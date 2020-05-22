package com.milliontech.circle.helper;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import jodd.bean.BeanUtil;

public class ValueHelper {
    
    public static boolean isItalics(Class<?> clazz, Object obj, String property) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Boolean result = (Boolean)getDataValue(clazz, obj, null, property, null);
        if(result==null){
            return false;
        }
        return result.booleanValue();
    }

    public static boolean isHighlight(Class<?> clazz, Object obj, String property) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Boolean result = (Boolean)getDataValue(clazz, obj, null, property, null);
        if(result==null){
            return false;
        }
        return result.booleanValue();
    }
    
    public static Object getDataValue(Class<?> clazz, Object obj, String method, String property, Map methodValMapMap){
        if (method == null && property == null) {
            return null;
        }
        
        Object value = null;
        if (property != null){
            try {
                value = BeanUtil.pojo.getProperty(obj, property);
            } catch (Exception ex) {
                //throw new RuntimeException(String.format("cannot access class=[%s], property=[%s]", clazz, property), ex);
                //org.slf4j.LoggerFactory.getLogger(ValueHelper.class).warn(String.format("cannot access class=[%s], property=[%s]", clazz, property), ex);
                value = null;
            }
        } else if (method != null){
            if (method.contains(".")) {
                String[] methods = StringUtils.split(method, '.');
                Object currentObj = obj;
                for(String m : methods) {
                    currentObj = invokeMethod(currentObj.getClass(), currentObj, m);
                    if (currentObj == null) {
                        break;
                    }
                }
                value = currentObj; 
            } else {
                value = invokeMethod(clazz, obj, method);
            }
        }

        if(methodValMapMap!=null && value!=null){
            value = DataHelper.getRemapValue(value, methodValMapMap, method, property);
        }
        return value;
    }

    private static Object invokeMethod(Class<?> clazz, Object obj, String method) {
        try {
            return MethodUtils.invokeExactMethod(obj, method);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("cannot access class=[%s], method=[%s]", clazz, method), ex);
        }
    }
}
