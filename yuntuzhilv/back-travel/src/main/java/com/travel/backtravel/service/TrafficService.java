package com.travel.backtravel.service;

import com.travel.backtravel.entity.Traffic;

import java.util.List;

public interface TrafficService {

    List<Traffic> findByRoute(String fromCity, String toCity);

    List<Traffic> list(Integer pageNum, Integer pageSize);

    long count();

    Traffic create(Traffic traffic);

    void delete(String id);
}
