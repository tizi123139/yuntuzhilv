package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.Attraction;
import com.travel.backtravel.exception.BusinessException;
import com.travel.backtravel.mapper.AttractionMapper;
import com.travel.backtravel.service.AttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttractionServiceImpl implements AttractionService {

    private final AttractionMapper attractionMapper;

    @Override
    public Page<Attraction> list(String city, String type, Integer pageNum, Integer pageSize) {
        Page<Attraction> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getIsDeleted, 0);

        if (StringUtils.hasText(city)) {
            wrapper.like(Attraction::getCity, city);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(Attraction::getType, type);
        }

        wrapper.orderByDesc(Attraction::getViewCount);
        return attractionMapper.selectPage(page, wrapper);
    }

    @Override
    public Attraction getDetail(String id) {
        Attraction attraction = attractionMapper.selectById(id);
        if (attraction == null || attraction.getIsDeleted() == 1) {
            throw new BusinessException("景点不存在");
        }
        return attraction;
    }

    @Override
    public Attraction create(Attraction attraction) {
        attraction.setId("A" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        attraction.setViewCount(0L);
        attraction.setIsDeleted(0);
        attraction.setCreateTime(LocalDateTime.now());
        attraction.setUpdateTime(LocalDateTime.now());
        attractionMapper.insert(attraction);
        return attraction;
    }

    @Override
    public Attraction update(Attraction attraction) {
        Attraction existing = attractionMapper.selectById(attraction.getId());
        if (existing == null || existing.getIsDeleted() == 1) {
            throw new BusinessException("景点不存在");
        }
        attraction.setUpdateTime(LocalDateTime.now());
        attractionMapper.updateById(attraction);
        return attractionMapper.selectById(attraction.getId());
    }

    @Override
    public void delete(String id) {
        Attraction existing = attractionMapper.selectById(id);
        if (existing == null || existing.getIsDeleted() == 1) {
            throw new BusinessException("景点不存在");
        }
        existing.setIsDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        attractionMapper.updateById(existing);
    }
}
