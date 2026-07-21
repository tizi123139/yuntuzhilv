package com.travel.backtravel.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItineraryVO {

    private Long id;

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

    private List<ItineraryItemVO> items;
}
