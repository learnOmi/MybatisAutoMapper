package com.main.utils;

public class BooleanUtils {
    public static Boolean compare(Boolean a, Boolean b) {
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }
}
