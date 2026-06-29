package com.agriculture.drone.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 地理距离计算 - Haversine公式
 */
public class GeoDistanceUtil {

    /** 地球半径(米) */
    private static final double EARTH_RADIUS = 6_371_000.0;

    /** 默认无人机速度 m/s */
    public static final double DEFAULT_SPEED = 1.5;

    /**
     * Haversine公式计算两点间地表距离(米)
     */
    public static double haversine(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    /**
     * 预计时间(秒) = 距离 / 速度
     */
    public static BigDecimal estimatedTimeSeconds(double distance) {
        return BigDecimal.valueOf(distance / DEFAULT_SPEED).setScale(0, RoundingMode.HALF_UP);
    }

    public static BigDecimal toBigDecimal(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP);
    }
}
