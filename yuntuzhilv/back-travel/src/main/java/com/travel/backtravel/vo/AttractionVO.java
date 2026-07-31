package com.travel.backtravel.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttractionVO {

    @JsonProperty("attractionId")
    private String id;

    private String name;

    private String city;

    private String type;

    private BigDecimal price;

    private String openTime;

    private String address;

    private String description;

    private String imgUrl;

    private BigDecimal rating;

    private Long viewCount;

}
