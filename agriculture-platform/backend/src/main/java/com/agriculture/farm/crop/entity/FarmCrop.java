package com.agriculture.farm.crop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("farm_crop")
public class FarmCrop {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private String icon;
    private Integer sortOrder;
    /** 0作物 1养殖 2其他 */
    private Integer type;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createTime;
}
