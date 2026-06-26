package com.agriculture.common.result;

import lombok.Data;
import java.util.List;

/**
 * 分页响应
 */
@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private int pageNum;
    private int pageSize;

    public PageResult() {}

    public PageResult(List<T> records, long total) {
        this.records = records;
        this.total = total;
    }

    public PageResult(List<T> records, long total, int pageNum, int pageSize) {
        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
