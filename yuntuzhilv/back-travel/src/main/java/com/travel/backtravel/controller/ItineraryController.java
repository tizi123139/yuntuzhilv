package com.travel.backtravel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.BookingCreateDTO;
import com.travel.backtravel.dto.ItineraryCreateDTO;
import com.travel.backtravel.dto.ItineraryItemDTO;
import com.travel.backtravel.service.ItineraryService;
import com.travel.backtravel.util.ResultUtil;
import com.travel.backtravel.vo.ItineraryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/itineraries")
@RequiredArgsConstructor
public class ItineraryController {

    private final ItineraryService itineraryService;

    @PostMapping
    public ResultUtil<ItineraryVO> createItinerary(@Valid @RequestBody ItineraryCreateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(itineraryService.createItinerary(userId, dto));
    }

    @GetMapping("/{id}")
    public ResultUtil<ItineraryVO> getItinerary(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(itineraryService.getItineraryById(userId, id));
    }

    @GetMapping
    public ResultUtil<Page<ItineraryVO>> getUserItineraries(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(itineraryService.getUserItineraries(userId, pageNum, pageSize));
    }

    @PutMapping("/{id}")
    public ResultUtil<ItineraryVO> updateItinerary(
            @PathVariable Long id,
            @Valid @RequestBody ItineraryCreateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(itineraryService.updateItinerary(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResultUtil<Void> deleteItinerary(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        itineraryService.deleteItinerary(userId, id);
        return ResultUtil.success();
    }

    @PostMapping("/{id}/items")
    public ResultUtil<ItineraryVO> addItem(
            @PathVariable Long id,
            @Valid @RequestBody ItineraryItemDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(itineraryService.addItem(userId, id, dto));
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResultUtil<ItineraryVO> updateItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody ItineraryItemDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(itineraryService.updateItem(userId, id, itemId, dto));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResultUtil<ItineraryVO> removeItem(
            @PathVariable Long id,
            @PathVariable Long itemId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(itineraryService.removeItem(userId, id, itemId));
    }

    @PutMapping("/{id}/archive")
    public ResultUtil<Void> archiveItinerary(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        itineraryService.archiveItinerary(userId, id);
        return ResultUtil.success();
    }

    @GetMapping("/{id}/export")
    public ResultUtil<Map<String, String>> exportToPdf(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        String pdfContent = itineraryService.exportToPdf(userId, id);
        return ResultUtil.success(Map.of("content", pdfContent));
    }

    @PostMapping("/booking")
    public ResultUtil<Void> bookItem(@Valid @RequestBody BookingCreateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        itineraryService.bookItem(userId, dto);
        return ResultUtil.success();
    }
}
