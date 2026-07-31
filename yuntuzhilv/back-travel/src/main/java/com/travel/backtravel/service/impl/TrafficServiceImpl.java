package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.TrafficDTO;
import com.travel.backtravel.entity.Traffic;
import com.travel.backtravel.exception.BusinessException;
import com.travel.backtravel.mapper.TrafficMapper;
import com.travel.backtravel.service.TrafficService;
import com.travel.backtravel.vo.TrafficVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrafficServiceImpl implements TrafficService {

    private final TrafficMapper trafficMapper;

    @Override
    public List<TrafficVO> findByRoute(String fromCity, String toCity) {
        LambdaQueryWrapper<Traffic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Traffic::getIsDeleted, 0)
                .eq(Traffic::getFromCity, fromCity)
                .eq(Traffic::getToCity, toCity);
        return trafficMapper.selectList(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrafficVO> list(Integer pageNum, Integer pageSize) {
        Page<Traffic> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Traffic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Traffic::getIsDeleted, 0);
        Page<Traffic> result = trafficMapper.selectPage(page, wrapper);
        return result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        LambdaQueryWrapper<Traffic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Traffic::getIsDeleted, 0);
        return trafficMapper.selectCount(wrapper);
    }

    @Override
    public TrafficVO create(TrafficDTO dto) {
        Traffic traffic = new Traffic();
        BeanUtils.copyProperties(dto, traffic);
        traffic.setId("T" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        traffic.setIsDeleted(0);
        traffic.setCreateTime(LocalDateTime.now());
        traffic.setUpdateTime(LocalDateTime.now());
        trafficMapper.insert(traffic);
        return convertToVO(traffic);
    }

    @Override
    public void delete(String id) {
        Traffic existing = trafficMapper.selectById(id);
        if (existing == null || existing.getIsDeleted() == 1) {
            throw new BusinessException("交通记录不存在");
        }
        existing.setIsDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        trafficMapper.updateById(existing);
    }

    private TrafficVO convertToVO(Traffic traffic) {
        TrafficVO vo = new TrafficVO();
        BeanUtils.copyProperties(traffic, vo);
        return vo;
    }
}
