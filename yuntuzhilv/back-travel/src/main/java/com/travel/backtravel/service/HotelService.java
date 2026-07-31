package com.travel.backtravel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.HotelDTO;
import com.travel.backtravel.vo.HotelVO;

import java.math.BigDecimal;

public interface HotelService {

    Page<HotelVO> list(Integer star, BigDecimal maxPrice, String city, Integer pageNum, Integer pageSize);

    HotelVO getDetail(String id);

    HotelVO create(HotelDTO dto);

    HotelVO update(HotelDTO dto);

    void delete(String id);
}
