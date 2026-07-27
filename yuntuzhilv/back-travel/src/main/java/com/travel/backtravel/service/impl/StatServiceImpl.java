package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.backtravel.entity.*;
import com.travel.backtravel.mapper.*;
import com.travel.backtravel.service.StatService;
import com.travel.backtravel.util.RedisUtil;
import com.travel.backtravel.vo.StatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final UserMapper userMapper;
    private final ItineraryMapper itineraryMapper;
    private final BookingOrderMapper bookingOrderMapper;
    private final AttractionMapper attractionMapper;
    private final RedisUtil redisUtil;

    private static final String STAT_CACHE_KEY = "statistics_cache";

    @Override
    public StatVO getStatistics() {
        Object cached = redisUtil.get(STAT_CACHE_KEY);
        if (cached != null) {
            return (StatVO) cached;
        }

        StatVO statVO = new StatVO();

        statVO.setTotalUsers(userMapper.selectCount(null));
        statVO.setTotalItineraries(itineraryMapper.selectCount(null));
        statVO.setTotalBookings(bookingOrderMapper.selectCount(null));

        List<BookingOrder> bookings = bookingOrderMapper.selectList(null);
        BigDecimal totalRevenue = bookings.stream()
                .map(BookingOrder::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statVO.setTotalRevenue(totalRevenue);

        statVO.setHotDestinations(getHotDestinations());
        statVO.setHotAttractions(getHotAttractions());
        statVO.setUserPreferences(getUserPreferences());

        redisUtil.set(STAT_CACHE_KEY, statVO, 30, java.util.concurrent.TimeUnit.MINUTES);
        return statVO;
    }

    private List<Map<String, Object>> getHotDestinations() {
        List<Itinerary> itineraries = itineraryMapper.selectList(null);
        Map<String, Long> destinationCount = new HashMap<>();

        for (Itinerary itinerary : itineraries) {
            String city = itinerary.getDestination();
            destinationCount.merge(city, 1L, Long::sum);
        }

        return destinationCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("city", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .toList();
    }

    private List<Map<String, Object>> getHotAttractions() {
        List<Attraction> attractions = attractionMapper.selectList(
                new LambdaQueryWrapper<Attraction>()
                        .orderByDesc(Attraction::getViewCount)
                        .last("LIMIT 10")
        );

        return attractions.stream()
                .map(attraction -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", attraction.getId());
                    map.put("name", attraction.getName());
                    map.put("city", attraction.getCity());
                    map.put("viewCount", attraction.getViewCount());
                    map.put("rating", attraction.getRating());
                    return map;
                })
                .toList();
    }

    private List<Map<String, Object>> getUserPreferences() {
        List<User> users = userMapper.selectList(null);
        Map<String, Long> preferenceCount = new HashMap<>();

        for (User user : users) {
            if (user.getPreferences() != null && !user.getPreferences().isEmpty()) {
                String[] prefs = user.getPreferences().split(",");
                for (String pref : prefs) {
                    preferenceCount.merge(pref.trim(), 1L, Long::sum);
                }
            }
        }

        return preferenceCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("preference", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .toList();
    }
}
