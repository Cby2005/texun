package com.agri.common.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页查询基类
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Min(value = 1, message = "页码最小为1")
    private int pageNum = 1;

    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页条数最大为100")
    private int pageSize = 10;

    private String orderBy;

    private boolean asc = true;

    /**
     * 计算偏移量
     */
    public long getOffset() {
        return (long) (pageNum - 1) * pageSize;
    }
}
