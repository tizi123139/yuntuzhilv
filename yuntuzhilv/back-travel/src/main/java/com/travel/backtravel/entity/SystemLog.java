package com.travel.backtravel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_log")
public class SystemLog {

    @TableId(type = IdType.AUTO)
    private Long logId;

    private Long operatorId;

    private String operatorType;

    private String module;

    private String ipAddress;

    private String content;

    private String requestParams;

    private String responseResult;

    private String status;

    private String errorMessage;

    private Integer executionTime;

    private LocalDateTime createTime;
}
