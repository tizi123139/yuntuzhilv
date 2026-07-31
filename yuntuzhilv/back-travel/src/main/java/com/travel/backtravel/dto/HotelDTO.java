package com.travel.backtravel.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HotelDTO {

    @JsonProperty("hotelId")
    @JsonAlias("hotelId")
    private String id;

    @NotBlank(message = "酒店名称不能为空")
    private String name;

    private Integer star;

    private BigDecimal price;

    private Integer remain;

    private String city;

    private String address;

    private String description;

    private String phone;

    private String imgUrl;

    private String facilities;
}
