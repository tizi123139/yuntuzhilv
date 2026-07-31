package com.travel.backtravel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItineraryItemDTO {

    @NotNull(message = "天数不能为空")
    private Integer dayNum;

    private Integer orderNum;

    private String itemName;

    private String itemDesc;

    private BigDecimal itemPrice;

    private String startTime;

    private String endTime;

    /** 酒店专用：入住时段 */
    private String checkInTime;

    /** 酒店专用：退房时段 */
    private String checkOutTime;
}
