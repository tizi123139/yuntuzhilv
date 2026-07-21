package com.travel.backtravel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItineraryItemDTO {

    private Long id;

    @NotNull(message = "天数不能为空")
    private Integer dayNumber;

    @NotBlank(message = "项目类型不能为空")
    private String itemType;

    private Long itemId;

    @NotBlank(message = "项目名称不能为空")
    private String itemName;

    private String itemDesc;

    private BigDecimal itemPrice;

    private String startTime;

    private String endTime;

    private Integer orderNum;
}
