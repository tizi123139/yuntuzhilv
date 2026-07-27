package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("tb_footprint")
public class Footprint {

    @TableId(type = IdType.AUTO)
    private Long footprintId;

    private Long userId;

    private String attractionId;

    private String cityName;

    private LocalDate visitDate;

    private Integer rating;

    private String comment;

    private Integer isDeleted;

    private LocalDateTime createTime;
}
