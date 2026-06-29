package com.agriculture.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agri_points_goods")
public class AgriPointsGoods {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String goodsName;
    private String goodsCategory;
    private String goodsImage;
    private String goodsDesc;
    private Integer requiredPoints;
    private Integer stock;
    private Integer exchangeLimit;
    /** ON_SALE/OFF_SALE/SOLD_OUT */
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
