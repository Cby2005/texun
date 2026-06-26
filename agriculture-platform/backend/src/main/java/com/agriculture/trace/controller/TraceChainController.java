package com.agriculture.trace.controller;

import com.agriculture.common.result.Result;
import com.agriculture.farm.env.entity.FarmEnvData;
import com.agriculture.knowledge.question.entity.KnowledgeQuestion;
import com.agriculture.trace.chain.entity.TraceChainBlock;
import com.agriculture.trace.chain.mapper.TraceChainBlockMapper;
import com.agriculture.trace.logistics.entity.TraceLogisticsRecord;
import com.agriculture.trace.logistics.mapper.TraceLogisticsRecordMapper;
import com.agriculture.trace.processing.entity.TraceProcessingRecord;
import com.agriculture.trace.processing.mapper.TraceProcessingRecordMapper;
import com.agriculture.trace.production.entity.TraceProductionRecord;
import com.agriculture.trace.production.mapper.TraceProductionRecordMapper;
import com.agriculture.trace.quality.entity.TraceQualityRecord;
import com.agriculture.trace.quality.mapper.TraceQualityRecordMapper;
import com.agriculture.trace.sales.entity.TraceSalesRecord;
import com.agriculture.trace.sales.mapper.TraceSalesRecordMapper;
import com.agriculture.trace.storage.entity.TraceStorageRecord;
import com.agriculture.trace.storage.mapper.TraceStorageRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trace/chain")
@RequiredArgsConstructor
@Tag(name = "溯源全链路追溯")
public class TraceChainController {

    private final TraceProductionRecordMapper productionMapper;
    private final TraceProcessingRecordMapper processingMapper;
    private final TraceStorageRecordMapper storageMapper;
    private final TraceLogisticsRecordMapper logisticsMapper;
    private final TraceQualityRecordMapper qualityMapper;
    private final TraceSalesRecordMapper salesMapper;
    private final TraceChainBlockMapper chainMapper;
    private final IService<FarmEnvData> envService;
    private final IService<KnowledgeQuestion> questionService;

    @GetMapping("/{batchNo}")
    @Operation(summary = "按批次号查询全链路追溯信息")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<Map<String, Object>> traceByBatchNo(@PathVariable String batchNo) {
        return Result.ok(buildTrace(batchNo));
    }

    @GetMapping("/public/{batchNo}")
    @Operation(summary = "公开批次追溯链路")
    public Result<Map<String, Object>> publicTraceByBatchNo(@PathVariable String batchNo) {
        return Result.ok(buildTrace(batchNo));
    }

    @GetMapping("/blocks/{batchNo}")
    @Operation(summary = "查询区块链存证记录")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<List<TraceChainBlock>> blocks(@PathVariable String batchNo) {
        return Result.ok(chainMapper.selectList(
                new LambdaQueryWrapper<TraceChainBlock>()
                        .eq(TraceChainBlock::getBatchNo, batchNo)
                        .orderByAsc(TraceChainBlock::getBlockHeight)));
    }

    private Map<String, Object> buildTrace(String batchNo) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("batchNo", batchNo);

        var environment = envService.list(new LambdaQueryWrapper<FarmEnvData>()
                .eq(FarmEnvData::getBatchNo, batchNo)
                .orderByDesc(FarmEnvData::getCreateTime)
                .last("LIMIT 50"));
        result.put("environment", environment);

        var production = productionMapper.selectList(
                new LambdaQueryWrapper<TraceProductionRecord>()
                        .eq(TraceProductionRecord::getBatchNo, batchNo)
                        .orderByDesc(TraceProductionRecord::getSowTime));
        result.put("production", production);

        var diagnosis = questionService.list(new LambdaQueryWrapper<KnowledgeQuestion>()
                .eq(KnowledgeQuestion::getBatchNo, batchNo)
                .orderByDesc(KnowledgeQuestion::getCreateTime));
        result.put("diagnosis", diagnosis);

        var processing = processingMapper.selectList(
                new LambdaQueryWrapper<TraceProcessingRecord>()
                        .eq(TraceProcessingRecord::getBatchNo, batchNo)
                        .orderByAsc(TraceProcessingRecord::getProcessingTime));
        result.put("processing", processing);

        var quality = qualityMapper.selectList(
                new LambdaQueryWrapper<TraceQualityRecord>()
                        .eq(TraceQualityRecord::getBatchNo, batchNo)
                        .orderByDesc(TraceQualityRecord::getQualityDate));
        result.put("quality", quality);

        var storage = storageMapper.selectList(
                new LambdaQueryWrapper<TraceStorageRecord>()
                        .eq(TraceStorageRecord::getBatchNo, batchNo)
                        .orderByAsc(TraceStorageRecord::getChangeTime));
        result.put("storage", storage);

        var logistics = logisticsMapper.selectList(
                new LambdaQueryWrapper<TraceLogisticsRecord>()
                        .eq(TraceLogisticsRecord::getBatchNo, batchNo)
                        .orderByAsc(TraceLogisticsRecord::getShipTime));
        result.put("logistics", logistics);

        var sales = salesMapper.selectList(
                new LambdaQueryWrapper<TraceSalesRecord>()
                        .eq(TraceSalesRecord::getBatchNo, batchNo)
                        .orderByDesc(TraceSalesRecord::getListingTime));
        result.put("sales", sales);

        var blocks = chainMapper.selectList(
                new LambdaQueryWrapper<TraceChainBlock>()
                        .eq(TraceChainBlock::getBatchNo, batchNo)
                        .orderByAsc(TraceChainBlock::getBlockHeight));
        result.put("chainBlocks", blocks);

        result.put("traceCompleteness", traceCompleteness(result));
        return result;
    }

    private int traceCompleteness(Map<String, Object> result) {
        String[] requiredLinks = {"environment", "production", "diagnosis", "quality", "logistics"};
        int complete = 0;
        for (String link : requiredLinks) {
            Object value = result.get(link);
            if (value instanceof Collection<?> collection && !collection.isEmpty()) {
                complete++;
            }
        }
        return complete * 100 / requiredLinks.length;
    }
}
