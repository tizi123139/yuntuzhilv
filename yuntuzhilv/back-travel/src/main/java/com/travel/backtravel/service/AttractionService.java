package com.travel.backtravel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.Attraction;

public interface AttractionService {

    Page<Attraction> list(String city, String type, Integer pageNum, Integer pageSize);

    Attraction getDetail(String id);

    Attraction create(Attraction attraction);

    Attraction update(Attraction attraction);

    void delete(String id);
}
