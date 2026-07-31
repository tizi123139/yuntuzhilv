package com.travel.backtravel.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TrafficVO {

    private String id;

    private String fromCity;

    private String toCity;

    private String type;

    private BigDecimal price;

    private String duration;

    private String code;

    private String departTime;

    private String carrier;
}
