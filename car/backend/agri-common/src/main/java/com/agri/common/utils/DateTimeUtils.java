package com.agri.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类
 */
public class DateTimeUtils {

    public static final String DATE_FMT = "yyyy-MM-dd";
    public static final String DATETIME_FMT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_COMPACT = "yyyyMMddHHmmss";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FMT);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FMT);

    public static String formatDate(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATETIME_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String text) {
        return LocalDateTime.parse(text, DATETIME_FORMATTER);
    }

    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text, DATE_FORMATTER);
    }

    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime ofEpochMilli(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    /** 获取今天开始时间 00:00:00 */
    public static LocalDateTime todayStart() {
        return LocalDate.now().atStartOfDay();
    }

    /** 获取今天结束时间 23:59:59 */
    public static LocalDateTime todayEnd() {
        return LocalDate.now().atTime(LocalTime.MAX);
    }

    /** 计算两个时间之间的天数差 */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
    }
}
