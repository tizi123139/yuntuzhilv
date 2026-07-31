package com.travel.backtravel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TrafficDTO {

    private String id;

    @NotBlank(message = "出发城市不能为空")
    private String fromCity;

    @NotBlank(message = "到达城市不能为空")
    private String toCity;

    private String type;

    private BigDecimal price;

    private String duration;

    private String code;

    private String departTime;

    private String carrier;
}
