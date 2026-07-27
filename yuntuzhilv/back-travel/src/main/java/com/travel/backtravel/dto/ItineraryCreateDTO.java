package com.travel.backtravel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ItineraryCreateDTO {

    private String title;

    @NotBlank(message = "出发城市不能为空")
    private String startCity;

    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @NotNull(message = "天数不能为空")
    private Integer days;

    private BigDecimal totalBudget;

    private String interests;

    private Integer people;

    private Integer isTemp;

    private List<ItineraryItemDTO> attractions;

    private List<ItineraryItemDTO> hotels;

    private List<ItineraryItemDTO> traffics;
}
