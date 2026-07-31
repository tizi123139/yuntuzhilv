package com.travel.backtravel.service;

import com.travel.backtravel.dto.TrafficDTO;
import com.travel.backtravel.vo.TrafficVO;

import java.util.List;

public interface TrafficService {

    List<TrafficVO> findByRoute(String fromCity, String toCity);

    List<TrafficVO> list(Integer pageNum, Integer pageSize);

    long count();

    TrafficVO create(TrafficDTO dto);

    void delete(String id);
}
