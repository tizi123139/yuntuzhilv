package com.travel.backtravel.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {

    private String realName;

    private String avatar;

    private String email;

    private String phone;

    private String preferences;
}
