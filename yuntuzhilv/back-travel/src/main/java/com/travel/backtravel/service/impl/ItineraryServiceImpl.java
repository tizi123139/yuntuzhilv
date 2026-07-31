package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.travel.backtravel.dto.BookingCreateDTO;
import com.travel.backtravel.dto.ItineraryCreateDTO;
import com.travel.backtravel.dto.ItineraryItemDTO;
import com.travel.backtravel.entity.BookingOrder;
import com.travel.backtravel.entity.Itinerary;
import com.travel.backtravel.exception.BusinessException;
import com.travel.backtravel.mapper.BookingOrderMapper;
import com.travel.backtravel.mapper.ItineraryMapper;
import com.travel.backtravel.service.ItineraryService;
import com.travel.backtravel.vo.ItineraryItemVO;
import com.travel.backtravel.vo.ItineraryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItineraryServiceImpl implements ItineraryService {

    private final ItineraryMapper itineraryMapper;
    private final BookingOrderMapper bookingOrderMapper;
    private final ObjectMapper objectMapper;

    // ==================== 创建行程（手动，存 dayPlansJson） ====================

    @Override
    @Transactional
    public ItineraryVO createItinerary(Long userId, ItineraryCreateDTO dto) {
        Itinerary itinerary = new Itinerary();
        itinerary.setItineraryId(generateUUID());
        itinerary.setUserId(userId);
        itinerary.setTitle(dto.getTitle() != null ? dto.getTitle() : dto.getDestination() + "之旅");
        itinerary.setStartCity(dto.getStartCity());
        itinerary.setDestination(dto.getDestination());
        itinerary.setStartDate(dto.getStartDate());
        itinerary.setEndDate(dto.getEndDate());
        itinerary.setDays(dto.getDays() != null ? dto.getDays() : (int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1);
        itinerary.setTotalBudget(dto.getTotalBudget() != null ? dto.getTotalBudget() : BigDecimal.ZERO);
        itinerary.setInterests(dto.getInterests());
        itinerary.setPeople(dto.getPeople() != null ? dto.getPeople() : 1);
        itinerary.setIsTemp(dto.getIsTemp() != null ? dto.getIsTemp() : 1);
        itinerary.setStatus(itinerary.getIsTemp() == 1 ? "planned" : "ACTIVE");
        itinerary.setIsArchived(0);
        itinerary.setIsDeleted(0);
        itinerary.setCreateTime(LocalDateTime.now());
        itinerary.setUpdateTime(LocalDateTime.now());

        // 将 attractions/hotels/traffics 合并为 dayPlansJson
        String dayPlansJson = buildDayPlansJson(dto);
        itinerary.setDayPlansJson(dayPlansJson);

        // 从 items 累加 totalCost
        BigDecimal totalCost = calculateTotalCostFromJson(dayPlansJson);
        itinerary.setTotalCost(totalCost);

        itineraryMapper.insert(itinerary);

        return convertToVO(itinerary, true);
    }

    // ==================== 保存 AI 行程（直接用 VO） ====================

    @Override
    @Transactional
    public ItineraryVO saveAiItinerary(Long userId, ItineraryVO vo) {
        Itinerary itinerary = new Itinerary();
        itinerary.setItineraryId(generateUUID());
        itinerary.setUserId(userId);
        itinerary.setTitle(vo.getTitle() != null ? vo.getTitle() : vo.getDestination() + "之旅");
        itinerary.setStartCity(vo.getStartCity());
        itinerary.setDestination(vo.getDestination());
        itinerary.setStartDate(vo.getStartDate());
        itinerary.setEndDate(vo.getEndDate());
        itinerary.setDays(vo.getDays() != null ? vo.getDays() : 1);
        itinerary.setTotalBudget(vo.getTotalBudget() != null ? vo.getTotalBudget() : BigDecimal.ZERO);
        itinerary.setInterests(vo.getInterests() != null && !vo.getInterests().isEmpty()
                ? String.join(",", vo.getInterests()) : null);
        itinerary.setPeople(vo.getPeople() != null ? vo.getPeople() : 1);
        itinerary.setIsTemp(0);
        itinerary.setStatus("ACTIVE");
        itinerary.setIsArchived(0);
        itinerary.setIsDeleted(0);
        itinerary.setCreateTime(LocalDateTime.now());
        itinerary.setUpdateTime(LocalDateTime.now());

        // 将 VO 的 dayPlans 序列化为 JSON
        String dayPlansJson = serializeDayPlans(vo.getDayPlans());
        itinerary.setDayPlansJson(dayPlansJson);

        // 累加 totalCost
        BigDecimal totalCost = calculateTotalCostFromJson(dayPlansJson);
        itinerary.setTotalCost(totalCost);

        itineraryMapper.insert(itinerary);
        log.info("AI行程已保存: {} (仅主表)", itinerary.getItineraryId());

        return convertToVO(itinerary, true);
    }

    // ==================== 查询行程详情 ====================

    @Override
    public ItineraryVO getItineraryById(Long userId, String itineraryId) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }
        if (!itinerary.getUserId().equals(userId)) {
            throw new BusinessException("无权访问此行程");
        }
        return convertToVO(itinerary, true);
    }

    // ==================== 用户行程列表 ====================

    @Override
    public Page<ItineraryVO> getUserItineraries(Long userId, Integer pageNum, Integer pageSize) {
        Page<Itinerary> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Itinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Itinerary::getUserId, userId)
                .eq(Itinerary::getIsDeleted, 0)
                .orderByDesc(Itinerary::getCreateTime);
        Page<Itinerary> resultPage = itineraryMapper.selectPage(page, wrapper);

        Page<ItineraryVO> voPage = new Page<>();
        voPage.setRecords(resultPage.getRecords().stream()
                .map(itinerary -> convertToVO(itinerary, false))
                .collect(Collectors.toList()));
        voPage.setTotal(resultPage.getTotal());
        voPage.setSize(resultPage.getSize());
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setPages(resultPage.getPages());
        return voPage;
    }

    // ==================== 更新行程 ====================

    @Override
    @Transactional
    public ItineraryVO updateItinerary(Long userId, String itineraryId, ItineraryCreateDTO dto) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }
        if (!itinerary.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此行程");
        }

        itinerary.setTitle(dto.getTitle() != null ? dto.getTitle() : itinerary.getTitle());
        itinerary.setStartCity(dto.getStartCity() != null ? dto.getStartCity() : itinerary.getStartCity());
        itinerary.setDestination(dto.getDestination() != null ? dto.getDestination() : itinerary.getDestination());
        itinerary.setStartDate(dto.getStartDate() != null ? dto.getStartDate() : itinerary.getStartDate());
        itinerary.setEndDate(dto.getEndDate() != null ? dto.getEndDate() : itinerary.getEndDate());
        if (dto.getDays() != null) itinerary.setDays(dto.getDays());
        if (dto.getTotalBudget() != null) itinerary.setTotalBudget(dto.getTotalBudget());
        if (dto.getInterests() != null) itinerary.setInterests(dto.getInterests());
        if (dto.getPeople() != null) itinerary.setPeople(dto.getPeople());
        itinerary.setUpdateTime(LocalDateTime.now());

        // 更新 dayPlansJson
        String dayPlansJson = buildDayPlansJson(dto);
        itinerary.setDayPlansJson(dayPlansJson);
        itinerary.setTotalCost(calculateTotalCostFromJson(dayPlansJson));

        itineraryMapper.updateById(itinerary);

        return convertToVO(itinerary, true);
    }

    // ==================== 删除行程 ====================

    @Override
    @Transactional
    public void deleteItinerary(Long userId, String itineraryId) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) throw new BusinessException("行程不存在");
        if (!itinerary.getUserId().equals(userId)) throw new BusinessException("无权删除此行程");
        itineraryMapper.deleteById(itineraryId);
    }

    // ==================== 归档行程 ====================

    @Override
    public void archiveItinerary(Long userId, String itineraryId) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) throw new BusinessException("行程不存在");
        if (!itinerary.getUserId().equals(userId)) throw new BusinessException("无权修改此行程");
        itinerary.setIsArchived(1);
        itinerary.setStatus("completed");
        itinerary.setUpdateTime(LocalDateTime.now());
        itineraryMapper.updateById(itinerary);
    }

    // ==================== 导出文本 ====================

    @Override
    public String exportToPdf(Long userId, String itineraryId) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) throw new BusinessException("行程不存在");
        if (!itinerary.getUserId().equals(userId)) throw new BusinessException("无权导出此行程");

        StringBuilder sb = new StringBuilder();
        sb.append("===== 旅游行程单 =====\n\n");
        if (itinerary.getTitle() != null) sb.append("行程标题：").append(itinerary.getTitle()).append("\n");
        if (itinerary.getStartCity() != null) sb.append("出发地：").append(itinerary.getStartCity()).append("\n");
        if (itinerary.getDestination() != null) sb.append("目的地：").append(itinerary.getDestination()).append("\n");
        if (itinerary.getDays() != null) sb.append("行程天数：").append(itinerary.getDays()).append("天\n");
        if (itinerary.getTotalCost() != null) sb.append("总费用：").append(itinerary.getTotalCost()).append("元\n");
        if (itinerary.getInterests() != null) sb.append("兴趣：").append(itinerary.getInterests()).append("\n");
        return sb.toString();
    }

    // ==================== 创建预订 ====================

    @Override
    @Transactional
    public void createBooking(Long userId, BookingCreateDTO dto) {
        BookingOrder order = new BookingOrder();
        order.setOrderId("ORD" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setItineraryId(dto.getItineraryId());
        order.setResourceType(dto.getResourceType());
        order.setResourceId(dto.getResourceId());
        order.setResourceName(dto.getResourceName());
        order.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
        order.setTotalPrice(dto.getTotalPrice() != null ? dto.getTotalPrice() : BigDecimal.ZERO);
        order.setCheckIn(dto.getCheckIn());
        order.setCheckOut(dto.getCheckOut());
        order.setOrderStatus("待支付");
        order.setIsDeleted(0);
        order.setCreateTime(LocalDateTime.now());
        bookingOrderMapper.insert(order);
    }

    // ==================== 私有辅助方法 ====================

    private String generateUUID() {
        return "TRP" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    /**
     * 将 ItineraryCreateDTO 的 attractions/hotels/traffics 合并为 dayPlansJson
     */
    private String buildDayPlansJson(ItineraryCreateDTO dto) {
        Map<Integer, List<ItineraryItemDTO>> dayMap = new TreeMap<>();

        if (dto.getAttractions() != null) {
            for (ItineraryItemDTO item : dto.getAttractions()) {
                dayMap.computeIfAbsent(item.getDayNum(), k -> new ArrayList<>()).add(item);
            }
        }
        if (dto.getHotels() != null) {
            for (ItineraryItemDTO item : dto.getHotels()) {
                dayMap.computeIfAbsent(item.getDayNum(), k -> new ArrayList<>()).add(item);
            }
        }
        if (dto.getTraffics() != null) {
            for (ItineraryItemDTO item : dto.getTraffics()) {
                dayMap.computeIfAbsent(item.getDayNum(), k -> new ArrayList<>()).add(item);
            }
        }

        ArrayNode daysArray = objectMapper.createArrayNode();
        for (Map.Entry<Integer, List<ItineraryItemDTO>> entry : dayMap.entrySet()) {
            ObjectNode dayNode = objectMapper.createObjectNode();
            dayNode.put("dayNumber", entry.getKey());
            ArrayNode itemsArray = objectMapper.createArrayNode();

            entry.getValue().sort(Comparator.comparingInt(i -> i.getOrderNum() != null ? i.getOrderNum() : 0));

            for (ItineraryItemDTO item : entry.getValue()) {
                ObjectNode itemNode = objectMapper.createObjectNode();
                itemNode.put("itemName", item.getItemName() != null ? item.getItemName() : "");
                itemNode.put("itemDesc", item.getItemDesc() != null ? item.getItemDesc() : "");
                itemNode.put("itemPrice", item.getItemPrice() != null ? item.getItemPrice().doubleValue() : 0);
                itemNode.put("startTime", item.getStartTime() != null ? item.getStartTime() : "");
                itemNode.put("endTime", item.getEndTime() != null ? item.getEndTime() : "");
                itemsArray.add(itemNode);
            }
            dayNode.set("items", itemsArray);
            daysArray.add(dayNode);
        }
        return daysArray.toString();
    }

    /**
     * 将 VO 的 dayPlans 序列化为 JSON 字符串
     */
    private String serializeDayPlans(List<ItineraryVO.DayPlan> dayPlans) {
        if (dayPlans == null || dayPlans.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(dayPlans);
        } catch (Exception e) {
            log.error("序列化 dayPlans 失败", e);
            return "[]";
        }
    }

    /**
     * 从 dayPlansJson 累加 totalCost
     */
    private BigDecimal calculateTotalCostFromJson(String dayPlansJson) {
        BigDecimal total = BigDecimal.ZERO;
        if (dayPlansJson == null || dayPlansJson.isEmpty()) {
            return total;
        }
        try {
            JsonNode daysNode = objectMapper.readTree(dayPlansJson);
            if (daysNode.isArray()) {
                for (JsonNode dayNode : daysNode) {
                    JsonNode itemsNode = dayNode.get("items");
                    if (itemsNode != null && itemsNode.isArray()) {
                        for (JsonNode itemNode : itemsNode) {
                            if (itemNode.has("itemPrice")) {
                                try {
                                    total = total.add(BigDecimal.valueOf(itemNode.get("itemPrice").asDouble()));
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析 dayPlansJson 计算 totalCost 失败", e);
        }
        return total;
    }

    /**
     * 将 Itinerary 实体转为 VO（解析 dayPlansJson 构建 dayPlans）
     */
    private ItineraryVO convertToVO(Itinerary itinerary, boolean loadDetails) {
        ItineraryVO vo = new ItineraryVO();
        vo.setItineraryId(itinerary.getItineraryId());
        vo.setTitle(itinerary.getTitle());
        vo.setStartCity(itinerary.getStartCity());
        vo.setDestination(itinerary.getDestination());
        vo.setStartDate(itinerary.getStartDate());
        vo.setEndDate(itinerary.getEndDate());
        vo.setDays(itinerary.getDays());
        vo.setTotalBudget(itinerary.getTotalBudget());
        vo.setTotalCost(itinerary.getTotalCost());
        vo.setInterests(itinerary.getInterests() != null
                ? new ArrayList<>(Arrays.asList(itinerary.getInterests().split(",")))
                : new ArrayList<>());
        vo.setTravelTips(itinerary.getTravelTips());
        vo.setPeople(itinerary.getPeople());
        vo.setStatus(itinerary.getStatus());
        vo.setIsTemp(itinerary.getIsTemp());
        vo.setIsArchived(itinerary.getIsArchived());
        vo.setCreateTime(itinerary.getCreateTime());

        if (loadDetails && itinerary.getDayPlansJson() != null && !itinerary.getDayPlansJson().isEmpty()) {
            try {
                JsonNode daysNode = objectMapper.readTree(itinerary.getDayPlansJson());
                List<ItineraryVO.DayPlan> dayPlans = new ArrayList<>();
                if (daysNode.isArray()) {
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
            } catch (Exception e) {
                log.warn("解析 dayPlansJson 失败", e);
            }
        }
        return vo;
    }
}
