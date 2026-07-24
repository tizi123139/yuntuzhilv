package com.travel.backtravel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送短信/邮箱验证码请求 DTO
 */
@Data
public class SendCodeDTO {

    /** 手机号（含国际区号，如 +8613800138000）或邮箱 */
    @NotBlank(message = "手机号不能为空")
    private String phone;
}
