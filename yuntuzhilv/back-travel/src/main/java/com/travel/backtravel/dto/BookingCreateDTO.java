package com.travel.backtravel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookingCreateDTO {

    @NotBlank(message = "行程ID不能为空")
    private String itineraryId;

    @NotBlank(message = "资源类型不能为空")
    private String resourceType;

    @NotBlank(message = "资源ID不能为空")
    private String resourceId;

    private String resourceName;

    private Integer quantity;

    private BigDecimal totalPrice;

    /** 酒店专用：入住日期 */
    private String checkIn;

    /** 酒店专用：离店日期 */
    private String checkOut;
}
