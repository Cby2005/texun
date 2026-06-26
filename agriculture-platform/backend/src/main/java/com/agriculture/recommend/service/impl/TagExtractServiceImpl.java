package com.agriculture.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.agriculture.common.exception.BizException;
import com.agriculture.knowledge.article.entity.KnowledgeArticle;
import com.agriculture.knowledge.article.mapper.KnowledgeArticleMapper;
import com.agriculture.knowledge.article_tag.entity.KnowledgeArticleTag;
import com.agriculture.knowledge.article_tag.mapper.KnowledgeArticleTagMapper;
import com.agriculture.knowledge.tag.entity.KnowledgeTag;
import com.agriculture.knowledge.tag.mapper.KnowledgeTagMapper;
import com.agriculture.recommend.service.TagExtractService;
import com.agriculture.recommend.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagExtractServiceImpl implements TagExtractService {

    private static final String SAFETY_TIP = "该文章涉及农药或病虫害防治内容，实际用药需结合当地农技部门指导，避免超量使用。";

    private static final Map<String, String> KEYWORD_TYPES = new LinkedHashMap<>();
    static {
        for (String tag : List.of("玉米", "小麦", "水稻", "番茄", "苹果", "大豆", "花生"))
            KEYWORD_TYPES.put(tag, "作物");
        for (String tag : List.of("草地贪夜蛾", "赤霉病", "白粉病", "根腐病", "锈病", "蚜虫"))
            KEYWORD_TYPES.put(tag, "病虫害");
        for (String tag : List.of("施肥", "灌溉", "育苗", "防治", "农机操作", "田间管理"))
            KEYWORD_TYPES.put(tag, "技术");
        for (String tag : List.of("河南", "山东", "东北", "华北", "华中"))
            KEYWORD_TYPES.put(tag, "地区");
        for (String tag : List.of("春耕", "夏管", "秋收", "冬季管理"))
            KEYWORD_TYPES.put(tag, "季节");
        for (String tag : List.of("补贴", "保险", "农机购置", "惠农政策"))
            KEYWORD_TYPES.put(tag, "政策");
    }

    private final KnowledgeArticleMapper articleMapper;
    private final KnowledgeTagMapper tagMapper;
    private final KnowledgeArticleTagMapper articleTagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TagVO> extractTags(Long articleId) {
        KnowledgeArticle article = articleMapper.selectById(articleId);
        if (article == null) throw new BizException(400, "文章不存在");

        String text = joinText(article.getTitle(), article.getSummary(), article.getContent());
        List<KnowledgeTag> matchedTags = KEYWORD_TYPES.entrySet().stream()
                .filter(e -> text.contains(e.getKey()))
                .map(e -> findOrCreateTag(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        boolean hasPest = text.contains("农药") || text.contains("防治") || text.contains("用药") || text.contains("杀虫剂");
        if (hasPest) {
            matchedTags.add(findOrCreateTag("病虫害防治", "技术"));
        }

        articleTagMapper.delete(new LambdaQueryWrapper<KnowledgeArticleTag>().eq(KnowledgeArticleTag::getArticleId, articleId));
        for (KnowledgeTag tag : matchedTags.stream().distinct().collect(Collectors.toList())) {
            KnowledgeArticleTag relation = new KnowledgeArticleTag();
            relation.setArticleId(articleId);
            relation.setTagId(tag.getId());
            relation.setWeight(calcWeight(tag, text));
            articleTagMapper.insert(relation);
        }

        return matchedTags.stream().distinct().map(this::toVO).collect(Collectors.toList());
    }

    private KnowledgeTag findOrCreateTag(String name, String type) {
        KnowledgeTag tag = tagMapper.selectOne(new LambdaQueryWrapper<KnowledgeTag>().eq(KnowledgeTag::getName, name));
        if (tag == null) { tag = new KnowledgeTag(); tag.setName(name); tag.setTagType(type); tagMapper.insert(tag); }
        else if (!StringUtils.hasText(tag.getTagType())) { tag.setTagType(type); tagMapper.updateById(tag); }
        return tag;
    }

    private double calcWeight(KnowledgeTag tag, String text) {
        int first = text.indexOf(tag.getName());
        return first >= 0 && first < 120 ? 1.5 : 1.0;
    }

    private String joinText(String... parts) {
        List<String> list = new ArrayList<>();
        for (String p : parts) if (p != null) list.add(p);
        return String.join(" ", list);
    }

    private TagVO toVO(KnowledgeTag tag) {
        TagVO vo = new TagVO(); vo.setId(tag.getId()); vo.setTagName(tag.getName()); vo.setTagType(tag.getTagType());
        return vo;
    }
}
