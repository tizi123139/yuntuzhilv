package com.travel.backtravel.controller;

import com.travel.backtravel.dto.AiPlanDTO;
import com.travel.backtravel.service.AiService;
import com.travel.backtravel.util.ResultUtil;
import com.travel.backtravel.vo.ItineraryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/plan")
    public ResultUtil<ItineraryVO> generateItinerary(@Valid @RequestBody AiPlanDTO dto) {
        Long userId = null;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Long) {
                userId = (Long) auth.getPrincipal();
            }
        } catch (Exception ignored) {
        }
        return ResultUtil.success(aiService.generateItinerary(userId, dto));
    }

    @PostMapping("/modify/{itineraryId}")
    public ResultUtil<ItineraryVO> modifyItinerary(
            @PathVariable Long itineraryId,
            @RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        String modifications = request.get("modifications");
        return ResultUtil.success(aiService.modifyItinerary(userId, itineraryId, modifications));
    }
}
