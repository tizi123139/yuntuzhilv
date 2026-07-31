package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("tb_itinerary")
public class Itinerary {

    @TableId(type = IdType.INPUT)
    private String itineraryId;

    private Long userId;

    private String title;

    private String startCity;

    private String destination;

    private Integer days;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal totalBudget;

    private BigDecimal totalCost;

    private String interests;

    private String travelTips;

    /** 每日行程 JSON（替代三张明细表） */
    private String dayPlansJson;

    private Integer people;

    private String status;

    private Integer isTemp;

    private Integer isArchived;

    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
