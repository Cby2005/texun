package com.agri.common.constant;

/**
 * 系统常量
 */
public final class Constants {

    private Constants() {}

    // ==================== 用户相关 ====================
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-Username";
    public static final String ROLE_HEADER = "X-Role";
    public static final String TENANT_ID_HEADER = "X-Tenant-Id";

    // ==================== Redis Key 前缀 ====================
    public static final String REDIS_USER_TOKEN = "agri:user:token:";
    public static final String REDIS_USER_INFO = "agri:user:info:";
    public static final String REDIS_USER_PERM = "agri:user:perm:";
    public static final String REDIS_VEHICLE_STATUS = "agri:vehicle:status:";
    public static final String REDIS_VEHICLE_LOCATION = "agri:vehicle:location:";
    public static final String REDIS_VEHICLE_TELEMETRY = "agri:vehicle:telemetry:";
    public static final String REDIS_WEATHER_CACHE = "agri:weather:cache:";
    public static final String REDIS_MONITOR_LATEST = "agri:monitor:latest:";
    public static final String REDIS_TASK_LOCK = "agri:task:lock:";
    public static final String REDIS_CHAIN_LAST = "agri:chain:last:";
    public static final String REDIS_CAPTCHA = "agri:captcha:";
    public static final String REDIS_RATE_LIMIT = "agri:rate:limit:";

    // ==================== Kafka Topic ====================
    public static final String TOPIC_TELEMETRY_RAW = "vehicle.telemetry.raw";
    public static final String TOPIC_TELEMETRY_PARSED = "vehicle.telemetry.parsed";
    public static final String TOPIC_VEHICLE_ALARM = "vehicle.alarm";
    public static final String TOPIC_COMMAND_ACK = "vehicle.command.ack";
    public static final String TOPIC_VEHICLE_LOCATION = "vehicle.location";
    public static final String TOPIC_TASK_EVENT = "task.event";
    public static final String TOPIC_CHAIN_STORE = "chain.store";
    public static final String TOPIC_MONITOR_DATA = "monitor.data";

    // ==================== 角色编码 ====================
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_FARM_MANAGER = "FARM_MANAGER";
    public static final String ROLE_DEVICE_OPERATOR = "DEVICE_OPERATOR";
    public static final String ROLE_SYSTEM_MAINTAINER = "SYSTEM_MAINTAINER";
    public static final String ROLE_TRACE_MANAGER = "TRACE_MANAGER";
    public static final String ROLE_EXPERT = "EXPERT";
    public static final String ROLE_FARMER = "FARMER";
    public static final String ROLE_CONSUMER = "CONSUMER";

    // ==================== 设备状态 ====================
    public static final String VEHICLE_IDLE = "IDLE";
    public static final String VEHICLE_ONLINE = "ONLINE";
    public static final String VEHICLE_OFFLINE = "OFFLINE";
    public static final String VEHICLE_WORKING = "WORKING";
    public static final String VEHICLE_FAULT = "FAULT";
    public static final String VEHICLE_MAINTENANCE = "MAINTENANCE";
    public static final String VEHICLE_SCRAPPED = "SCRAPPED";

    // ==================== 指令状态 ====================
    public static final String CMD_CREATED = "CREATED";
    public static final String CMD_SENT = "SENT";
    public static final String CMD_RECEIVED = "RECEIVED";
    public static final String CMD_EXECUTING = "EXECUTING";
    public static final String CMD_SUCCESS = "SUCCESS";
    public static final String CMD_FAILED = "FAILED";
    public static final String CMD_TIMEOUT = "TIMEOUT";
    public static final String CMD_CANCELLED = "CANCELLED";

    // ==================== 任务类型 ====================
    public static final String TASK_SOWING = "SOWING";
    public static final String TASK_IRRIGATION = "IRRIGATION";
    public static final String TASK_FERTILIZATION = "FERTILIZATION";
    public static final String TASK_SPRAYING = "SPRAYING";
    public static final String TASK_HARVESTING = "HARVESTING";
    public static final String TASK_INSPECTION = "INSPECTION";
    public static final String TASK_TRANSPORT = "TRANSPORT";

    // ==================== 任务状态 ====================
    public static final String TASK_PENDING = "PENDING";
    public static final String TASK_ASSIGNED = "ASSIGNED";
    public static final String TASK_EXECUTING = "EXECUTING";
    public static final String TASK_PAUSED = "PAUSED";
    public static final String TASK_COMPLETED = "COMPLETED";
    public static final String TASK_CANCELLED = "CANCELLED";
    public static final String TASK_FAILED = "FAILED";

    // ==================== WebSocket ====================
    public static final String WS_TOPIC_VEHICLE = "/topic/vehicle/";
    public static final String WS_TOPIC_TASK = "/topic/task/";
    public static final String WS_TOPIC_ALARM = "/topic/alarm";
    public static final String WS_TOPIC_MONITOR = "/topic/monitor/";

    // ==================== 坐标系 ====================
    public static final String COORD_GCJ02 = "GCJ-02";
    public static final String COORD_WGS84 = "WGS-84";

    // ==================== TraceId ====================
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String TRACE_ID_KEY = "traceId";

    // ==================== 幂等 ====================
    public static final String IDEMPOTENT_TOKEN_HEADER = "Idempotent-Token";

    // ==================== 分页默认值 ====================
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
}
