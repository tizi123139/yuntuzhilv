package com.travel.backtravel.service;

import com.travel.backtravel.vo.StatVO;

import java.util.List;
import java.util.Map;

public interface StatService {

    StatVO getStatistics();

    List<Map<String, Object>> getHotAttractions();

    List<Map<String, Object>> getHotDestinations();

    List<Map<String, Object>> getHotelSelectionRatio(String city);

    List<Map<String, Object>> getAttractionSelectionRatio(String city);

    Map<String, Object> getCityTrend();
}
