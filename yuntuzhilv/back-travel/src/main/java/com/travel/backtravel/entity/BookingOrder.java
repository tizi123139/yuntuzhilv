package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_booking_order")
public class BookingOrder {

    @TableId(type = IdType.INPUT)
    private String orderId;

    private Long userId;

    private String itineraryId;

    private String resourceType;

    private String resourceId;

    private String resourceName;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String checkIn;

    private String checkOut;

    private String orderStatus;

    private Integer isDeleted;

    private LocalDateTime createTime;
}
