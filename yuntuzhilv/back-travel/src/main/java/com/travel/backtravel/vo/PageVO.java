package com.travel.backtravel.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {

    private List<T> records;

    private Long total;

    private Integer pageNum;

    private Integer pageSize;

    public PageVO(List<T> records, Long total, Integer pageNum, Integer pageSize) {
        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
