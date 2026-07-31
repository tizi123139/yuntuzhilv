package com.travel.backtravel.controller;

import com.travel.backtravel.dto.AiPlanDTO;
import com.travel.backtravel.service.AiService;
import com.travel.backtravel.util.ResultUtil;
import com.travel.backtravel.vo.ItineraryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "AI智能规划", description = "AI智能生成和修改行程接口")
public class AiController {

    private final AiService aiService;

    @PostMapping("/itinerary/generate")
    @Operation(summary = "AI生成行程", description = "根据用户需求AI智能生成旅游行程")
    public ResultUtil<ItineraryVO> generateItinerary(@Valid @RequestBody AiPlanDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long)) {
            return ResultUtil.error(401, "请先登录后再使用AI生成行程");
        }
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(aiService.generateItinerary(userId, dto));
    }

    @PostMapping("/modify/{itineraryId}")
    @Operation(summary = "AI修改行程", description = "根据用户反馈AI智能修改已生成的行程")
    public ResultUtil<ItineraryVO> modifyItinerary(
            @PathVariable String itineraryId,
            @RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long)) {
            return ResultUtil.error(401, "未登录");
        }
        Long userId = (Long) auth.getPrincipal();
        String modifications = request.get("modifications");
        return ResultUtil.success(aiService.modifyItinerary(userId, itineraryId, modifications));
    }
}
