package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.AttractionDTO;
import com.travel.backtravel.entity.Attraction;
import com.travel.backtravel.exception.BusinessException;
import com.travel.backtravel.mapper.AttractionMapper;
import com.travel.backtravel.service.AttractionService;
import com.travel.backtravel.vo.AttractionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttractionServiceImpl implements AttractionService {

    private final AttractionMapper attractionMapper;

    @Override
    public Page<AttractionVO> list(String city, String type, Integer pageNum, Integer pageSize) {
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
        Page<Attraction> resultPage = attractionMapper.selectPage(page, wrapper);

        Page<AttractionVO> voPage = new Page<>();
        voPage.setRecords(resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList()));
        voPage.setTotal(resultPage.getTotal());
        voPage.setSize(resultPage.getSize());
        voPage.setCurrent(resultPage.getCurrent());
        voPage.setPages(resultPage.getPages());
        return voPage;
    }

    @Override
    public AttractionVO getDetail(String id) {
        Attraction attraction = attractionMapper.selectById(id);
        if (attraction == null || attraction.getIsDeleted() == 1) {
            throw new BusinessException("景点不存在");
        }
        return convertToVO(attraction);
    }

    @Override
    public AttractionVO create(AttractionDTO dto) {
        Attraction attraction = new Attraction();
        BeanUtils.copyProperties(dto, attraction);
        attraction.setId("A" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        attraction.setViewCount(0L);
        attraction.setIsDeleted(0);
        attraction.setCreateTime(LocalDateTime.now());
        attraction.setUpdateTime(LocalDateTime.now());
        attractionMapper.insert(attraction);
        return convertToVO(attraction);
    }

    @Override
    public AttractionVO update(AttractionDTO dto) {
        Attraction existing = attractionMapper.selectById(dto.getId());
        if (existing == null || existing.getIsDeleted() == 1) {
            throw new BusinessException("景点不存在");
        }
        BeanUtils.copyProperties(dto, existing);
        existing.setUpdateTime(LocalDateTime.now());
        attractionMapper.updateById(existing);
        return convertToVO(attractionMapper.selectById(dto.getId()));
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

    private AttractionVO convertToVO(Attraction attraction) {
        AttractionVO vo = new AttractionVO();
        BeanUtils.copyProperties(attraction, vo);
        return vo;
    }
}
