package com.agriculture.recommend.service;

import com.agriculture.recommend.vo.TagVO;
import java.util.List;

public interface TagExtractService {
    List<TagVO> extractTags(Long articleId);
}
