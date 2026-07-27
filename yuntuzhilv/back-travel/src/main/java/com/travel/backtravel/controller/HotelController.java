package com.travel.backtravel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.Hotel;
import com.travel.backtravel.service.HotelService;
import com.travel.backtravel.util.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping("/list")
    public ResultUtil<Page<Hotel>> list(
            @RequestParam(required = false) Integer star,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultUtil.success(hotelService.list(star, maxPrice, city, pageNum, pageSize));
    }

    @GetMapping("/detail")
    public ResultUtil<Hotel> detail(@RequestParam String id) {
        return ResultUtil.success(hotelService.getDetail(id));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Hotel> add(@RequestBody Hotel hotel) {
        return ResultUtil.success(hotelService.create(hotel));
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Hotel> update(@RequestBody Hotel hotel) {
        return ResultUtil.success(hotelService.update(hotel));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Void> delete(@RequestParam String id) {
        hotelService.delete(id);
        return ResultUtil.success();
    }
}
