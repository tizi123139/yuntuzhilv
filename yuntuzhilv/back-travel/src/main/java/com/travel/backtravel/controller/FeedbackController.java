package com.travel.backtravel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.Feedback;
import com.travel.backtravel.service.FeedbackService;
import com.travel.backtravel.util.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Tag(name = "反馈管理", description = "用户反馈的提交、查询、处理接口")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/submit")
    @Operation(summary = "提交反馈", description = "用户提交反馈信息")
    public ResultUtil<Void> submit(@RequestBody Feedback feedback) {
        feedbackService.submit(feedback);
        return ResultUtil.success();
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取反馈列表", description = "管理员分页查询反馈列表")
    public ResultUtil<Page<Feedback>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultUtil.success(feedbackService.list(status, pageNum, pageSize));
    }

    @PostMapping("/updateStatus")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "处理反馈", description = "管理员更新反馈状态为已处理")
    public ResultUtil<Void> updateStatus(@RequestBody Map<String, Object> params) {
        Long feedbackId = Long.valueOf(params.get("feedbackId").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        feedbackService.updateStatus(feedbackId, status);
        return ResultUtil.success();
    }
}
