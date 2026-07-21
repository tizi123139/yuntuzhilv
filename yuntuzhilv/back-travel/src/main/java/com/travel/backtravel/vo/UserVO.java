package com.travel.backtravel.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;

    private String username;

    private String email;

    private String phone;

    private String realName;

    private String avatar;

    private String role;

    private String preferences;

    private Integer status;

    private LocalDateTime createdAt;
}
