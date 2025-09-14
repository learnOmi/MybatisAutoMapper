package com.main.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    // 常用日期格式
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
    // 其他常用日期格式
    public static final String DATE_FORMAT_CN = "yyyy年MM月dd日";
    public static final String DATE_TIME_FORMAT_CN = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String DATE_FORMAT_SLASH = "yyyy/MM/dd";
    public static final String DATE_TIME_FORMAT_SLASH = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE_FORMAT_DOT = "yyyy.MM.dd";
    public static final String DATE_TIME_FORMAT_DOT = "yyyy.MM.dd HH:mm:ss";
    public static final String DATE_FORMAT_SIMPLE = "yyyyMMdd";
    public static final String DATE_TIME_FORMAT_SIMPLE = "yyyyMMddHHmmss";
    public static final String TIME_FORMAT_SIMPLE = "HHmmss";
    
    /**
     * 格式化日期为字符串
     * @param date 日期对象
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
    
    /**
     * 格式化日期为字符串，使用默认格式 yyyy-MM-dd
     * @param date 日期对象
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date) {
        return formatDate(date, DATE_FORMAT);
    }
    
    /**
     * 格式化日期为字符串（中文格式）
     * @param date 日期对象
     * @return 格式化后的字符串
     */
    public static String formatDateCN(Date date) {
        return formatDate(date, DATE_FORMAT_CN);
    }
    
    /**
     * 格式化日期为字符串（斜杠格式）
     * @param date 日期对象
     * @return 格式化后的字符串
     */
    public static String formatDateSlash(Date date) {
        return formatDate(date, DATE_FORMAT_SLASH);
    }
    
    /**
     * 格式化日期为字符串（点格式）
     * @param date 日期对象
     * @return 格式化后的字符串
     */
    public static String formatDateDot(Date date) {
        return formatDate(date, DATE_FORMAT_DOT);
    }
    
    /**
     * 格式化日期为字符串（简单数字格式）
     * @param date 日期对象
     * @return 格式化后的字符串
     */
    public static String formatDateSimple(Date date) {
        return formatDate(date, DATE_FORMAT_SIMPLE);
    }
    
    /**
     * 格式化日期时间为字符串，使用默认格式 yyyy-MM-dd HH:mm:ss
     * @param date 日期时间对象
     * @return 格式化后的字符串
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, DATE_TIME_FORMAT);
    }
    
    /**
     * 格式化日期时间为字符串（中文格式）
     * @param date 日期时间对象
     * @return 格式化后的字符串
     */
    public static String formatDateTimeCN(Date date) {
        return formatDate(date, DATE_TIME_FORMAT_CN);
    }
    
    /**
     * 格式化日期时间为字符串（斜杠格式）
     * @param date 日期时间对象
     * @return 格式化后的字符串
     */
    public static String formatDateTimeSlash(Date date) {
        return formatDate(date, DATE_TIME_FORMAT_SLASH);
    }
    
    /**
     * 格式化日期时间为字符串（点格式）
     * @param date 日期时间对象
     * @return 格式化后的字符串
     */
    public static String formatDateTimeDot(Date date) {
        return formatDate(date, DATE_TIME_FORMAT_DOT);
    }
    
    /**
     * 格式化日期时间为字符串（简单数字格式）
     * @param date 日期时间对象
     * @return 格式化后的字符串
     */
    public static String formatDateTimeSimple(Date date) {
        return formatDate(date, DATE_TIME_FORMAT_SIMPLE);
    }
    
    /**
     * 将字符串解析为日期对象
     * @param dateStr 日期字符串
     * @param pattern 格式模式
     * @return 日期对象
     */
    public static Date parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("日期解析失败: " + dateStr, e);
        }
    }
    
    /**
     * 将字符串解析为日期对象，使用默认格式 yyyy-MM-dd
     * @param dateStr 日期字符串
     * @return 日期对象
     */
    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, DATE_FORMAT);
    }
    
    /**
     * 将字符串解析为日期时间对象，使用默认格式 yyyy-MM-dd HH:mm:ss
     * @param dateTimeStr 日期时间字符串
     * @return 日期时间对象
     */
    public static Date parseDateTime(String dateTimeStr) {
        return parseDate(dateTimeStr, DATE_TIME_FORMAT);
    }
    
    /**
     * 格式化时间为字符串（简单数字格式）
     * @param date 时间对象
     * @return 格式化后的字符串
     */
    public static String formatTimeSimple(Date date) {
        return formatDate(date, TIME_FORMAT_SIMPLE);
    }

}
