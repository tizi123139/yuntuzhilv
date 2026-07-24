package com.travel.backtravel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理员修改用户状态请求 DTO
 */
@Data
public class UpdateStatusDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 目标状态：1=启用, 0=禁用 */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
