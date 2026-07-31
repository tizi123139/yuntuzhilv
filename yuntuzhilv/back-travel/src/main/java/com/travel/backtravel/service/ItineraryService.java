package com.travel.backtravel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.BookingCreateDTO;
import com.travel.backtravel.dto.ItineraryCreateDTO;
import com.travel.backtravel.vo.ItineraryVO;

public interface ItineraryService {

    ItineraryVO createItinerary(Long userId, ItineraryCreateDTO dto);

    ItineraryVO getItineraryById(Long userId, String itineraryId);

    Page<ItineraryVO> getUserItineraries(Long userId, Integer pageNum, Integer pageSize);

    ItineraryVO updateItinerary(Long userId, String itineraryId, ItineraryCreateDTO dto);

    void deleteItinerary(Long userId, String itineraryId);

    void archiveItinerary(Long userId, String itineraryId);

    String exportToPdf(Long userId, String itineraryId);

    void createBooking(Long userId, BookingCreateDTO dto);

    /**
     * 保存 AI 生成的行程（直接存储 dayPlansJson，不走明细表）
     */
    ItineraryVO saveAiItinerary(Long userId, ItineraryVO vo);
}
