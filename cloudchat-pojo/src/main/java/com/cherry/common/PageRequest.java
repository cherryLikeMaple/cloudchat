package com.cherry.common;

import lombok.Data;

/**
 * @author cherry
 */
@Data
public class PageRequest {

    /**
     * 当前是第几页.
     */
    private int page;

    /**
     * 一页有多少条数据
     */
    private int pageSize;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 默认升序
     */
    private String sortOrder = "ascend";
}
