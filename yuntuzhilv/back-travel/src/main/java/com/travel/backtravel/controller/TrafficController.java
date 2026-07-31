package com.travel.backtravel.controller;

import com.travel.backtravel.annotation.OperationLog;
import com.travel.backtravel.dto.TrafficDTO;
import com.travel.backtravel.service.TrafficService;
import com.travel.backtravel.util.ResultUtil;
import com.travel.backtravel.vo.TrafficVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "交通管理", description = "交通线路的查询、新增、删除接口")
public class TrafficController {

    private final TrafficService trafficService;

    @GetMapping("/list")
    @Operation(summary = "获取交通线路列表", description = "获取交通线路列表，支持按起点和终点城市筛选")
    public ResultUtil<Map<String, Object>> list(
            @RequestParam(required = false) String fromCity,
            @RequestParam(required = false) String toCity,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Map<String, Object> result = new HashMap<>();

        if (StringUtils.hasText(fromCity) && StringUtils.hasText(toCity)) {
            List<TrafficVO> list = trafficService.findByRoute(fromCity, toCity);
            result.put("list", list);
            result.put("total", list.size());
        } else {
            List<TrafficVO> list = trafficService.list(pageNum, pageSize);
            long total = trafficService.count();
            result.put("list", list);
            result.put("total", total);
        }

        return ResultUtil.success(result);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(value = "新增交通线路", module = "交通管理", type = "add")
    @Operation(summary = "新增交通线路", description = "管理员新增一条交通线路")
    public ResultUtil<TrafficVO> add(@Valid @RequestBody TrafficDTO dto) {
        return ResultUtil.success(trafficService.create(dto));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(value = "删除交通线路", module = "交通管理", type = "delete")
    @Operation(summary = "删除交通线路", description = "管理员删除指定的交通线路")
    public ResultUtil<Void> delete(@RequestParam String id) {
        trafficService.delete(id);
        return ResultUtil.success();
    }
}
