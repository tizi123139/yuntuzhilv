package com.travel.backtravel.controller;

import com.travel.backtravel.entity.Traffic;
import com.travel.backtravel.service.TrafficService;
import com.travel.backtravel.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/traffic")
@RequiredArgsConstructor
public class TrafficController {

    private final TrafficService trafficService;

    @GetMapping("/list")
    public ResultUtil<Map<String, Object>> list(
            @RequestParam(required = false) String fromCity,
            @RequestParam(required = false) String toCity,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Map<String, Object> result = new HashMap<>();

        if (StringUtils.hasText(fromCity) && StringUtils.hasText(toCity)) {
            // 游客查询：按起止城市查
            List<Traffic> list = trafficService.findByRoute(fromCity, toCity);
            result.put("list", list);
            result.put("total", list.size());
        } else {
            // 管理员查询：分页
            List<Traffic> list = trafficService.list(pageNum, pageSize);
            long total = trafficService.count();
            result.put("list", list);
            result.put("total", total);
        }

        return ResultUtil.success(result);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Traffic> add(@RequestBody Traffic traffic) {
        return ResultUtil.success(trafficService.create(traffic));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Void> delete(@RequestParam String id) {
        trafficService.delete(id);
        return ResultUtil.success();
    }
}
