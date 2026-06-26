package com.agriculture.trace.controller;

import com.agriculture.common.result.Result;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

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

    @GetMapping("/{batchNo}")
    @Operation(summary = "按批次号查询全链路溯源信息")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<Map<String, Object>> traceByBatchNo(@PathVariable String batchNo) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("batchNo", batchNo);

        // 种植记录
        var production = productionMapper.selectList(
            new LambdaQueryWrapper<TraceProductionRecord>().eq(TraceProductionRecord::getBatchNo, batchNo).orderByDesc(TraceProductionRecord::getSowTime));
        result.put("production", production);

        // 加工记录
        var processing = processingMapper.selectList(
            new LambdaQueryWrapper<TraceProcessingRecord>().eq(TraceProcessingRecord::getBatchNo, batchNo).orderByAsc(TraceProcessingRecord::getProcessingTime));
        result.put("processing", processing);

        // 质检记录
        var quality = qualityMapper.selectList(
            new LambdaQueryWrapper<TraceQualityRecord>().eq(TraceQualityRecord::getBatchNo, batchNo).orderByDesc(TraceQualityRecord::getQualityDate));
        result.put("quality", quality);

        // 仓储记录
        var storage = storageMapper.selectList(
            new LambdaQueryWrapper<TraceStorageRecord>().eq(TraceStorageRecord::getBatchNo, batchNo).orderByAsc(TraceStorageRecord::getChangeTime));
        result.put("storage", storage);

        // 物流记录
        var logistics = logisticsMapper.selectList(
            new LambdaQueryWrapper<TraceLogisticsRecord>().eq(TraceLogisticsRecord::getBatchNo, batchNo).orderByAsc(TraceLogisticsRecord::getShipTime));
        result.put("logistics", logistics);

        // 销售记录
        var sales = salesMapper.selectList(
            new LambdaQueryWrapper<TraceSalesRecord>().eq(TraceSalesRecord::getBatchNo, batchNo).orderByDesc(TraceSalesRecord::getListingTime));
        result.put("sales", sales);

        // 区块链存证
        var blocks = chainMapper.selectList(
            new LambdaQueryWrapper<TraceChainBlock>().eq(TraceChainBlock::getBatchNo, batchNo).orderByAsc(TraceChainBlock::getBlockHeight));
        result.put("chainBlocks", blocks);

        return Result.ok(result);
    }

    @GetMapping("/blocks/{batchNo}")
    @Operation(summary = "查询区块链存证记录")
    @PreAuthorize("hasAnyRole('ADMIN','TRACE_ADMIN')")
    public Result<List<TraceChainBlock>> blocks(@PathVariable String batchNo) {
        return Result.ok(chainMapper.selectList(
            new LambdaQueryWrapper<TraceChainBlock>().eq(TraceChainBlock::getBatchNo, batchNo).orderByAsc(TraceChainBlock::getBlockHeight)));
    }
}
