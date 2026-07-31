package com.travel.backtravel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.annotation.OperationLog;
import com.travel.backtravel.dto.BookingCreateDTO;
import com.travel.backtravel.dto.ItineraryCreateDTO;
import com.travel.backtravel.service.ItineraryService;
import com.travel.backtravel.service.PdfService;
import com.travel.backtravel.util.ResultUtil;
import com.travel.backtravel.vo.ItineraryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping({"/itineraries", "/itinerary"})
@RequiredArgsConstructor
@Tag(name = "行程管理", description = "行程的创建、查询、修改、删除、导出等接口")
public class ItineraryController {

    private final ItineraryService itineraryService;
    private final PdfService pdfService;

    @PostMapping
    @Operation(summary = "创建行程", description = "根据用户输入创建一个新的行程单")
    public ResultUtil<ItineraryVO> createItinerary(@Valid @RequestBody ItineraryCreateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(itineraryService.createItinerary(userId, dto));
    }

    /**
     * 保存 AI 生成的行程
     */
    @PostMapping({"/save-ai", "/save"})
    @OperationLog(value = "保存AI生成行程", module = "行程管理", type = "add")
    @Operation(summary = "保存AI生成行程", description = "保存由AI智能规划生成的行程到用户账户")
    public ResultUtil<ItineraryVO> saveAiItinerary(@RequestBody ItineraryVO itineraryVO) {
        log.info("saveAiItinerary - startCity: {}, destination: {}, title: {}", 
                itineraryVO.getStartCity(), itineraryVO.getDestination(), itineraryVO.getTitle());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        
        return ResultUtil.success(itineraryService.saveAiItinerary(userId, itineraryVO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取行程详情", description = "根据行程ID获取行程的详细信息")
    public ResultUtil<ItineraryVO> getItinerary(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(itineraryService.getItineraryById(userId, id));
    }

    @GetMapping
    @Operation(summary = "获取用户行程列表", description = "获取当前登录用户的所有行程列表，支持分页")
    public ResultUtil<Page<ItineraryVO>> getUserItineraries(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(itineraryService.getUserItineraries(userId, pageNum, pageSize));
    }

    @PutMapping("/{id}")
    @OperationLog(value = "修改行程", module = "行程管理", type = "update")
    @Operation(summary = "修改行程", description = "根据行程ID更新行程信息")
    public ResultUtil<ItineraryVO> updateItinerary(@PathVariable String id, @Valid @RequestBody ItineraryCreateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(itineraryService.updateItinerary(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    @OperationLog(value = "删除行程", module = "行程管理", type = "delete")
    @Operation(summary = "删除行程", description = "根据行程ID删除行程")
    public ResultUtil<Void> deleteItinerary(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        itineraryService.deleteItinerary(userId, id);
        return ResultUtil.success();
    }

    @PutMapping("/{id}/archive")
    @OperationLog(value = "标记行程为已完成", module = "行程管理", type = "update")
    @Operation(summary = "标记行程为已完成", description = "将行程状态更改为已完成")
    public ResultUtil<Void> archiveItinerary(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        itineraryService.archiveItinerary(userId, id);
        return ResultUtil.success();
    }

    /**
     * 导出行程为 PDF 文件
     */
    @GetMapping("/export/{id}")
    @Operation(summary = "导出行程PDF", description = "根据行程ID导出PDF格式的行程文件")
    public void exportToPdf(@PathVariable String id, HttpServletResponse response) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        
        ItineraryVO itinerary = itineraryService.getItineraryById(userId, id);
        pdfService.generateItineraryPdf(itinerary, response);
    }

    @PostMapping("/booking")
    @Operation(summary = "创建预订", description = "为行程中的酒店创建预订记录")
    public ResultUtil<Void> createBooking(@Valid @RequestBody BookingCreateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        itineraryService.createBooking(userId, dto);
        return ResultUtil.success();
    }
}
