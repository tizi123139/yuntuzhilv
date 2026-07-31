package com.travel.backtravel.controller;

import com.travel.backtravel.service.StatService;
import com.travel.backtravel.util.ResultUtil;
import com.travel.backtravel.vo.StatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
@Tag(name = "统计分析", description = "系统统计数据查询接口（管理员）")
public class StatController {

    private final StatService statService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取系统统计", description = "获取系统整体统计数据（用户数、行程数等）")
    public ResultUtil<StatVO> getStatistics() {
        return ResultUtil.success(statService.getStatistics());
    }

    @GetMapping("/hotAttraction")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "热门景点统计", description = "获取热门景点排行数据")
    public ResultUtil<List<Map<String, Object>>> hotAttraction() {
        return ResultUtil.success(statService.getHotAttractions());
    }

    @GetMapping("/hotCity")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "热门目的地统计", description = "获取热门目的地城市排行数据")
    public ResultUtil<List<Map<String, Object>>> hotCity() {
        return ResultUtil.success(statService.getHotDestinations());
    }

    @GetMapping("/hotelSelectionRatio")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "酒店选择比例", description = "获取指定城市的酒店选择比例数据")
    public ResultUtil<List<Map<String, Object>>> hotelSelectionRatio(@RequestParam String city) {
        return ResultUtil.success(statService.getHotelSelectionRatio(city));
    }

    @GetMapping("/attractionSelectionRatio")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "景点选择比例", description = "获取指定城市的景点选择比例数据")
    public ResultUtil<List<Map<String, Object>>> attractionSelectionRatio(@RequestParam String city) {
        return ResultUtil.success(statService.getAttractionSelectionRatio(city));
    }

    @GetMapping("/cityTrend")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "城市趋势", description = "获取城市旅游趋势数据")
    public ResultUtil<Map<String, Object>> cityTrend() {
        return ResultUtil.success(statService.getCityTrend());
    }
}
