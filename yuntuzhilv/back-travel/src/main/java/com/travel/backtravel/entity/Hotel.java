package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_hotel")
public class Hotel {

    @TableId(value = "hotel_id", type = IdType.INPUT)
    private String id;

    private String name;

    /** 对应数据库 star VARCHAR（自动转换 Integer ↔ '二星'/'五星'） */
    @TableField(value = "star", typeHandler = com.travel.backtravel.handler.StarTypeHandler.class)
    private Integer star;

    private BigDecimal price;

    /** 对应数据库 stock 字段 */
    @TableField("stock")
    private Integer remain;

    /** 对应数据库 destination 字段 */
    @TableField("destination")
    private String city;

    private String address;

    private String description;

    private String phone;

    @TableField("img_url")
    private String imgUrl;

    private String facilities;

    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
