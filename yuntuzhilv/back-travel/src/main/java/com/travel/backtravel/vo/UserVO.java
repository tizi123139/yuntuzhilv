package com.travel.backtravel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long userId;

    private String username;

    private String email;

    private String phone;

    private String realName;

    private String avatar;

    private String role;

    private String preferences;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
