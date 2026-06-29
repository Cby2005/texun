package com.agriculture.greenhouse.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("strawberry_planting_record")
public class StrawberryPlantingRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 区域编码 A/B/C/D */
    private String areaCode;
    /** 区域名称 */
    private String areaName;
    /** 地块编号 A-1/B-2 等 */
    private String plotCode;
    /** 草莓品种 */
    private String variety;
    /** 种植数量(株) */
    private Integer plantCount;
    /** 当前成熟数量(株) */
    private Integer matureCount;
    /** 累计已采摘数量(株) */
    private Integer harvestedCount;
    /** 累计采摘重量(kg) */
    private BigDecimal harvestedWeight;
    /** 预计产量(kg) */
    private BigDecimal expectedYield;
    /** 单株成本(元/株) */
    private BigDecimal unitCost;
    /** 总成本 */
    private BigDecimal totalCost;
    /** 种植日期 */
    private LocalDate plantingDate;
    /** 生长状态 */
    private String growthStatus;
    /** 负责人 */
    private String managerName;
    /** 备注 */
    private String remark;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
