package com.travel.backtravel.vo;

import lombok.Data;

/**
 * 用户个人信息与统计摘要 VO（对应前端 getUserInfoApi 响应）
 */
@Data
public class UserInfoVO {

    private Long userId;

    private String username;

    private String avatar;

    /** 会员等级，如 "活跃旅行者" */
    private String level;

    /** 个人简介 */
    private String bio;

    /** 收藏景点数 */
    private Integer favoritesCount;

    /** 行程总数 */
    private Integer tripsCount;

    /** 当前积分 */
    private Integer points;
}
