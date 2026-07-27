package com.travel.backtravel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItineraryVO {

    private String itineraryId;

    private String title;

    private String startCity;

    private String destination;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer days;

    private BigDecimal totalBudget;

    private BigDecimal totalCost;

    private String interests;

    private String travelTips;

    private Integer people;

    private String status;

    private Integer isTemp;

    private Integer isArchived;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private List<ItineraryItemVO> attractions;

    private List<ItineraryItemVO> hotels;

    private List<ItineraryItemVO> traffics;
}
