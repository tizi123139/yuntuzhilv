package com.travel.backtravel.controller;

import com.travel.backtravel.service.StatService;
import com.travel.backtravel.util.ResultUtil;
import com.travel.backtravel.vo.StatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<StatVO> getStatistics() {
        return ResultUtil.success(statService.getStatistics());
    }
}
