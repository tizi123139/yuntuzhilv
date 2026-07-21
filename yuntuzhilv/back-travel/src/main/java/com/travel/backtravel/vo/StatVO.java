package com.travel.backtravel.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class StatVO {

    private List<Map<String, Object>> hotDestinations;

    private List<Map<String, Object>> hotAttractions;

    private List<Map<String, Object>> userPreferences;

    private Long totalUsers;

    private Long totalItineraries;

    private Long totalBookings;

    private BigDecimal totalRevenue;
}
