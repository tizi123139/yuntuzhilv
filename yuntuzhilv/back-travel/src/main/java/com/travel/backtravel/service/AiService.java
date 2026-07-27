package com.travel.backtravel.service;

import com.travel.backtravel.dto.AiPlanDTO;
import com.travel.backtravel.vo.ItineraryVO;

public interface AiService {

    ItineraryVO generateItinerary(Long userId, AiPlanDTO dto);

    ItineraryVO modifyItinerary(Long userId, String itineraryId, String modifications);
}
