package com.travel.backtravel.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HotelVO {

    @JsonProperty("hotelId")
    private String id;

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
