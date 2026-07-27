package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_attraction")
public class Attraction {

    @TableId(value = "attraction_id", type = IdType.INPUT)
    private String id;

    private String name;

    private String city;

    private String type;

    private BigDecimal price;

    private String openTime;

    private String address;

    private String description;

    @TableField("img_url")
    private String imgUrl;

    /** 对应数据库 score 字段 */
    @TableField("score")
    private BigDecimal rating;

    /** 对应数据库 visit_count 字段 */
    @TableField("visit_count")
    private Long viewCount;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
