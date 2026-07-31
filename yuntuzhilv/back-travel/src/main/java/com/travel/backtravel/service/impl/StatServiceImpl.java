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
    private final HotelMapper hotelMapper;
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

    @Override
    public List<Map<String, Object>> getHotDestinations() {
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
                    map.put("name", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .toList();
    }

    @Override
    public List<Map<String, Object>> getHotAttractions() {
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
                    map.put("count", attraction.getViewCount());
                    return map;
                })
                .toList();
    }

    @Override
    public List<Map<String, Object>> getHotelSelectionRatio(String city) {
        // 查询指定城市的所有酒店
        List<Hotel> hotels = hotelMapper.selectList(
                new LambdaQueryWrapper<Hotel>().eq(Hotel::getCity, city)
        );
        if (hotels.isEmpty()) {
            return new ArrayList<>();
        }

        // 按星级分组统计酒店数量
        Map<String, Long> starCount = new HashMap<>();
        for (Hotel hotel : hotels) {
            String starLabel = (hotel.getStar() != null ? hotel.getStar() : "未分类") + "星酒店";
            starCount.merge(starLabel, 1L, Long::sum);
        }

        long total = starCount.values().stream().mapToLong(Long::longValue).sum();
        return starCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("value", Math.round(entry.getValue() * 100.0 / total));
                    return map;
                })
                .toList();
    }

    @Override
    public List<Map<String, Object>> getAttractionSelectionRatio(String city) {
        // 查询指定城市的所有景点
        List<Attraction> attractions = attractionMapper.selectList(
                new LambdaQueryWrapper<Attraction>().eq(Attraction::getCity, city)
        );
        if (attractions.isEmpty()) {
            return new ArrayList<>();
        }

        // 按类型分组统计景点数量
        Map<String, Long> typeCount = new HashMap<>();
        for (Attraction attraction : attractions) {
            String type = attraction.getType() != null ? attraction.getType() : "其他";
            typeCount.merge(type, 1L, Long::sum);
        }

        long total = typeCount.values().stream().mapToLong(Long::longValue).sum();
        return typeCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("value", Math.round(entry.getValue() * 100.0 / total));
                    return map;
                })
                .toList();
    }

    @Override
    public Map<String, Object> getCityTrend() {
        List<Itinerary> itineraries = itineraryMapper.selectList(null);

        // 按目的地和月份统计访问量
        Map<String, Map<Integer, Long>> cityMonthCount = new HashMap<>();
        for (Itinerary itinerary : itineraries) {
            if (itinerary.getStartDate() == null || itinerary.getDestination() == null) continue;
            String city = itinerary.getDestination();
            int month = itinerary.getStartDate().getMonthValue();
            cityMonthCount.computeIfAbsent(city, k -> new HashMap<>())
                    .merge(month, 1L, Long::sum);
        }

        // 取前5个热门城市
        List<String> topCities = cityMonthCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(
                        b.getValue().values().stream().mapToLong(Long::longValue).sum(),
                        a.getValue().values().stream().mapToLong(Long::longValue).sum()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        // 构建月份标签
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(i + "月");
        }

        // 构建城市数据
        List<Map<String, Object>> cities = new ArrayList<>();
        for (String cityName : topCities) {
            Map<Integer, Long> monthData = cityMonthCount.getOrDefault(cityName, new HashMap<>());
            List<Long> data = new ArrayList<>();
            for (int m = 1; m <= 12; m++) {
                data.add(monthData.getOrDefault(m, 0L));
            }
            Map<String, Object> cityMap = new HashMap<>();
            cityMap.put("name", cityName);
            cityMap.put("data", data);
            cities.add(cityMap);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("months", months);
        result.put("cities", cities);
        return result;
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
