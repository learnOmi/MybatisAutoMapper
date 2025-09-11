package com.main.utils;

public class StringUtils {
    public static String uperCaseFirst (String str){
        if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String lowerCaseFirst (String str){
        if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
