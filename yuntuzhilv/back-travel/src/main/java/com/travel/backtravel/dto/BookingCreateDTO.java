package com.travel.backtravel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingCreateDTO {

    @NotNull(message = "行程项目ID不能为空")
    private Long itineraryItemId;

    @NotBlank(message = "预订类型不能为空")
    private String bookingType;

    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    private String targetName;

    private Integer quantity;
}
