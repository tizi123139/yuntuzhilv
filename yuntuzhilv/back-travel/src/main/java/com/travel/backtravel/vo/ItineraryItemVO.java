package com.travel.backtravel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ItineraryItemVO {

    private Long detailId;

    private Integer dayNum;

    private Integer orderNum;

    /** 资源 ID（景点/酒店/交通 ID） */
    private String resourceId;

    /** 资源名称 */
    private String itemName;

    private String itemDesc;

    private BigDecimal itemPrice;

    private String startTime;

    private String endTime;

    /** 酒店专用：入住时段 */
    private String checkInTime;

    /** 酒店专用：退房时段 */
    private String checkOutTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
