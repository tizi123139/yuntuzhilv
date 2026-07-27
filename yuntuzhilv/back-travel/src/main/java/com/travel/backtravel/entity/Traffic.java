package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_traffic")
public class Traffic {

    @TableId(value = "traffic_id", type = IdType.INPUT)
    private String id;

    /** 对应数据库 start_city 字段 */
    @TableField("start_city")
    private String fromCity;

    /** 对应数据库 end_city 字段 */
    @TableField("end_city")
    private String toCity;

    /** 对应数据库 way 字段 */
    @TableField("way")
    private String type;

    /** 对应数据库 cost 字段 */
    @TableField("cost")
    private BigDecimal price;

    private String duration;

    private String code;

    private String departTime;

    private String carrier;

    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
