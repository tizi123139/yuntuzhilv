package com.travel.backtravel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.Hotel;

import java.math.BigDecimal;

public interface HotelService {

    Page<Hotel> list(Integer star, BigDecimal maxPrice, String city, Integer pageNum, Integer pageSize);

    Hotel getDetail(String id);

    Hotel create(Hotel hotel);

    Hotel update(Hotel hotel);

    void delete(String id);
}
