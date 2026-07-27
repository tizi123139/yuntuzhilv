package com.travel.backtravel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.Attraction;
import com.travel.backtravel.service.AttractionService;
import com.travel.backtravel.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attraction")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;

    @GetMapping("/list")
    public ResultUtil<Page<Attraction>> list(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultUtil.success(attractionService.list(city, type, pageNum, pageSize));
    }

    @GetMapping("/detail")
    public ResultUtil<Attraction> detail(@RequestParam String id) {
        return ResultUtil.success(attractionService.getDetail(id));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Attraction> add(@RequestBody Attraction attraction) {
        return ResultUtil.success(attractionService.create(attraction));
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Attraction> update(@RequestBody Attraction attraction) {
        return ResultUtil.success(attractionService.update(attraction));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Void> delete(@RequestParam String id) {
        attractionService.delete(id);
        return ResultUtil.success();
    }
}
