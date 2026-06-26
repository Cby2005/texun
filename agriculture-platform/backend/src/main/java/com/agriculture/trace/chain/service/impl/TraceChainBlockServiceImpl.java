package com.agriculture.trace.chain.service.impl;

import com.agriculture.trace.chain.entity.TraceChainBlock;
import com.agriculture.trace.chain.mapper.TraceChainBlockMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("chainBlockService")
public class TraceChainBlockServiceImpl extends ServiceImpl<TraceChainBlockMapper, TraceChainBlock> {
}
