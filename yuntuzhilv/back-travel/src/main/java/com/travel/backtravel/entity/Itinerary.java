package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("itinerary")
public class Itinerary {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String departureCity;

    private String destinationCity;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer days;

    private BigDecimal budget;

    private String interests;

    private BigDecimal totalCost;

    private String status;

    private Integer isArchived;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
