package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("booking_record")
public class BookingRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long itineraryItemId;

    private String bookingType;

    private Long targetId;

    private String targetName;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String status;

    private LocalDateTime bookingDate;
}
