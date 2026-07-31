package com.travel.backtravel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.AttractionDTO;
import com.travel.backtravel.vo.AttractionVO;

public interface AttractionService {

    Page<AttractionVO> list(String city, String type, Integer pageNum, Integer pageSize);

    AttractionVO getDetail(String id);

    AttractionVO create(AttractionDTO dto);

    AttractionVO update(AttractionDTO dto);

    void delete(String id);
}
