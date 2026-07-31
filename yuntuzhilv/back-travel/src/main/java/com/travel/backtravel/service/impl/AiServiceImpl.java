package com.travel.backtravel.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.backtravel.dto.AiPlanDTO;
import com.travel.backtravel.entity.Itinerary;
import com.travel.backtravel.exception.BusinessException;
import com.travel.backtravel.mapper.ItineraryMapper;
import com.travel.backtravel.rag.PromptTemplate;
import com.travel.backtravel.service.AiService;
import com.travel.backtravel.util.RedisUtil;
import com.travel.backtravel.vo.ItineraryItemVO;
import com.travel.backtravel.vo.ItineraryVO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatLanguageModel chatLanguageModel;
    private final PromptTemplate promptTemplate;
    private final ItineraryMapper itineraryMapper;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    private static final String AI_REQUEST_KEY = "ai_request:";

    // ==================== 生成行程 ====================

    @Override
    @Transactional
    public ItineraryVO generateItinerary(Long userId, AiPlanDTO dto) {
        // 防重复提交
        if (userId != null) {
            String requestKey = AI_REQUEST_KEY + userId;
            if (!redisUtil.setNX(requestKey, "processing", 10, TimeUnit.SECONDS)) {
                throw new BusinessException("请求过于频繁，请稍后再试");
            }
        }

        try {
            // 计算天数
            dto.setDays((int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1);

            // 构建精简 Prompt（无资源列表）
            String prompt = promptTemplate.generateItineraryPrompt(dto);

            // 调用大模型
            long start = System.currentTimeMillis();
            String response = chatLanguageModel.generate(prompt);
            log.info("AI生成耗时: {}ms", System.currentTimeMillis() - start);

            // 解析 JSON
            JsonNode jsonNode = parseJsonResponse(response);

            // 兜底：重新累加 totalCost
            BigDecimal recalculatedCost = recalculateTotalCost(jsonNode);

            // 保存行程（仅插入主表）
            return saveItinerary(userId, dto, jsonNode, recalculatedCost);

        } finally {
            if (userId != null) {
                redisUtil.delete(AI_REQUEST_KEY + userId);
            }
        }
    }

    // ==================== 修改行程 ====================

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

        // 从 dayPlansJson 构建当前行程文本
        String currentItineraryText = buildCurrentItineraryText(itinerary);

        // 调用大模型修改
        String prompt = promptTemplate.modifyItineraryPrompt(currentItineraryText, modifications);
        String response = chatLanguageModel.generate(prompt);
        log.info("AI modify response: {}", response);

        JsonNode jsonNode = parseJsonResponse(response);

        // 兜底：重新累加 totalCost
        BigDecimal recalculatedCost = recalculateTotalCost(jsonNode);

        // 更新主表
        String dayPlansJson = extractDaysJson(jsonNode);
        itinerary.setTitle(jsonNode.has("title") ? jsonNode.get("title").asText() : itinerary.getTitle());
        itinerary.setDayPlansJson(dayPlansJson);
        itinerary.setTotalCost(recalculatedCost);
        itinerary.setUpdateTime(LocalDateTime.now());
        itineraryMapper.updateById(itinerary);

        // 返回 VO
        return buildItineraryVO(itinerary, jsonNode);
    }

    // ==================== 保存行程（仅主表） ====================

    private ItineraryVO saveItinerary(Long userId, AiPlanDTO dto, JsonNode jsonNode, BigDecimal totalCost) {
        String itineraryId = "TRP" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        // 提取 days JSON 字符串
        String dayPlansJson = extractDaysJson(jsonNode);

        Itinerary itinerary = new Itinerary();
        itinerary.setItineraryId(itineraryId);
        itinerary.setUserId(userId != null ? userId : 0L);
        itinerary.setTitle(jsonNode.has("title") ? jsonNode.get("title").asText() : dto.getDestinationCity() + "旅行");
        itinerary.setStartCity(dto.getDepartureCity());
        itinerary.setDestination(dto.getDestinationCity());
        itinerary.setDays(dto.getDays());
        itinerary.setStartDate(dto.getStartDate());
        itinerary.setEndDate(dto.getEndDate());
        itinerary.setTotalBudget(dto.getBudget());
        itinerary.setTotalCost(totalCost);
        itinerary.setDayPlansJson(dayPlansJson);
        itinerary.setInterests(dto.getInterests() != null ? String.join(",", dto.getInterests()) : null);
        itinerary.setPeople(dto.getPeople());
        itinerary.setStatus("ACTIVE");
        itinerary.setIsTemp(0);
        itinerary.setIsArchived(0);
        itinerary.setIsDeleted(0);
        itinerary.setCreateTime(LocalDateTime.now());
        itinerary.setUpdateTime(LocalDateTime.now());

        itineraryMapper.insert(itinerary);

        return buildItineraryVO(itinerary, jsonNode);
    }

    // ==================== 兜底：重新计算总价 ====================

    private BigDecimal recalculateTotalCost(JsonNode jsonNode) {
        BigDecimal total = BigDecimal.ZERO;
        JsonNode daysNode = jsonNode.get("days");
        if (daysNode != null && daysNode.isArray()) {
            for (JsonNode dayNode : daysNode) {
                JsonNode itemsNode = dayNode.get("items");
                if (itemsNode != null && itemsNode.isArray()) {
                    for (JsonNode itemNode : itemsNode) {
                        if (itemNode.has("itemPrice")) {
                            try {
                                double price = itemNode.get("itemPrice").asDouble();
                                total = total.add(BigDecimal.valueOf(price));
                            } catch (Exception e) {
                                log.warn("解析 itemPrice 失败: {}", itemNode.get("itemPrice"));
                            }
                        }
                    }
                }
            }
        }
        log.info("兜底重新计算 totalCost = {}", total);
        return total;
    }

    // ==================== 提取 days JSON 字符串 ====================

    private String extractDaysJson(JsonNode jsonNode) {
        JsonNode daysNode = jsonNode.get("days");
        if (daysNode != null) {
            return daysNode.toString();
        }
        return "[]";
    }

    // ==================== 构建 VO ====================

    private ItineraryVO buildItineraryVO(Itinerary itinerary, JsonNode jsonNode) {
        ItineraryVO vo = new ItineraryVO();
        vo.setItineraryId(itinerary.getItineraryId());
        vo.setTitle(itinerary.getTitle());
        vo.setStartCity(itinerary.getStartCity());
        vo.setDestination(itinerary.getDestination());
        vo.setDays(itinerary.getDays());
        vo.setStartDate(itinerary.getStartDate());
        vo.setEndDate(itinerary.getEndDate());
        vo.setTotalBudget(itinerary.getTotalBudget());
        vo.setTotalCost(itinerary.getTotalCost());
        vo.setInterests(itinerary.getInterests() != null
                ? new ArrayList<>(Arrays.asList(itinerary.getInterests().split(",")))
                : new ArrayList<>());
        vo.setPeople(itinerary.getPeople());
        vo.setStatus(itinerary.getStatus());
        vo.setCreateTime(itinerary.getCreateTime());

        // 从 jsonNode 的 days 数组构建 dayPlans
        List<ItineraryVO.DayPlan> dayPlans = new ArrayList<>();
        JsonNode daysNode = jsonNode.get("days");
        if (daysNode != null && daysNode.isArray()) {
            for (JsonNode dayNode : daysNode) {
                ItineraryVO.DayPlan dayPlan = new ItineraryVO.DayPlan();
                dayPlan.setDayNumber(dayNode.has("dayNumber") ? dayNode.get("dayNumber").asInt() : 1);

                List<ItineraryItemVO> items = new ArrayList<>();
                JsonNode itemsNode = dayNode.get("items");
                if (itemsNode != null && itemsNode.isArray()) {
                    int orderNum = 1;
                    for (JsonNode itemNode : itemsNode) {
                        ItineraryItemVO item = new ItineraryItemVO();
                        item.setDayNum(dayPlan.getDayNumber());
                        item.setOrderNum(orderNum++);
                        item.setItemName(itemNode.has("itemName") ? itemNode.get("itemName").asText() : "");
                        item.setItemDesc(itemNode.has("itemDesc") ? itemNode.get("itemDesc").asText() : "");
                        item.setItemPrice(itemNode.has("itemPrice")
                                ? BigDecimal.valueOf(itemNode.get("itemPrice").asDouble())
                                : BigDecimal.ZERO);
                        item.setStartTime(itemNode.has("startTime") ? itemNode.get("startTime").asText() : null);
                        item.setEndTime(itemNode.has("endTime") ? itemNode.get("endTime").asText() : null);
                        items.add(item);
                    }
                }
                dayPlan.setItems(items);
                dayPlans.add(dayPlan);
            }
        }
        vo.setDayPlans(dayPlans);

        return vo;
    }

    // ==================== 从 dayPlansJson 构建当前行程文本 ====================

    private String buildCurrentItineraryText(Itinerary itinerary) {
        StringBuilder sb = new StringBuilder();
        sb.append("行程标题：").append(itinerary.getTitle()).append("\n");
        sb.append("目的地：").append(itinerary.getDestination()).append("\n");
        sb.append("天数：").append(itinerary.getDays()).append("天\n\n");

        if (itinerary.getDayPlansJson() != null && !itinerary.getDayPlansJson().isEmpty()) {
            try {
                JsonNode daysNode = objectMapper.readTree(itinerary.getDayPlansJson());
                if (daysNode.isArray()) {
                    for (JsonNode dayNode : daysNode) {
                        int dayNum = dayNode.has("dayNumber") ? dayNode.get("dayNumber").asInt() : 1;
                        sb.append("第").append(dayNum).append("天：\n");
                        JsonNode itemsNode = dayNode.get("items");
                        if (itemsNode != null && itemsNode.isArray()) {
                            for (JsonNode itemNode : itemsNode) {
                                sb.append("- ");
                                if (itemNode.has("startTime")) sb.append(itemNode.get("startTime").asText()).append(" ");
                                if (itemNode.has("itemName")) sb.append(itemNode.get("itemName").asText());
                                if (itemNode.has("itemPrice")) sb.append("（").append(itemNode.get("itemPrice").asDouble()).append("元）");
                                sb.append("\n");
                            }
                        }
                        sb.append("\n");
                    }
                }
            } catch (Exception e) {
                log.warn("解析 dayPlansJson 失败", e);
            }
        }
        return sb.toString();
    }

    // ==================== JSON 解析 ====================

    private JsonNode parseJsonResponse(String response) {
        try {
            String jsonStr = response.trim();
            // 移除 markdown 代码块标记
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            }
            if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.substring(3);
            }
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            jsonStr = jsonStr.trim();
            // 移除控制字符
            jsonStr = jsonStr.replaceAll("[\n\r\t]", "");
            return objectMapper.readTree(jsonStr);
        } catch (Exception e) {
            log.error("AI原始返回内容：{}，解析异常", response, e);
            throw new BusinessException("行程生成失败，请重试");
        }
    }
}
