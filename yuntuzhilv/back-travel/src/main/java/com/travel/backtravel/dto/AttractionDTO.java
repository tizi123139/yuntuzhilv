package com.travel.backtravel.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttractionDTO {

    @JsonProperty("attractionId")
    @JsonAlias("attractionId")
    private String id;

    @NotBlank(message = "景点名称不能为空")
    private String name;

    private String city;

    private String type;

    private BigDecimal price;

    private String openTime;

    private String address;

    private String description;

    private String imgUrl;

    private BigDecimal longitude;

    private BigDecimal latitude;
}
