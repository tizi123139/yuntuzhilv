package com.travel.backtravel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.SystemLog;
import com.travel.backtravel.service.SystemLogService;
import com.travel.backtravel.util.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
@Tag(name = "操作日志", description = "系统操作日志查询接口（管理员）")
public class LogController {

    private final SystemLogService systemLogService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取操作日志", description = "管理员分页查询系统操作日志，支持按操作类型和模块筛选")
    public ResultUtil<Page<SystemLog>> getLogs(
            @RequestParam(required = false) String operatorType,
            @RequestParam(required = false) String module,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultUtil.success(systemLogService.getLogs(operatorType, module, pageNum, pageSize));
    }
}
