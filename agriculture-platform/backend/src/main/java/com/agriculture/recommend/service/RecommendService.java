package com.agriculture.recommend.service;

import com.agriculture.common.result.PageResult;
import com.agriculture.recommend.dto.BehaviorReportDTO;
import com.agriculture.recommend.vo.ContentRecommendVO;
import com.agriculture.recommend.vo.RecommendArticleVO;
import com.agriculture.recommend.vo.RecommendLogVO;
import com.agriculture.recommend.vo.UserProfileVO;

import java.util.List;

public interface RecommendService {

    /** 行为上报 */
    void reportBehavior(BehaviorReportDTO dto);

    /** 农技精选推荐（根据用户画像排序） */
    PageResult<ContentRecommendVO> recommendContent(Long userId, Integer pageNum, Integer pageSize);

    /** 相关技术（同分类/同标签） */
    List<ContentRecommendVO> similarContent(Long contentId, Integer pageSize);

    /** 获取用户画像 */
    UserProfileVO getUserProfile(Long userId);

    /** 以下为兼容旧接口 */
    List<RecommendArticleVO> recommendArticles(Long userId, Integer limit);

    void saveRecommendLogs(Long userId, List<RecommendArticleVO> articles);

    PageResult<RecommendLogVO> listLogs(long page, long size, Long userId);
}
