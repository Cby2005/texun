package com.agriculture.agri.content.service.impl;

import com.agriculture.agri.content.dto.AgriContentQueryDTO;
import com.agriculture.agri.content.entity.AgriContent;
import com.agriculture.agri.content.mapper.AgriContentMapper;
import com.agriculture.agri.content.service.AgriContentService;
import com.agriculture.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AgriContentServiceImpl implements AgriContentService {

    private final AgriContentMapper mapper;

    @Override
    public PageResult<AgriContent> list(AgriContentQueryDTO query) {
        LambdaQueryWrapper<AgriContent> qw = buildQuery(query);
        qw.orderByDesc(AgriContent::getPublishTime);
        Page<AgriContent> page = mapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), qw);
        return new PageResult<>(page.getRecords(), page.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public AgriContent getById(Long id) { return mapper.selectById(id); }

    @Override
    @Transactional
    public void add(AgriContent content) {
        content.setViewCount(0); content.setCommentCount(0); content.setLikeCount(0);
        content.setCreateTime(LocalDateTime.now()); content.setUpdateTime(LocalDateTime.now());
        if (content.getPublishStatus() == null) content.setPublishStatus("PUBLISHED");
        if (content.getPublishTime() == null) content.setPublishTime(LocalDateTime.now());
        mapper.insert(content);
    }

    @Override
    @Transactional
    public void update(AgriContent content) {
        content.setUpdateTime(LocalDateTime.now());
        mapper.updateById(content);
    }

    @Override
    @Transactional
    public void delete(Long id) { mapper.deleteById(id); }

    @Override
    @Transactional
    public void publish(Long id) {
        AgriContent c = mapper.selectById(id);
        if (c != null) { c.setPublishStatus("PUBLISHED"); c.setPublishTime(LocalDateTime.now()); c.setUpdateTime(LocalDateTime.now()); mapper.updateById(c); }
    }

    @Override
    @Transactional
    public void offline(Long id) {
        AgriContent c = mapper.selectById(id);
        if (c != null) { c.setPublishStatus("OFFLINE"); c.setUpdateTime(LocalDateTime.now()); mapper.updateById(c); }
    }

    @Override
    public PageResult<AgriContent> recommend(AgriContentQueryDTO query) {
        LambdaQueryWrapper<AgriContent> qw = buildQuery(query);
        qw.eq(AgriContent::getRecommendFlag, 1).orderByDesc(AgriContent::getPublishTime);
        Page<AgriContent> p = mapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), qw);
        return new PageResult<>(p.getRecords(), p.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public PageResult<AgriContent> hot(AgriContentQueryDTO query) {
        LambdaQueryWrapper<AgriContent> qw = buildQuery(query);
        qw.eq(AgriContent::getHotFlag, 1).orderByDesc(AgriContent::getViewCount);
        Page<AgriContent> p = mapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), qw);
        return new PageResult<>(p.getRecords(), p.getTotal(), query.getPageNum(), query.getPageSize());
    }

    private LambdaQueryWrapper<AgriContent> buildQuery(AgriContentQueryDTO query) {
        LambdaQueryWrapper<AgriContent> qw = new LambdaQueryWrapper<>();
        qw.eq(AgriContent::getPublishStatus, "PUBLISHED");
        if (query.getContentType() != null && !query.getContentType().isBlank())
            qw.eq(AgriContent::getContentType, query.getContentType());
        if (query.getCategory() != null && !query.getCategory().isBlank())
            qw.eq(AgriContent::getCategory, query.getCategory());
        if (query.getKeyword() != null && !query.getKeyword().isBlank())
            qw.and(w -> w.like(AgriContent::getTitle, query.getKeyword()).or().like(AgriContent::getSummary, query.getKeyword()));
        if (query.getRecommendFlag() != null) qw.eq(AgriContent::getRecommendFlag, query.getRecommendFlag());
        if (query.getHotFlag() != null) qw.eq(AgriContent::getHotFlag, query.getHotFlag());
        return qw;
    }
}
