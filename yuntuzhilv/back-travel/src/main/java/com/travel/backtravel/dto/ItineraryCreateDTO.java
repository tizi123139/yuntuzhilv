package com.travel.backtravel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ItineraryCreateDTO {

    @NotBlank(message = "目的地不能为空")
    private String destinationCity;

    private String departureCity;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    private BigDecimal budget;

    private String interests;

    private List<ItineraryItemDTO> items;
}
