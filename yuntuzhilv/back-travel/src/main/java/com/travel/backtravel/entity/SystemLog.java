package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_log")
public class SystemLog {

    @TableId(type = IdType.AUTO)
    private Long logId;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operator_type")
    private String operatorType;

    private String module;

    @TableField("ip")
    private String ipAddress;

    private String content;

    @TableField("request_params")
    private String requestParams;

    @TableField("response_result")
    private String responseResult;

    private String status;

    @TableField("error_message")
    private String errorMessage;

    @TableField("execution_time")
    private Integer executionTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    /** 瞬态字段：操作人用户名（通过关联查询填充，非数据库列） */
    @TableField(exist = false)
    private String operator;
}
