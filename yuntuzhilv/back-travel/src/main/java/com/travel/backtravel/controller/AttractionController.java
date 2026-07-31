package com.travel.backtravel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.annotation.OperationLog;
import com.travel.backtravel.dto.AttractionDTO;
import com.travel.backtravel.service.AttractionService;
import com.travel.backtravel.util.ResultUtil;
import com.travel.backtravel.vo.AttractionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attraction")
@RequiredArgsConstructor
@Tag(name = "景点管理", description = "景点的查询、新增、修改、删除接口")
public class AttractionController {

    private final AttractionService attractionService;

    @GetMapping("/list")
    @Operation(summary = "获取景点列表", description = "获取景点列表，支持按城市、类型筛选")
    public ResultUtil<Page<AttractionVO>> list(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultUtil.success(attractionService.list(city, type, pageNum, pageSize));
    }

    @GetMapping("/detail")
    @Operation(summary = "获取景点详情", description = "根据景点ID获取景点详细信息")
    public ResultUtil<AttractionVO> detail(@RequestParam String id) {
        return ResultUtil.success(attractionService.getDetail(id));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(value = "新增景点", module = "景点管理", type = "add")
    @Operation(summary = "新增景点", description = "管理员新增一条景点记录")
    public ResultUtil<AttractionVO> add(@Valid @RequestBody AttractionDTO dto) {
        return ResultUtil.success(attractionService.create(dto));
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(value = "修改景点信息", module = "景点管理", type = "update")
    @Operation(summary = "修改景点信息", description = "管理员更新景点信息")
    public ResultUtil<AttractionVO> update(@Valid @RequestBody AttractionDTO dto) {
        return ResultUtil.success(attractionService.update(dto));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(value = "删除景点", module = "景点管理", type = "delete")
    @Operation(summary = "删除景点", description = "管理员删除指定景点")
    public ResultUtil<Void> delete(@RequestParam String id) {
        attractionService.delete(id);
        return ResultUtil.success();
    }
}
