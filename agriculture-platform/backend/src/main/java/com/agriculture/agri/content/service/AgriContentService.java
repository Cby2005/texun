package com.agriculture.agri.content.service;

import com.agriculture.agri.content.dto.AgriContentQueryDTO;
import com.agriculture.agri.content.entity.AgriContent;
import com.agriculture.common.result.PageResult;

public interface AgriContentService {
    PageResult<AgriContent> list(AgriContentQueryDTO query);
    AgriContent getById(Long id);
    void add(AgriContent content);
    void update(AgriContent content);
    void delete(Long id);
    void publish(Long id);
    void offline(Long id);
    PageResult<AgriContent> recommend(AgriContentQueryDTO query);
    PageResult<AgriContent> hot(AgriContentQueryDTO query);
}
