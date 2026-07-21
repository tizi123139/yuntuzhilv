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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItineraryServiceImpl implements ItineraryService {

    private final ItineraryMapper itineraryMapper;
    private final ItineraryItemMapper itineraryItemMapper;
    private final BookingRecordMapper bookingRecordMapper;
    private final HotelMapper hotelMapper;
    private final AttractionMapper attractionMapper;

    @Override
    @Transactional
    public ItineraryVO createItinerary(Long userId, ItineraryCreateDTO dto) {
        Itinerary itinerary = new Itinerary();
        itinerary.setUserId(userId);
        itinerary.setDestinationCity(dto.getDestinationCity());
        itinerary.setDepartureCity(dto.getDepartureCity());
        itinerary.setStartDate(dto.getStartDate());
        itinerary.setEndDate(dto.getEndDate());
        itinerary.setDays((int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1);
        itinerary.setBudget(dto.getBudget());
        itinerary.setInterests(dto.getInterests());
        itinerary.setTitle(dto.getDestinationCity() + "之旅");
        itinerary.setStatus("ACTIVE");
        itinerary.setIsArchived(0);

        itineraryMapper.insert(itinerary);

        BigDecimal totalCost = BigDecimal.ZERO;
        if (dto.getItems() != null) {
            for (ItineraryItemDTO itemDTO : dto.getItems()) {
                ItineraryItem item = new ItineraryItem();
                item.setItineraryId(itinerary.getId());
                item.setDayNumber(itemDTO.getDayNumber());
                item.setItemType(itemDTO.getItemType());
                item.setItemId(itemDTO.getItemId());
                item.setItemName(itemDTO.getItemName());
                item.setItemDesc(itemDTO.getItemDesc());
                item.setItemPrice(itemDTO.getItemPrice() != null ? itemDTO.getItemPrice() : BigDecimal.ZERO);
                item.setStartTime(itemDTO.getStartTime());
                item.setEndTime(itemDTO.getEndTime());
                item.setOrderNum(itemDTO.getOrderNum() != null ? itemDTO.getOrderNum() : 0);

                itineraryItemMapper.insert(item);
                totalCost = totalCost.add(item.getItemPrice());
            }
        }

        itinerary.setTotalCost(totalCost);
        itineraryMapper.updateById(itinerary);

        return convertToVO(itinerary, true);
    }

    @Override
    public ItineraryVO getItineraryById(Long userId, Long itineraryId) {
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
                .orderByDesc(Itinerary::getCreatedAt);

        Page<Itinerary> resultPage = itineraryMapper.selectPage(page, wrapper);
        return resultPage.convert(itinerary -> convertToVO(itinerary, false));
    }

    @Override
    @Transactional
    public ItineraryVO updateItinerary(Long userId, Long itineraryId, ItineraryCreateDTO dto) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }

        if (!itinerary.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此行程");
        }

        itinerary.setDestinationCity(dto.getDestinationCity());
        itinerary.setDepartureCity(dto.getDepartureCity());
        itinerary.setStartDate(dto.getStartDate());
        itinerary.setEndDate(dto.getEndDate());
        itinerary.setDays((int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1);
        itinerary.setBudget(dto.getBudget());
        itinerary.setInterests(dto.getInterests());

        itineraryMapper.updateById(itinerary);

        LambdaQueryWrapper<ItineraryItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(ItineraryItem::getItineraryId, itineraryId);
        itineraryItemMapper.delete(itemWrapper);

        BigDecimal totalCost = BigDecimal.ZERO;
        if (dto.getItems() != null) {
            for (ItineraryItemDTO itemDTO : dto.getItems()) {
                ItineraryItem item = new ItineraryItem();
                item.setItineraryId(itineraryId);
                item.setDayNumber(itemDTO.getDayNumber());
                item.setItemType(itemDTO.getItemType());
                item.setItemId(itemDTO.getItemId());
                item.setItemName(itemDTO.getItemName());
                item.setItemDesc(itemDTO.getItemDesc());
                item.setItemPrice(itemDTO.getItemPrice() != null ? itemDTO.getItemPrice() : BigDecimal.ZERO);
                item.setStartTime(itemDTO.getStartTime());
                item.setEndTime(itemDTO.getEndTime());
                item.setOrderNum(itemDTO.getOrderNum() != null ? itemDTO.getOrderNum() : 0);

                itineraryItemMapper.insert(item);
                totalCost = totalCost.add(item.getItemPrice());
            }
        }

        itinerary.setTotalCost(totalCost);
        itineraryMapper.updateById(itinerary);

        return convertToVO(itinerary, true);
    }

    @Override
    @Transactional
    public void deleteItinerary(Long userId, Long itineraryId) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }

        if (!itinerary.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此行程");
        }

        itineraryItemMapper.delete(new LambdaQueryWrapper<ItineraryItem>().eq(ItineraryItem::getItineraryId, itineraryId));
        itineraryMapper.deleteById(itineraryId);
    }

    @Override
    @Transactional
    public ItineraryVO addItem(Long userId, Long itineraryId, ItineraryItemDTO dto) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }

        if (!itinerary.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此行程");
        }

        ItineraryItem item = new ItineraryItem();
        item.setItineraryId(itineraryId);
        item.setDayNumber(dto.getDayNumber());
        item.setItemType(dto.getItemType());
        item.setItemId(dto.getItemId());
        item.setItemName(dto.getItemName());
        item.setItemDesc(dto.getItemDesc());
        item.setItemPrice(dto.getItemPrice() != null ? dto.getItemPrice() : BigDecimal.ZERO);
        item.setStartTime(dto.getStartTime());
        item.setEndTime(dto.getEndTime());
        item.setOrderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0);

        itineraryItemMapper.insert(item);

        itinerary.setTotalCost(itinerary.getTotalCost().add(item.getItemPrice()));
        itineraryMapper.updateById(itinerary);

        return convertToVO(itinerary, true);
    }

    @Override
    @Transactional
    public ItineraryVO updateItem(Long userId, Long itineraryId, Long itemId, ItineraryItemDTO dto) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }

        if (!itinerary.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此行程");
        }

        ItineraryItem item = itineraryItemMapper.selectById(itemId);
        if (item == null || !item.getItineraryId().equals(itineraryId)) {
            throw new BusinessException("行程项目不存在");
        }

        BigDecimal oldPrice = item.getItemPrice();
        item.setDayNumber(dto.getDayNumber());
        item.setItemType(dto.getItemType());
        item.setItemId(dto.getItemId());
        item.setItemName(dto.getItemName());
        item.setItemDesc(dto.getItemDesc());
        item.setItemPrice(dto.getItemPrice() != null ? dto.getItemPrice() : BigDecimal.ZERO);
        item.setStartTime(dto.getStartTime());
        item.setEndTime(dto.getEndTime());
        item.setOrderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0);

        itineraryItemMapper.updateById(item);

        itinerary.setTotalCost(itinerary.getTotalCost().subtract(oldPrice).add(item.getItemPrice()));
        itineraryMapper.updateById(itinerary);

        return convertToVO(itinerary, true);
    }

    @Override
    @Transactional
    public ItineraryVO removeItem(Long userId, Long itineraryId, Long itemId) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }

        if (!itinerary.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此行程");
        }

        ItineraryItem item = itineraryItemMapper.selectById(itemId);
        if (item == null || !item.getItineraryId().equals(itineraryId)) {
            throw new BusinessException("行程项目不存在");
        }

        itinerary.setTotalCost(itinerary.getTotalCost().subtract(item.getItemPrice()));
        itineraryMapper.updateById(itinerary);

        itineraryItemMapper.deleteById(itemId);

        return convertToVO(itinerary, true);
    }

    @Override
    public void archiveItinerary(Long userId, Long itineraryId) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }

        if (!itinerary.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此行程");
        }

        itinerary.setIsArchived(1);
        itinerary.setStatus("ARCHIVED");
        itineraryMapper.updateById(itinerary);
    }

    @Override
    public String exportToPdf(Long userId, Long itineraryId) {
        Itinerary itinerary = itineraryMapper.selectById(itineraryId);
        if (itinerary == null) {
            throw new BusinessException("行程不存在");
        }

        if (!itinerary.getUserId().equals(userId)) {
            throw new BusinessException("无权导出此行程");
        }

        List<ItineraryItem> items = itineraryItemMapper.selectList(
                new LambdaQueryWrapper<ItineraryItem>()
                        .eq(ItineraryItem::getItineraryId, itineraryId)
                        .orderByAsc(ItineraryItem::getDayNumber, ItineraryItem::getOrderNum)
        );

        StringBuilder pdfContent = new StringBuilder();
        pdfContent.append("===== 旅游行程单 =====\n\n");
        pdfContent.append("行程标题：").append(itinerary.getTitle()).append("\n");
        pdfContent.append("出发地：").append(itinerary.getDepartureCity()).append("\n");
        pdfContent.append("目的地：").append(itinerary.getDestinationCity()).append("\n");
        pdfContent.append("出发日期：").append(itinerary.getStartDate()).append("\n");
        pdfContent.append("结束日期：").append(itinerary.getEndDate()).append("\n");
        pdfContent.append("行程天数：").append(itinerary.getDays()).append("天\n");
        pdfContent.append("预算：").append(itinerary.getBudget()).append("元\n");
        pdfContent.append("兴趣偏好：").append(itinerary.getInterests()).append("\n");
        pdfContent.append("总费用：").append(itinerary.getTotalCost()).append("元\n\n");

        int currentDay = 0;
        for (ItineraryItem item : items) {
            if (item.getDayNumber() != currentDay) {
                currentDay = item.getDayNumber();
                pdfContent.append("--- 第").append(currentDay).append("天 ---\n");
            }
            pdfContent.append(item.getStartTime()).append(" - ").append(item.getEndTime()).append("：");
            pdfContent.append(item.getItemName()).append("（").append(item.getItemType()).append("）\n");
            if (item.getItemDesc() != null) {
                pdfContent.append("    ").append(item.getItemDesc()).append("\n");
            }
            pdfContent.append("    价格：").append(item.getItemPrice()).append("元\n");
        }

        pdfContent.append("\n===== 行程单结束 =====\n");
        return pdfContent.toString();
    }

    @Override
    @Transactional
    public void bookItem(Long userId, BookingCreateDTO dto) {
        ItineraryItem item = itineraryItemMapper.selectById(dto.getItineraryItemId());
        if (item == null) {
            throw new BusinessException("行程项目不存在");
        }

        BookingRecord record = new BookingRecord();
        record.setUserId(userId);
        record.setItineraryItemId(dto.getItineraryItemId());
        record.setBookingType(dto.getBookingType());
        record.setTargetId(dto.getTargetId());
        record.setTargetName(dto.getTargetName() != null ? dto.getTargetName() : item.getItemName());
        record.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
        record.setTotalPrice(item.getItemPrice().multiply(BigDecimal.valueOf(record.getQuantity())));
        record.setStatus("PENDING");
        record.setBookingDate(LocalDateTime.now());

        bookingRecordMapper.insert(record);

        if ("HOTEL".equals(dto.getBookingType())) {
            Hotel hotel = hotelMapper.selectById(dto.getTargetId());
            if (hotel != null && hotel.getAvailableRooms() > 0) {
                hotel.setAvailableRooms(hotel.getAvailableRooms() - 1);
                hotelMapper.updateById(hotel);
            }
        }
    }

    private ItineraryVO convertToVO(Itinerary itinerary, boolean loadItems) {
        ItineraryVO vo = new ItineraryVO();
        vo.setId(itinerary.getId());
        vo.setTitle(itinerary.getTitle());
        vo.setDepartureCity(itinerary.getDepartureCity());
        vo.setDestinationCity(itinerary.getDestinationCity());
        vo.setStartDate(itinerary.getStartDate());
        vo.setEndDate(itinerary.getEndDate());
        vo.setDays(itinerary.getDays());
        vo.setBudget(itinerary.getBudget());
        vo.setInterests(itinerary.getInterests());
        vo.setTotalCost(itinerary.getTotalCost());
        vo.setStatus(itinerary.getStatus());
        vo.setIsArchived(itinerary.getIsArchived());
        vo.setCreatedAt(itinerary.getCreatedAt());

        if (loadItems) {
            List<ItineraryItem> items = itineraryItemMapper.selectList(
                    new LambdaQueryWrapper<ItineraryItem>()
                            .eq(ItineraryItem::getItineraryId, itinerary.getId())
                            .orderByAsc(ItineraryItem::getDayNumber, ItineraryItem::getOrderNum)
            );
            vo.setItems(items.stream().map(this::convertToItemVO).collect(Collectors.toList()));
        }

        return vo;
    }

    private ItineraryItemVO convertToItemVO(ItineraryItem item) {
        ItineraryItemVO vo = new ItineraryItemVO();
        vo.setId(item.getId());
        vo.setDayNumber(item.getDayNumber());
        vo.setItemType(item.getItemType());
        vo.setItemId(item.getItemId());
        vo.setItemName(item.getItemName());
        vo.setItemDesc(item.getItemDesc());
        vo.setItemPrice(item.getItemPrice());
        vo.setStartTime(item.getStartTime());
        vo.setEndTime(item.getEndTime());
        vo.setOrderNum(item.getOrderNum());
        return vo;
    }
}
