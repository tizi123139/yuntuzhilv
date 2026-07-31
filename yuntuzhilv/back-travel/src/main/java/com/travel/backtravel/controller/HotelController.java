package com.travel.backtravel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.annotation.OperationLog;
import com.travel.backtravel.dto.HotelDTO;
import com.travel.backtravel.service.HotelService;
import com.travel.backtravel.util.ResultUtil;
import com.travel.backtravel.vo.HotelVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
@Tag(name = "酒店管理", description = "酒店的查询、新增、修改、删除接口")
public class HotelController {

    private final HotelService hotelService;

    @GetMapping("/list")
    @Operation(summary = "获取酒店列表", description = "获取酒店列表，支持按星级、价格、城市筛选")
    public ResultUtil<Page<HotelVO>> list(
            @RequestParam(required = false) Integer star,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultUtil.success(hotelService.list(star, maxPrice, city, pageNum, pageSize));
    }

    @GetMapping("/detail")
    @Operation(summary = "获取酒店详情", description = "根据酒店ID获取酒店详细信息")
    public ResultUtil<HotelVO> detail(@RequestParam String id) {
        return ResultUtil.success(hotelService.getDetail(id));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(value = "新增酒店", module = "酒店管理", type = "add")
    @Operation(summary = "新增酒店", description = "管理员新增一条酒店记录")
    public ResultUtil<HotelVO> add(@Valid @RequestBody HotelDTO dto) {
        return ResultUtil.success(hotelService.create(dto));
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(value = "修改酒店信息", module = "酒店管理", type = "update")
    @Operation(summary = "修改酒店信息", description = "管理员更新酒店信息")
    public ResultUtil<HotelVO> update(@Valid @RequestBody HotelDTO dto) {
        return ResultUtil.success(hotelService.update(dto));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(value = "删除酒店", module = "酒店管理", type = "delete")
    @Operation(summary = "删除酒店", description = "管理员删除指定酒店")
    public ResultUtil<Void> delete(@RequestParam String id) {
        hotelService.delete(id);
        return ResultUtil.success();
    }
}
