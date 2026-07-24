package com.travel.backtravel.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {

    private List<T> list;

    private Long total;

    public PageVO(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }
}
