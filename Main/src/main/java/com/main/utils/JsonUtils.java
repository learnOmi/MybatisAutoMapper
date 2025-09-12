package com.main.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

public class JsonUtils {
    public static String toJson(Object obj) {
        if (null == obj) {
            return null;
        }
        //return JSON.toJSONString(obj, JSONWriter.Feature.ReferenceDetection);
        return  JSON.toJSONString(obj);
    }
}
