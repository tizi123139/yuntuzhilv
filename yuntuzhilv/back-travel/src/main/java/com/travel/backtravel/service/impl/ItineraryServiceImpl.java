package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.BookingCreateDTO;
import com.travel.backtravel.dto.ItineraryCreateDTO;
import com.travel.backtravel.dto.ItineraryItemDTO;
import com.travel.backtravel.entity.*;
import com.travel.backtravel.exception.BusinessException;
import com.travel.backtravel.mapper.*;
import com.travel.backtravel.service.ItineraryService;
import com.travel.backtravel.vo.ItineraryItemVO;
import com.travel.backtravel.vo.ItineraryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItineraryServiceImpl implements ItineraryService {

    private final ItineraryMapper itineraryMapper;
    private final ItineraryAttractionMapper itineraryAttractionMapper;
    private final ItineraryHotelMapper itineraryHotelMapper;
    private final ItineraryTrafficMapper itineraryTrafficMapper;
    private final BookingOrderMapper bookingOrderMapper;

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
        itinerary.setTotalCost(BigDecimal.ZERO);
        itinerary.setInterests(dto.getInterests());
        itinerary.setPeople(dto.getPeople() != null ? dto.getPeople() : 1);
        itinerary.setIsTemp(dto.getIsTemp() != null ? dto.getIsTemp() : 1);
        itinerary.setStatus("planned");
        itinerary.setIsArchived(0);
        itinerary.setIsDeleted(0);
        itinerary.setCreateTime(LocalDateTime.now());
        itinerary.setUpdateTime(LocalDateTime.now());

        itineraryMapper.insert(itinerary);

        BigDecimal totalCost = saveAttractionDetails(itinerary.getItineraryId(), dto.getAttractions());
        totalCost = totalCost.add(saveHotelDetails(itinerary.getItineraryId(), dto.getHotels()));
        totalCost = totalCost.add(saveTrafficDetails(itinerary.getItineraryId(), dto.getTraffics()));

        itinerary.setTotalCost(totalCost);
        itineraryMapper.updateById(itinerary);

        return convertToVO(itinerary, true);
    }

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

    @Override
    public Page<ItineraryVO> getUserItineraries(Long userId, Integer pageNum, Integer pageSize) {
        Page<Itinerary> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Itinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Itinerary::getUserId, userId)
                .eq(Itinerary::getIsDeleted, 0)
                .orderByDesc(Itinerary::getCreateTime);
        Page<Itinerary> resultPage = itineraryMapper.selectPage(page, wrapper);
        return resultPage.convert(itinerary -> convertToVO(itinerary, false));
    }

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

        deleteAllDetails(itineraryId);

        BigDecimal totalCost = BigDecimal.ZERO;
        if (dto.getAttractions() != null) totalCost = totalCost.add(saveAttractionDetails(itineraryId, dto.getAttractions()));
        if (dto.getHotels() != null) totalCost = totalCost.add(saveHotelDetails(itineraryId, dto.getHotels()));
        if (dto.getTraffics() != null) totalCost = totalCost.add(saveTrafficDetails(itineraryId, dto.getTraffics()));

        itinerary.setTotalCost(totalCost);
        itineraryMapper.updateById(itinerary);

        return convertToVO(itinerary, true);
    }

    @Override
    @Transactional
    public void deleteItinerary(Long userId, String itineraryId) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) throw new BusinessException("行程不存在");
        if (!itinerary.getUserId().equals(userId)) throw new BusinessException("无权删除此行程");
        deleteAllDetails(itineraryId);
        itineraryMapper.deleteById(itineraryId);
    }

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

    @Override
    public String exportToPdf(Long userId, String itineraryId) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) throw new BusinessException("行程不存在");
        if (!itinerary.getUserId().equals(userId)) throw new BusinessException("无权导出此行程");

        StringBuilder sb = new StringBuilder();
        sb.append("===== 旅游行程单 =====\n\n");
        sb.append("行程标题：").append(itinerary.getTitle()).append("\n");
        sb.append("出发地：").append(itinerary.getStartCity()).append("\n");
        sb.append("目的地：").append(itinerary.getDestination()).append("\n");
        sb.append("行程天数：").append(itinerary.getDays()).append("天\n");
        sb.append("总费用：").append(itinerary.getTotalCost()).append("元\n");
        return sb.toString();
    }

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

    private String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private BigDecimal saveAttractionDetails(String itineraryId, List<ItineraryItemDTO> items) {
        BigDecimal total = BigDecimal.ZERO;
        if (items == null) return total;
        for (ItineraryItemDTO dto : items) {
            ItineraryAttraction entity = new ItineraryAttraction();
            entity.setItineraryId(itineraryId);
            entity.setDayNum(dto.getDayNum());
            entity.setOrderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0);
            entity.setAttractionId(dto.getResourceId());
            entity.setItemPrice(dto.getItemPrice() != null ? dto.getItemPrice() : BigDecimal.ZERO);
            entity.setStartTime(dto.getStartTime());
            entity.setEndTime(dto.getEndTime());
            entity.setItemDesc(dto.getItemDesc());
            entity.setIsDeleted(0);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            itineraryAttractionMapper.insert(entity);
            total = total.add(entity.getItemPrice());
        }
        return total;
    }

    private BigDecimal saveHotelDetails(String itineraryId, List<ItineraryItemDTO> items) {
        BigDecimal total = BigDecimal.ZERO;
        if (items == null) return total;
        for (ItineraryItemDTO dto : items) {
            ItineraryHotel entity = new ItineraryHotel();
            entity.setItineraryId(itineraryId);
            entity.setDayNum(dto.getDayNum());
            entity.setOrderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0);
            entity.setHotelId(dto.getResourceId());
            entity.setItemPrice(dto.getItemPrice() != null ? dto.getItemPrice() : BigDecimal.ZERO);
            entity.setCheckInTime(dto.getCheckInTime());
            entity.setCheckOutTime(dto.getCheckOutTime());
            entity.setItemDesc(dto.getItemDesc());
            entity.setIsDeleted(0);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            itineraryHotelMapper.insert(entity);
            total = total.add(entity.getItemPrice());
        }
        return total;
    }

    private BigDecimal saveTrafficDetails(String itineraryId, List<ItineraryItemDTO> items) {
        BigDecimal total = BigDecimal.ZERO;
        if (items == null) return total;
        for (ItineraryItemDTO dto : items) {
            ItineraryTraffic entity = new ItineraryTraffic();
            entity.setItineraryId(itineraryId);
            entity.setDayNum(dto.getDayNum());
            entity.setOrderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0);
            entity.setTrafficId(dto.getResourceId());
            entity.setItemPrice(dto.getItemPrice() != null ? dto.getItemPrice() : BigDecimal.ZERO);
            entity.setStartTime(dto.getStartTime());
            entity.setEndTime(dto.getEndTime());
            entity.setItemDesc(dto.getItemDesc());
            entity.setIsDeleted(0);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            itineraryTrafficMapper.insert(entity);
            total = total.add(entity.getItemPrice());
        }
        return total;
    }

    private void deleteAllDetails(String itineraryId) {
        itineraryAttractionMapper.delete(new LambdaQueryWrapper<ItineraryAttraction>().eq(ItineraryAttraction::getItineraryId, itineraryId));
        itineraryHotelMapper.delete(new LambdaQueryWrapper<ItineraryHotel>().eq(ItineraryHotel::getItineraryId, itineraryId));
        itineraryTrafficMapper.delete(new LambdaQueryWrapper<ItineraryTraffic>().eq(ItineraryTraffic::getItineraryId, itineraryId));
    }

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
        vo.setInterests(itinerary.getInterests());
        vo.setTravelTips(itinerary.getTravelTips());
        vo.setPeople(itinerary.getPeople());
        vo.setStatus(itinerary.getStatus());
        vo.setIsTemp(itinerary.getIsTemp());
        vo.setIsArchived(itinerary.getIsArchived());
        vo.setCreateTime(itinerary.getCreateTime());

        if (loadDetails) {
            String id = itinerary.getItineraryId();
            List<ItineraryAttraction> attractions = itineraryAttractionMapper.selectList(
                    new LambdaQueryWrapper<ItineraryAttraction>().eq(ItineraryAttraction::getItineraryId, id).eq(ItineraryAttraction::getIsDeleted, 0).orderByAsc(ItineraryAttraction::getDayNum, ItineraryAttraction::getOrderNum));
            vo.setAttractions(attractions.stream().map(this::convertAttractionToVO).collect(Collectors.toList()));

            List<ItineraryHotel> hotels = itineraryHotelMapper.selectList(
                    new LambdaQueryWrapper<ItineraryHotel>().eq(ItineraryHotel::getItineraryId, id).eq(ItineraryHotel::getIsDeleted, 0).orderByAsc(ItineraryHotel::getDayNum, ItineraryHotel::getOrderNum));
            vo.setHotels(hotels.stream().map(this::convertHotelToVO).collect(Collectors.toList()));

            List<ItineraryTraffic> traffics = itineraryTrafficMapper.selectList(
                    new LambdaQueryWrapper<ItineraryTraffic>().eq(ItineraryTraffic::getItineraryId, id).eq(ItineraryTraffic::getIsDeleted, 0).orderByAsc(ItineraryTraffic::getDayNum, ItineraryTraffic::getOrderNum));
            vo.setTraffics(traffics.stream().map(this::convertTrafficToVO).collect(Collectors.toList()));
        }
        return vo;
    }

    private ItineraryItemVO convertAttractionToVO(ItineraryAttraction e) {
        ItineraryItemVO vo = new ItineraryItemVO();
        vo.setDetailId(e.getDetailId());
        vo.setDayNum(e.getDayNum());
        vo.setOrderNum(e.getOrderNum());
        vo.setResourceId(e.getAttractionId());
        vo.setItemPrice(e.getItemPrice());
        vo.setStartTime(e.getStartTime());
        vo.setEndTime(e.getEndTime());
        vo.setItemDesc(e.getItemDesc());
        vo.setCreateTime(e.getCreateTime());
        return vo;
    }

    private ItineraryItemVO convertHotelToVO(ItineraryHotel e) {
        ItineraryItemVO vo = new ItineraryItemVO();
        vo.setDetailId(e.getDetailId());
        vo.setDayNum(e.getDayNum());
        vo.setOrderNum(e.getOrderNum());
        vo.setResourceId(e.getHotelId());
        vo.setItemPrice(e.getItemPrice());
        vo.setCheckInTime(e.getCheckInTime());
        vo.setCheckOutTime(e.getCheckOutTime());
        vo.setItemDesc(e.getItemDesc());
        vo.setCreateTime(e.getCreateTime());
        return vo;
    }

    private ItineraryItemVO convertTrafficToVO(ItineraryTraffic e) {
        ItineraryItemVO vo = new ItineraryItemVO();
        vo.setDetailId(e.getDetailId());
        vo.setDayNum(e.getDayNum());
        vo.setOrderNum(e.getOrderNum());
        vo.setResourceId(e.getTrafficId());
        vo.setItemPrice(e.getItemPrice());
        vo.setStartTime(e.getStartTime());
        vo.setEndTime(e.getEndTime());
        vo.setItemDesc(e.getItemDesc());
        vo.setCreateTime(e.getCreateTime());
        return vo;
    }
}
