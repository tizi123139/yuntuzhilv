package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.HotelDTO;
import com.travel.backtravel.entity.Hotel;
import com.travel.backtravel.exception.BusinessException;
import com.travel.backtravel.mapper.HotelMapper;
import com.travel.backtravel.service.HotelService;
import com.travel.backtravel.vo.HotelVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelMapper hotelMapper;

    @Override
    public Page<HotelVO> list(Integer star, BigDecimal maxPrice, String city, Integer pageNum, Integer pageSize) {
        Page<Hotel> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getIsDeleted, 0);

        if (star != null) {
            wrapper.eq(Hotel::getStar, star);
        }
        if (maxPrice != null) {
            wrapper.le(Hotel::getPrice, maxPrice);
        }
        if (StringUtils.hasText(city)) {
            wrapper.like(Hotel::getCity, city);
        }

        wrapper.orderByDesc(Hotel::getStar);
        Page<Hotel> resultPage = hotelMapper.selectPage(page, wrapper);

        Page<HotelVO> voPage = new Page<>();
        voPage.setRecords(resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        voPage.setTotal(resultPage.getTotal());
        voPage.setSize(resultPage.getSize());
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setPages(resultPage.getPages());
        return voPage;
    }

    @Override
    public HotelVO getDetail(String id) {
        Hotel hotel = hotelMapper.selectById(id);
        if (hotel == null || hotel.getIsDeleted() == 1) {
            throw new BusinessException("酒店不存在");
        }
        return convertToVO(hotel);
    }

    @Override
    public HotelVO create(HotelDTO dto) {
        Hotel hotel = new Hotel();
        BeanUtils.copyProperties(dto, hotel);
        hotel.setId("H" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        if (hotel.getRemain() == null) {
            hotel.setRemain(0);
        }
        hotel.setIsDeleted(0);
        hotel.setCreateTime(LocalDateTime.now());
        hotel.setUpdateTime(LocalDateTime.now());
        hotelMapper.insert(hotel);
        return convertToVO(hotel);
    }

    @Override
    public HotelVO update(HotelDTO dto) {
        Hotel existing = hotelMapper.selectById(dto.getId());
        if (existing == null || existing.getIsDeleted() == 1) {
            throw new BusinessException("酒店不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        existing.setUpdateTime(LocalDateTime.now());
        hotelMapper.updateById(existing);
        return convertToVO(hotelMapper.selectById(dto.getId()));
    }

    @Override
    public void delete(String id) {
        Hotel existing = hotelMapper.selectById(id);
        if (existing == null || existing.getIsDeleted() == 1) {
            throw new BusinessException("酒店不存在");
        }
        existing.setIsDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        hotelMapper.updateById(existing);
    }

    private HotelVO convertToVO(Hotel hotel) {
        HotelVO vo = new HotelVO();
        BeanUtils.copyProperties(hotel, vo);
        return vo;
    }
}
