package com.agriculture.recommend.service;

import com.agriculture.common.result.PageResult;
import com.agriculture.recommend.vo.RecommendArticleVO;
import com.agriculture.recommend.vo.RecommendLogVO;
import java.util.List;

public interface RecommendService {
    List<RecommendArticleVO> recommendArticles(Long userId, Integer limit);
    void saveRecommendLogs(Long userId, List<RecommendArticleVO> articles);
    PageResult<RecommendLogVO> listLogs(long page, long size, Long userId);
}
