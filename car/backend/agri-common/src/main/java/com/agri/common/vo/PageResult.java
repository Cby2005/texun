package com.agri.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果封装
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;
    private List<T> records;

    public PageResult() {
        this.records = Collections.emptyList();
    }

    public PageResult(long total, int pageNum, int pageSize, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.records = records;
        this.pages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
    }

    public static <T> PageResult<T> of(long total, int pageNum, int pageSize, List<T> records) {
        return new PageResult<>(total, pageNum, pageSize, records);
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }
}
