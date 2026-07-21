package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("itinerary_item")
public class ItineraryItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long itineraryId;

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
