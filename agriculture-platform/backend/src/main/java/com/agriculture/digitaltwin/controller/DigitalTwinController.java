package com.agriculture.digitaltwin.controller;

import com.agriculture.common.result.Result;
import com.agriculture.digitaltwin.entity.*;
import com.agriculture.digitaltwin.mapper.*;
import com.agriculture.greenhouse.entity.StrawberryPlantingRecord;
import com.agriculture.greenhouse.mapper.StrawberryPlantingRecordMapper;
import com.agriculture.trace.batch.entity.TraceBatch;
import com.agriculture.trace.batch.mapper.TraceBatchMapper;
import com.agriculture.trace.processing.entity.TraceProcessingRecord;
import com.agriculture.trace.processing.mapper.TraceProcessingRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/digital-twin")
@RequiredArgsConstructor
@Tag(name = "数字农田孪生巡检")
public class DigitalTwinController {

    private final StrawberryPlotMapper plotMapper;
    private final StrawberryPlantingRecordMapper plantingMapper;
    private final StrawberryHarvestRecordMapper harvestMapper;
    private final StrawberrySaleBatchMapper saleMapper;
    private final PickingRobotMapper robotMapper;
    private final TraceBatchMapper traceBatchMapper;
    private final TraceProcessingRecordMapper traceProcessingMapper;
    private final AbnormalPlantImageMapper abnormalImageMapper;

    /** ==================== 地块 ==================== */

    @GetMapping("/plots")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<List<Map<String, Object>>> getPlots() {
        List<StrawberryPlot> plots = plotMapper.selectList(
                new LambdaQueryWrapper<StrawberryPlot>().eq(StrawberryPlot::getDeleted, 0).orderByAsc(StrawberryPlot::getPlotCode));
        List<StrawberryPlantingRecord> plantings = plantingMapper.selectList(
                new LambdaQueryWrapper<StrawberryPlantingRecord>().eq(StrawberryPlantingRecord::getDeleted, 0));
        Map<Long, StrawberryPlantingRecord> plantingMap = plantings.stream()
                .collect(Collectors.toMap(StrawberryPlantingRecord::getId, p -> p, (a, b) -> a));

        List<Map<String, Object>> result = plots.stream().map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", p.getId()); m.put("plotCode", p.getPlotCode());
            m.put("areaCode", p.getAreaCode()); m.put("areaName", p.getAreaName());
            m.put("rowNo", p.getRowNo()); m.put("colNo", p.getColNo());
            m.put("plotStatus", p.getPlotStatus());
            StrawberryPlantingRecord rec = p.getCurrentPlantingRecordId() != null
                    ? plantingMap.get(p.getCurrentPlantingRecordId()) : null;
            m.put("currentPlanting", rec != null ? toPlantingMap(rec) : null);
            m.put("lastInspectionTime", p.getLastInspectionTime());
            return m;
        }).collect(Collectors.toList());
        return Result.ok(result);
    }

    @PostMapping("/plots/init")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<Integer> initPlots() {
        String[] areas = {"A区育苗区","B区开花区","C区结果区","D区异常修复区"};
        String[] codes = {"A","B","C","D"};
        int count = 0;
        for (int a = 0; a < areas.length; a++) {
            for (int i = 1; i <= 4; i++) {
                String plotCode = codes[a] + "-" + i;
                Long exist = plotMapper.selectCount(
                        new LambdaQueryWrapper<StrawberryPlot>().eq(StrawberryPlot::getPlotCode, plotCode));
                if (exist > 0) continue;
                StrawberryPlot p = new StrawberryPlot();
                p.setPlotCode(plotCode); p.setAreaCode(codes[a]); p.setAreaName(areas[a]);
                p.setRowNo(i); p.setColNo(1); p.setPlotStatus("空");
                p.setCoordinateX(BigDecimal.ZERO); p.setCoordinateY(BigDecimal.ZERO); p.setCoordinateZ(BigDecimal.ZERO);
                plotMapper.insert(p); count++;
            }
        }
        return Result.ok(count);
    }

    @GetMapping("/plots/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<Map<String, Object>> getPlotDetail(@PathVariable Long id) {
        StrawberryPlot p = plotMapper.selectById(id);
        if (p == null) return Result.ok(null);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("plot", p);
        if (p.getCurrentPlantingRecordId() != null) {
            m.put("planting", plantingMapper.selectById(p.getCurrentPlantingRecordId()));
            // 采收记录
            List<StrawberryHarvestRecord> harvests = harvestMapper.selectList(
                    new LambdaQueryWrapper<StrawberryHarvestRecord>()
                            .eq(StrawberryHarvestRecord::getPlantingRecordId, p.getCurrentPlantingRecordId())
                            .eq(StrawberryHarvestRecord::getDeleted, 0));
            m.put("harvests", harvests);
            // 销售批次
            List<StrawberrySaleBatch> sales = saleMapper.selectList(
                    new LambdaQueryWrapper<StrawberrySaleBatch>()
                            .eq(StrawberrySaleBatch::getPlantingRecordId, p.getCurrentPlantingRecordId())
                            .eq(StrawberrySaleBatch::getDeleted, 0));
            m.put("sales", sales);
        }
        return Result.ok(m);
    }

    /** ==================== 采摘 ==================== */

    @GetMapping("/picking-robots/available")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<List<PickingRobot>> availableRobots() {
        return Result.ok(robotMapper.selectList(
                new LambdaQueryWrapper<PickingRobot>().eq(PickingRobot::getDeleted, 0).eq(PickingRobot::getStatus, "空闲")));
    }

    @PostMapping("/harvest")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<Map<String, Object>> harvest(@RequestBody Map<String, Object> req) {
        Long plantingId = Long.valueOf(req.get("plantingRecordId").toString());
        Long robotId = req.get("robotId") != null ? Long.valueOf(req.get("robotId").toString()) : null;

        StrawberryPlantingRecord planting = plantingMapper.selectById(plantingId);
        if (planting == null) return Result.fail("种植记录不存在");
        if (!"成熟期".equals(planting.getGrowthStatus()))
            return Result.fail("只有成熟期可采摘");

        // 校验采摘数量
        int existingMature = planting.getMatureCount() != null ? planting.getMatureCount() : 0;
        // ponytail: fallback matureCount from plantCount if not set
        if (existingMature == 0 && planting.getPlantCount() != null) {
            existingMature = planting.getPlantCount();
            planting.setMatureCount(existingMature);
        }
        int harvestCount = Integer.parseInt(req.getOrDefault("harvestCount", "0").toString());
        BigDecimal harvestWeight = new BigDecimal(req.getOrDefault("harvestWeight", "0").toString());
        if (harvestCount <= 0) return Result.fail("采摘数量必须大于0");
        if (harvestWeight.compareTo(BigDecimal.ZERO) <= 0) return Result.fail("采摘重量必须大于0");
        if (harvestCount > existingMature) return Result.fail("采摘数量不能大于当前成熟数量(" + existingMature + "株)");

        String qualityGrade = req.getOrDefault("qualityGrade", "一级").toString();
        String operatorName = req.getOrDefault("operatorName", "").toString();
        String postHarvestAction = req.getOrDefault("postHarvestAction", "fresh_storage").toString();
        boolean generateTraceCode = Boolean.TRUE.equals(req.get("generateTraceCode"));

        // 1. 生成采收记录
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String harvestCode = "HV-" + dateStr + "-" + (System.currentTimeMillis() % 100000);
        StrawberryHarvestRecord h = new StrawberryHarvestRecord();
        h.setHarvestCode(harvestCode);
        h.setPlantingRecordId(plantingId);
        h.setPlotId(findPlotByPlantingId(plantingId) != null ? findPlotByPlantingId(plantingId).getId() : null);
        h.setGreenhouseId(findPlotByPlantingId(plantingId) != null ? findPlotByPlantingId(plantingId).getGreenhouseId() : null);
        h.setRobotId(robotId);
        h.setVariety(planting.getVariety());
        h.setHarvestCount(harvestCount);
        h.setHarvestWeight(harvestWeight);
        h.setQualityGrade(qualityGrade);
        h.setPostHarvestAction(postHarvestAction);
        h.setHarvestDate(LocalDate.now());
        h.setOperatorName(operatorName);
        h.setHarvestStatus("已采收");
        h.setRemark(req.getOrDefault("remark", "").toString());
        harvestMapper.insert(h);

        // 2. 更新种植记录计数器
        int newMature = existingMature - harvestCount;
        int harvestedCount = (planting.getHarvestedCount() != null ? planting.getHarvestedCount() : 0) + harvestCount;
        BigDecimal harvestedWeight = (planting.getHarvestedWeight() != null ? planting.getHarvestedWeight() : BigDecimal.ZERO).add(harvestWeight);
        planting.setMatureCount(newMature);
        planting.setHarvestedCount(harvestedCount);
        planting.setHarvestedWeight(harvestedWeight);
        plantingMapper.updateById(planting);

        // 3. 更新地块状态
        StrawberryPlot plot = findPlotByPlantingId(plantingId);
        if (plot != null) {
            plot.setPlotStatus(newMature > 0 ? "部分采摘" : "已采摘");
            plotMapper.updateById(plot);
        }

        // 4. 机器人恢复空闲
        if (robotId != null) {
            PickingRobot robot = robotMapper.selectById(robotId);
            if (robot != null) { robot.setStatus("空闲"); robot.setCurrentTaskId(null); robotMapper.updateById(robot); }
        }

        // 5. 生成 TraceBatch（联动溯源管理）
        TraceBatch batch = new TraceBatch();
        batch.setBatchNo("BATCH" + dateStr + String.format("%03d", (harvestMapper.selectCount(null) + 1)));
        batch.setProductCode("STB" + dateStr + String.format("%03d", (harvestMapper.selectCount(null) + 1)));
        batch.setProductName(planting.getVariety());
        batch.setQuantity(harvestWeight);
        batch.setUnit("kg");
        batch.setProductionDate(LocalDate.now());
        batch.setFarmId(plot != null ? plot.getGreenhouseId() : null);
        batch.setLandId(plot != null ? plot.getId() : null);
        batch.setCropId(plantingId);
        batch.setStatus(1); // 已完成
        // ponytail: 用 remark JSON 存联动信息
        batch.setRemark(String.format("{\"source\":\"digital_twin_harvest\",\"harvestRecordId\":%d,\"plotCode\":\"%s\",\"harvestCode\":\"%s\"}",
                h.getId(), planting.getPlotCode(), harvestCode));
        traceBatchMapper.insert(batch);

        String traceCode = null;
        if (generateTraceCode) {
            traceCode = "TRACE" + dateStr + String.format("%03d", (traceBatchMapper.selectCount(null) + 1));
        }

        // 6. 如果选择加工，生成加工记录
        Long processingId = null;
        if ("processing".equals(postHarvestAction)) {
            TraceProcessingRecord processing = new TraceProcessingRecord();
            processing.setBatchNo(batch.getBatchNo());
            processing.setProcessingMethod(postHarvestAction);
            processing.setProcessingTime(LocalDateTime.now());
            processing.setOperatorName(operatorName);
            processing.setRemark("数字孪生采摘联动，待加工");
            traceProcessingMapper.insert(processing);
            processingId = processing.getId();
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("harvestRecordId", h.getId());
        res.put("harvestCode", harvestCode);
        res.put("batchNo", batch.getBatchNo());
        res.put("batchId", batch.getId());
        res.put("traceCode", traceCode);
        res.put("processingId", processingId);
        res.put("remainingMatureCount", newMature);
        res.put("harvestedCount", harvestedCount);
        res.put("plotStatus", plot != null ? plot.getPlotStatus() : null);
        res.put("postHarvestAction", postHarvestAction);
        return Result.ok(res);
    }

    /** ==================== 售卖 ==================== */

    @PostMapping("/sale")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<Map<String, Object>> sale(@RequestBody Map<String, Object> req) {
        Long harvestId = Long.valueOf(req.get("harvestRecordId").toString());
        StrawberryHarvestRecord harvest = harvestMapper.selectById(harvestId);
        if (harvest == null) return Result.fail("采收记录不存在");

        Long plantingId = harvest.getPlantingRecordId();
        StrawberryPlantingRecord planting = plantingMapper.selectById(plantingId);

        BigDecimal saleWeight = new BigDecimal(req.getOrDefault("saleWeight", harvest.getHarvestWeight().toString()).toString());
        if (saleWeight.compareTo(harvest.getHarvestWeight()) > 0)
            return Result.fail("销售重量不能大于采收重量");

        BigDecimal unitPrice = new BigDecimal(req.getOrDefault("unitPrice", "30").toString());
        BigDecimal totalAmount = saleWeight.multiply(unitPrice).setScale(2, java.math.RoundingMode.HALF_UP);

        StrawberrySaleBatch batch = new StrawberrySaleBatch();
        batch.setBatchCode("SL-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + System.currentTimeMillis() % 10000);
        batch.setHarvestRecordId(harvestId);
        batch.setPlantingRecordId(plantingId);
        batch.setPlotId(harvest.getPlotId());
        batch.setVariety(planting != null ? planting.getVariety() : "");
        batch.setSaleWeight(saleWeight);
        batch.setUnitPrice(unitPrice);
        batch.setTotalAmount(totalAmount);
        batch.setCustomerName(req.getOrDefault("customerName", "").toString());
        batch.setSaleChannel(req.getOrDefault("saleChannel", "基地直销").toString());
        batch.setSaleDate(LocalDate.now());
        if (Boolean.TRUE.equals(req.get("generateTraceCode"))) {
            batch.setTraceCode("TRC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        batch.setSaleStatus("已售出");
        saleMapper.insert(batch);

        // 地块状态改为已售卖
        StrawberryPlot plot = findPlotByPlantingId(plantingId);
        if (plot != null) {
            plot.setPlotStatus("已售卖");
            plotMapper.updateById(plot);
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("saleId", batch.getId()); res.put("batchCode", batch.getBatchCode());
        res.put("totalAmount", totalAmount);
        return Result.ok(res);
    }

    @GetMapping("/sale-batches")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<List<StrawberrySaleBatch>> saleBatches() {
        return Result.ok(saleMapper.selectList(
                new LambdaQueryWrapper<StrawberrySaleBatch>().eq(StrawberrySaleBatch::getDeleted, 0).orderByDesc(StrawberrySaleBatch::getCreateTime)));
    }

    /** ==================== 统计 ==================== */

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<Map<String, Object>> statistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 地块统计
        List<StrawberryPlot> plots = plotMapper.selectList(
                new LambdaQueryWrapper<StrawberryPlot>().eq(StrawberryPlot::getDeleted, 0));
        Map<String, Long> plotStatusCount = plots.stream()
                .collect(Collectors.groupingBy(p -> p.getPlotStatus() != null ? p.getPlotStatus() : "空", LinkedHashMap::new, Collectors.counting()));
        stats.put("totalPlots", plots.size());
        stats.put("emptyPlots", plotStatusCount.getOrDefault("空", 0L));
        stats.put("plantedPlots", plots.size() - plotStatusCount.getOrDefault("空", 0L));

        // 种植统计
        List<StrawberryPlantingRecord> plantings = plantingMapper.selectList(
                new LambdaQueryWrapper<StrawberryPlantingRecord>().eq(StrawberryPlantingRecord::getDeleted, 0));
        int totalPlants = 0; double totalCost = 0; int matureCount = 0; int anomalyCount = 0;
        for (StrawberryPlantingRecord r : plantings) {
            totalPlants += (r.getPlantCount() != null ? r.getPlantCount() : 0);
            totalCost += (r.getTotalCost() != null ? r.getTotalCost().doubleValue() : 0);
            if ("成熟期".equals(r.getGrowthStatus())) matureCount++;
            if ("异常".equals(r.getGrowthStatus())) anomalyCount++;
        }
        stats.put("totalPlants", totalPlants);
        stats.put("totalCost", BigDecimal.valueOf(totalCost).setScale(2, java.math.RoundingMode.HALF_UP));
        stats.put("maturePlots", matureCount);
        stats.put("anomalyPlots", anomalyCount);
        stats.put("plantingRecordCount", plantings.size());

        // 采收统计
        List<StrawberryHarvestRecord> harvests = harvestMapper.selectList(
                new LambdaQueryWrapper<StrawberryHarvestRecord>().eq(StrawberryHarvestRecord::getDeleted, 0));
        BigDecimal totalHarvestWeight = harvests.stream()
                .map(h -> h.getHarvestWeight() != null ? h.getHarvestWeight() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("harvestRecordCount", harvests.size());
        stats.put("totalHarvestWeight", totalHarvestWeight);

        // 销售统计
        List<StrawberrySaleBatch> sales = saleMapper.selectList(
                new LambdaQueryWrapper<StrawberrySaleBatch>().eq(StrawberrySaleBatch::getDeleted, 0));
        BigDecimal totalSaleAmount = sales.stream()
                .map(s -> s.getTotalAmount() != null ? s.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long traceCount = sales.stream().filter(s -> s.getTraceCode() != null && !s.getTraceCode().isEmpty()).count();
        stats.put("saleBatchCount", sales.size());
        stats.put("totalSaleAmount", totalSaleAmount);
        stats.put("traceCodeCount", traceCount);

        return Result.ok(stats);
    }

    // ==================== helper ====================
    private Map<String, Object> toPlantingMap(StrawberryPlantingRecord r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", r.getId()); m.put("areaCode", r.getAreaCode()); m.put("areaName", r.getAreaName());
        m.put("plotCode", r.getPlotCode()); m.put("variety", r.getVariety());
        m.put("plantCount", r.getPlantCount()); m.put("unitCost", r.getUnitCost());
        m.put("totalCost", r.getTotalCost()); m.put("plantingDate", r.getPlantingDate());
        m.put("growthStatus", r.getGrowthStatus()); m.put("managerName", r.getManagerName());
        m.put("matureCount", r.getMatureCount()); m.put("harvestedCount", r.getHarvestedCount());
        m.put("harvestedWeight", r.getHarvestedWeight()); m.put("expectedYield", r.getExpectedYield());
        return m;
    }

    private StrawberryPlot findPlotByPlantingId(Long plantingId) {
        StrawberryPlantingRecord rec = plantingMapper.selectById(plantingId);
        if (rec == null || rec.getPlotCode() == null) return null;
        return plotMapper.selectOne(
                new LambdaQueryWrapper<StrawberryPlot>().eq(StrawberryPlot::getPlotCode, rec.getPlotCode()).eq(StrawberryPlot::getDeleted, 0));
    }

    /** ==================== 异常植株图片 ==================== */

    @PostMapping("/abnormal-images/save")
    @PreAuthorize("hasAnyRole('ADMIN','FARM_ADMIN','FARMER')")
    public Result<Map<String, Object>> saveAbnormalImage(@RequestBody Map<String, Object> params) {
        AbnormalPlantImage img = new AbnormalPlantImage();
        img.setImageUrl((String) params.get("imageUrl"));
        img.setAbnormalReason((String) params.get("abnormalReason"));
        img.setDetectResult((String) params.get("detectResult"));
        img.setGrowthStage((String) params.get("growthStage"));
        img.setAreaCode((String) params.get("areaCode"));
        img.setPlotCode((String) params.get("plotCode"));
        img.setConfidence(params.get("confidence") != null ? new BigDecimal(params.get("confidence").toString()) : null);

        Object pid = params.get("plotId");
        if (pid != null) img.setPlotId(Long.valueOf(pid.toString()));
        Object prid = params.get("plantingRecordId");
        if (prid != null) img.setPlantingRecordId(Long.valueOf(prid.toString()));
        Object bid = params.get("baseId");
        if (bid != null) img.setBaseId(Long.valueOf(bid.toString()));
        Object gid = params.get("greenhouseId");
        if (gid != null) img.setGreenhouseId(Long.valueOf(gid.toString()));

        img.setSavedBy(com.agriculture.common.security.UserContext.getCurrentUserId());
        img.setSaveTime(LocalDateTime.now());
        img.setCreateTime(LocalDateTime.now());
        img.setUpdateTime(LocalDateTime.now());
        abnormalImageMapper.insert(img);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id", img.getId());
        res.put("imageUrl", img.getImageUrl());
        return Result.ok(res);
    }

    @GetMapping("/plots/{plotId}/abnormal-images")
    public Result<List<AbnormalPlantImage>> listAbnormalImages(@PathVariable Long plotId) {
        List<AbnormalPlantImage> list = abnormalImageMapper.selectList(
                new LambdaQueryWrapper<AbnormalPlantImage>().eq(AbnormalPlantImage::getPlotId, plotId)
                        .orderByDesc(AbnormalPlantImage::getCreateTime));
        return Result.ok(list);
    }

    @GetMapping("/abnormal-images/{id}")
    public Result<AbnormalPlantImage> getAbnormalImage(@PathVariable Long id) {
        return Result.ok(abnormalImageMapper.selectById(id));
    }
}
