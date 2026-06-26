package com.agri.common.pattern.chain;

/**
 * 责任链模式：故障告警处理器接口
 * 支持逐级上报
 */
public interface AlarmHandler {
    /** 设置下一个处理器 */
    AlarmHandler setNext(AlarmHandler next);
    /** 处理告警 */
    AlarmResult handle(AlarmContext context);
    /** 处理器名称 */
    String name();
    /** 是否支持处理该级别告警 */
    boolean support(String level);
}
