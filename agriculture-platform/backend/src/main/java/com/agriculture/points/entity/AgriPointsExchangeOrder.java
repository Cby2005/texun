package com.agriculture.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_points_exchange_order")
public class AgriPointsExchangeOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long goodsId;
    private String goodsName;
    private Integer requiredPoints;
    private Integer exchangeQuantity;
    private Integer totalPoints;
    /** PENDING/PROCESSING/COMPLETED/CANCELLED/REFUNDED */
    private String orderStatus;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private LocalDateTime exchangeTime;
    private LocalDateTime finishTime;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
