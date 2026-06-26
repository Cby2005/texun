package com.agriculture.farm.enterprise.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("farm_enterprise")
public class FarmEnterprise {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String address;
    private java.math.BigDecimal lng;
    private java.math.BigDecimal lat;
    private String icon;
    private String contactName;
    private String contactPhone;
    private Long adminId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
