package com.agriculture.yolo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_yolo_result")
public class KnowledgeYoloResult {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionId;
    private Long imageId;
    private String modelId;
    private String diseaseName;
    private BigDecimal confidence;
    private String predictionJson;
    private String resultImageUrl;
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}
