package com.agriculture.farm.enterprise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("farm_enterprise")
public class FarmEnterprise {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 基地编码 */
    private String baseCode;
    /** 基地名称 */
    private String baseName;
    /** 所属企业/合作社 */
    private String enterpriseName;
    /** 基地类型 */
    private String baseType;
    /** 主栽作物 */
    private String mainCrop;
    /** 草莓品种 */
    private String strawberryVariety;
    /** 所在地区 */
    private String region;
    /** 详细地址 */
    private String detailAddress;
    /** 基地负责人 */
    private String managerName;
    /** 联系电话 */
    private String managerPhone;
    /** 经度（可空，不在前端展示） */
    private BigDecimal lng;
    /** 纬度（可空，不在前端展示） */
    private BigDecimal lat;
    /** 总面积（亩） */
    private BigDecimal totalArea;
    /** 温室数量 */
    private Integer greenhouseCount;
    /** 草莓种植面积（亩） */
    private BigDecimal plantingArea;
    /** 种植模式 */
    private String plantingMode;
    /** 灌溉方式 */
    private String irrigationMode;
    /** 年预计产量（kg） */
    private BigDecimal expectedYield;
    /** 是否接入环境监测 */
    private Integer enableMonitor;
    /** 是否启用病虫害识别 */
    private Integer enableDiseaseDetect;
    /** 是否启用溯源 */
    private Integer enableTrace;
    /** 管理员用户ID */
    private Long adminId;
    /** 状态：0正常 1停用 2建设中 */
    private Integer status;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
