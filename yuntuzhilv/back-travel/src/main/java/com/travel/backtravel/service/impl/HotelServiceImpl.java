package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.Hotel;
import com.travel.backtravel.exception.BusinessException;
import com.travel.backtravel.mapper.HotelMapper;
import com.travel.backtravel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelMapper hotelMapper;

    @Override
    public Page<Hotel> list(Integer star, BigDecimal maxPrice, String city, Integer pageNum, Integer pageSize) {
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
        return hotelMapper.selectPage(page, wrapper);
    }

    @Override
    public Hotel getDetail(String id) {
        Hotel hotel = hotelMapper.selectById(id);
        if (hotel == null || hotel.getIsDeleted() == 1) {
            throw new BusinessException("酒店不存在");
        }
        return hotel;
    }

    @Override
    public Hotel create(Hotel hotel) {
        hotel.setId("H" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        if (hotel.getRemain() == null) {
            hotel.setRemain(0);
        }
        hotel.setIsDeleted(0);
        hotel.setCreateTime(LocalDateTime.now());
        hotel.setUpdateTime(LocalDateTime.now());
        hotelMapper.insert(hotel);
        return hotel;
    }

    @Override
    public Hotel update(Hotel hotel) {
        Hotel existing = hotelMapper.selectById(hotel.getId());
        if (existing == null || existing.getIsDeleted() == 1) {
            throw new BusinessException("酒店不存在");
        }
        hotel.setUpdateTime(LocalDateTime.now());
        hotelMapper.updateById(hotel);
        return hotelMapper.selectById(hotel.getId());
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
}
