package com.travel.backtravel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.BookingCreateDTO;
import com.travel.backtravel.dto.ItineraryCreateDTO;
import com.travel.backtravel.dto.ItineraryItemDTO;
import com.travel.backtravel.vo.ItineraryVO;

public interface ItineraryService {

    ItineraryVO createItinerary(Long userId, ItineraryCreateDTO dto);

    ItineraryVO getItineraryById(Long userId, Long itineraryId);

    Page<ItineraryVO> getUserItineraries(Long userId, Integer pageNum, Integer pageSize);

    ItineraryVO updateItinerary(Long userId, Long itineraryId, ItineraryCreateDTO dto);

    void deleteItinerary(Long userId, Long itineraryId);

    ItineraryVO addItem(Long userId, Long itineraryId, ItineraryItemDTO dto);

    ItineraryVO updateItem(Long userId, Long itineraryId, Long itemId, ItineraryItemDTO dto);

    ItineraryVO removeItem(Long userId, Long itineraryId, Long itemId);

    void archiveItinerary(Long userId, Long itineraryId);

    String exportToPdf(Long userId, Long itineraryId);

    void bookItem(Long userId, BookingCreateDTO dto);
}
