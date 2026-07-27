package com.travel.backtravel.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.backtravel.dto.AiPlanDTO;
import com.travel.backtravel.dto.ItineraryCreateDTO;
import com.travel.backtravel.dto.ItineraryItemDTO;
import com.travel.backtravel.entity.Itinerary;
import com.travel.backtravel.entity.ItineraryAttraction;
import com.travel.backtravel.exception.BusinessException;
import com.travel.backtravel.mapper.ItineraryAttractionMapper;
import com.travel.backtravel.mapper.ItineraryMapper;
import com.travel.backtravel.rag.PromptTemplate;
import com.travel.backtravel.service.AiService;
import com.travel.backtravel.service.ItineraryService;
import com.travel.backtravel.util.RedisUtil;
import com.travel.backtravel.vo.ItineraryVO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatLanguageModel chatLanguageModel;
    private final PromptTemplate promptTemplate;
    private final ItineraryService itineraryService;
    private final ItineraryMapper itineraryMapper;
    private final ItineraryAttractionMapper itineraryAttractionMapper;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    private static final String AI_REQUEST_KEY = "ai_request:";

    @Override
    @Transactional
    public ItineraryVO generateItinerary(Long userId, AiPlanDTO dto) {
        if (userId != null) {
            String requestKey = AI_REQUEST_KEY + userId;
            try {
                redisUtil.setNX(requestKey, "processing", 10, TimeUnit.SECONDS);
            } catch (RuntimeException e) {
                throw new BusinessException("请求过于频繁，请稍后再试");
            }
        }

        try {
            String prompt = promptTemplate.generateItineraryPrompt(dto);
            String response = chatLanguageModel.generate(prompt);

            log.info("AI response: {}", response);

            JsonNode jsonNode = parseJsonResponse(response);

            ItineraryCreateDTO createDTO = new ItineraryCreateDTO();
            createDTO.setDestination(dto.getDestinationCity());
            createDTO.setStartCity(dto.getDepartureCity());
            createDTO.setStartDate(dto.getStartDate());
            createDTO.setEndDate(dto.getEndDate());
            createDTO.setTotalBudget(dto.getBudget());
            createDTO.setInterests(dto.getInterests());

            List<ItineraryItemDTO> attractions = new ArrayList<>();
            JsonNode daysNode = jsonNode.get("days");
            if (daysNode != null && daysNode.isArray()) {
                for (JsonNode dayNode : daysNode) {
                    int dayNum = dayNode.has("dayNumber") ? dayNode.get("dayNumber").asInt() : 1;
                    JsonNode itemsNode = dayNode.get("items");
                    if (itemsNode != null && itemsNode.isArray()) {
                        for (JsonNode itemNode : itemsNode) {
                            ItineraryItemDTO itemDTO = new ItineraryItemDTO();
                            itemDTO.setDayNum(dayNum);
                            itemDTO.setItemName(itemNode.has("itemName") ? itemNode.get("itemName").asText() : "");
                            itemDTO.setItemDesc(itemNode.has("itemDesc") ? itemNode.get("itemDesc").asText() : null);
                            itemDTO.setItemPrice(itemNode.has("itemPrice") ? BigDecimal.valueOf(itemNode.get("itemPrice").asDouble()) : BigDecimal.ZERO);
                            itemDTO.setStartTime(itemNode.has("startTime") ? itemNode.get("startTime").asText() : null);
                            itemDTO.setEndTime(itemNode.has("endTime") ? itemNode.get("endTime").asText() : null);
                            attractions.add(itemDTO);
                        }
                    }
                }
            }
            createDTO.setAttractions(attractions);

            return itineraryService.createItinerary(userId != null ? userId : 0L, createDTO);
        } finally {
            if (userId != null) {
                redisUtil.delete(AI_REQUEST_KEY + userId);
            }
        }
    }

    @Override
    @Transactional
    public ItineraryVO modifyItinerary(Long userId, String itineraryId, String modifications) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }

        if (!itinerary.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此行程");
        }

        List<ItineraryAttraction> attractions = itineraryAttractionMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ItineraryAttraction>()
                        .eq(ItineraryAttraction::getItineraryId, itineraryId)
                        .eq(ItineraryAttraction::getIsDeleted, 0)
                        .orderByAsc(ItineraryAttraction::getDayNum, ItineraryAttraction::getOrderNum)
        );

        StringBuilder currentItinerary = new StringBuilder();
        currentItinerary.append("行程标题：").append(itinerary.getTitle()).append("\n");
        currentItinerary.append("目的地：").append(itinerary.getDestination()).append("\n");
        currentItinerary.append("天数：").append(itinerary.getDays()).append("天\n\n");

        int currentDay = 0;
        for (ItineraryAttraction item : attractions) {
            if (item.getDayNum() != currentDay) {
                currentDay = item.getDayNum();
                currentItinerary.append("第").append(currentDay).append("天：\n");
            }
            currentItinerary.append("- ").append(item.getStartTime()).append(" ").append(item.getItemDesc())
                    .append("（").append(item.getItemPrice()).append("元）\n");
        }

        String prompt = promptTemplate.modifyItineraryPrompt(itineraryId, currentItinerary.toString(), modifications);
        String response = chatLanguageModel.generate(prompt);

        log.info("AI modify response: {}", response);

        JsonNode jsonNode = parseJsonResponse(response);

        ItineraryCreateDTO createDTO = new ItineraryCreateDTO();
        createDTO.setDestination(itinerary.getDestination());
        createDTO.setStartCity(itinerary.getStartCity());
        createDTO.setStartDate(itinerary.getStartDate());
        createDTO.setEndDate(itinerary.getEndDate());
        createDTO.setTotalBudget(itinerary.getTotalBudget());
        createDTO.setInterests(itinerary.getInterests());

        List<ItineraryItemDTO> newAttractions = new ArrayList<>();
        JsonNode daysNode = jsonNode.get("days");
        if (daysNode != null && daysNode.isArray()) {
            for (JsonNode dayNode : daysNode) {
                int dayNum = dayNode.has("dayNumber") ? dayNode.get("dayNumber").asInt() : 1;
                JsonNode itemsNode = dayNode.get("items");
                if (itemsNode != null && itemsNode.isArray()) {
                    for (JsonNode itemNode : itemsNode) {
                        ItineraryItemDTO itemDTO = new ItineraryItemDTO();
                        itemDTO.setDayNum(dayNum);
                        itemDTO.setItemName(itemNode.has("itemName") ? itemNode.get("itemName").asText() : "");
                        itemDTO.setItemDesc(itemNode.has("itemDesc") ? itemNode.get("itemDesc").asText() : null);
                        itemDTO.setItemPrice(itemNode.has("itemPrice") ? BigDecimal.valueOf(itemNode.get("itemPrice").asDouble()) : BigDecimal.ZERO);
                        itemDTO.setStartTime(itemNode.has("startTime") ? itemNode.get("startTime").asText() : null);
                        itemDTO.setEndTime(itemNode.has("endTime") ? itemNode.get("endTime").asText() : null);
                        newAttractions.add(itemDTO);
                    }
                }
            }
        }
        createDTO.setAttractions(newAttractions);

        return itineraryService.updateItinerary(userId, itineraryId, createDTO);
    }

    private JsonNode parseJsonResponse(String response) {
        try {
            String jsonStr = response.trim();
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            }
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            return objectMapper.readTree(jsonStr.trim());
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", response, e);
            throw new BusinessException("行程生成失败，请重试");
        }
    }
}
