package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_itinerary_hotel")
public class ItineraryHotel {

    @TableId(type = IdType.AUTO)
    private Long detailId;

    private String itineraryId;

    private Integer dayNum;

    private Integer orderNum;

    private String hotelId;

    private BigDecimal itemPrice;

    private String checkInTime;

    private String checkOutTime;

    private String itemDesc;

    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
