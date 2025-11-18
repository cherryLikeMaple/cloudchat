package com.cherry.vo;

import lombok.Data;

import java.util.List;

/**
 * @author cherry
 */
@Data
public class CommentListResult {

    private Integer total;            // 总评论数（commentList.size()）
    private List<CommentVO> list;     // 树形评论列表
}
