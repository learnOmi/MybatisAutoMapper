package com.main.bean;

import com.main.utils.PropertiesUtils;

public class Constants {
    public static Boolean IGNORE_TABLE_PREFIX;
    public static String SUFFIX_BEAN_PARMA;

    static {
        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getString("ignore.table.prefix"));
        SUFFIX_BEAN_PARMA = PropertiesUtils.getString("suffix.bean.parma");
    }
}
