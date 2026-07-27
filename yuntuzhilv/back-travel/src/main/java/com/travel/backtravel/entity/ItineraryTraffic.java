package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_itinerary_traffic")
public class ItineraryTraffic {

    @TableId(type = IdType.AUTO)
    private Long detailId;

    private String itineraryId;

    private Integer dayNum;

    private Integer orderNum;

    private String trafficId;

    private BigDecimal itemPrice;

    private String startTime;

    private String endTime;

    private String itemDesc;

    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
