package com.agriculture.drone.image.controller;

import com.agriculture.common.result.Result;
import com.agriculture.common.result.PageResult;
import com.agriculture.drone.image.dto.ImageAddRequest;
import com.agriculture.drone.image.entity.DroneInspectionImage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/drone/image")
@RequiredArgsConstructor
@Tag(name = "巡检图像记录")
public class DroneInspectionImageController {

    private final IService<DroneInspectionImage> service;

    @GetMapping("/list")
    public Result<PageResult<DroneInspectionImage>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long taskId) {
        LambdaQueryWrapper<DroneInspectionImage> qw = new LambdaQueryWrapper<>();
        if (taskId != null) qw.eq(DroneInspectionImage::getTaskId, taskId);
        qw.orderByDesc(DroneInspectionImage::getCreateTime);
        Page<DroneInspectionImage> page = service.page(new Page<>(pageNum, pageSize), qw);
        return Result.ok(new PageResult<>(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @PostMapping("/add")
    public Result<DroneInspectionImage> add(@RequestBody ImageAddRequest req) {
        DroneInspectionImage img = new DroneInspectionImage();
        img.setTaskId(req.getTaskId());
        img.setImageUrl(req.getImageUrl());
        img.setCapturePoint(req.getCapturePoint());
        img.setDetectResult("PENDING");
        service.save(img);
        return Result.ok(img);
    }

    @PostMapping("/detect/{id}")
    public Result<DroneInspectionImage> detect(@PathVariable Long id) {
        DroneInspectionImage img = service.getById(id);
        if (img == null) return Result.fail(400, "图像记录不存在");
        // 模拟识别结果
        double rand = Math.random();
        if (rand < 0.4) {
            img.setDetectResult("HEALTHY");
            img.setDiseaseType("");
            img.setConfidence(BigDecimal.valueOf(0.85 + Math.random() * 0.14));
            img.setSuggestion("草莓生长正常，无需处理");
        } else {
            String[] diseases = {"灰霉病", "白粉病", "红蜘蛛", "蚜虫", "炭疽病", "叶斑病"};
            String disease = diseases[(int) (Math.random() * diseases.length)];
            img.setDetectResult("DISEASE");
            img.setDiseaseType(disease);
            img.setConfidence(BigDecimal.valueOf(0.7 + Math.random() * 0.25));
            img.setSuggestion("检测到" + disease + "，建议立即采取防治措施，可生成对应农事任务");
        }
        service.updateById(img);
        return Result.ok(img);
    }
}
