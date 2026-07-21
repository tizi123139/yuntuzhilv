package com.travel.backtravel.dto;

import lombok.Data;

@Data
public class UserQueryDTO {

    private String username;

    private String email;

    private String phone;

    private String role;

    private Integer status;

    private Integer pageNum;

    private Integer pageSize;
}
