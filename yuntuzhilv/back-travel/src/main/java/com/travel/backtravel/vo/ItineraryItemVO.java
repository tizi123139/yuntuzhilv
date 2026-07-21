package com.travel.backtravel.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItineraryItemVO {

    private Long id;

    private Integer dayNumber;

    private String itemType;

    private Long itemId;

    private String itemName;

    private String itemDesc;

    private BigDecimal itemPrice;

    private String startTime;

    private String endTime;

    private Integer orderNum;
}
