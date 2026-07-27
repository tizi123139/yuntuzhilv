package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.UserLoginDTO;
import com.travel.backtravel.dto.UserRegisterDTO;
import com.travel.backtravel.dto.UserUpdateDTO;
import com.travel.backtravel.entity.Itinerary;
import com.travel.backtravel.entity.User;
import com.travel.backtravel.exception.BusinessException;
import com.travel.backtravel.mapper.ItineraryMapper;
import com.travel.backtravel.mapper.UserMapper;
import com.travel.backtravel.service.UserService;
import com.travel.backtravel.util.JwtUtil;
import com.travel.backtravel.util.RedisUtil;
import com.travel.backtravel.vo.LoginVO;
import com.travel.backtravel.vo.PageVO;
import com.travel.backtravel.vo.UserInfoVO;
import com.travel.backtravel.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final ItineraryMapper itineraryMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public UserVO register(UserRegisterDTO dto) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已注册
        if (StringUtils.hasText(dto.getPhone())) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, dto.getPhone());
            if (userMapper.selectCount(phoneWrapper) > 0) {
                throw new BusinessException("该手机号已注册");
            }
        }

        // 检查邮箱是否已注册
        if (StringUtils.hasText(dto.getEmail())) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, dto.getEmail());
            if (userMapper.selectCount(emailWrapper) > 0) {
                throw new BusinessException("该邮箱已注册");
            }
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole("USER");
        user.setStatus(1);

        userMapper.insert(user);
        return convertToVO(user);
    }

    @Override
    public LoginVO login(UserLoginDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getUsername());

        redisUtil.set("refresh_token:" + user.getUserId(), refreshToken, 7, TimeUnit.DAYS);

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setUser(convertToVO(user));

        return loginVO;
    }

    @Override
    public void logout(String token) {
        redisUtil.addToSet("jwt:blacklist", token);
        redisUtil.expire("jwt:blacklist", 24, TimeUnit.HOURS);
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar() != null ? user.getAvatar() : "");
        vo.setBio(user.getRealName() != null ? user.getRealName() : "");

        // 根据行程数量计算会员等级
        int tripsCount = getUserTripsCount(userId);
        vo.setTripsCount(tripsCount);
        vo.setLevel(calculateLevel(tripsCount));

        // 收藏数和积分（暂用默认值，后续可扩展）
        vo.setFavoritesCount(0);
        vo.setPoints(tripsCount * 10);

        return vo;
    }

    @Override
    public UserVO updateUser(Long userId, UserUpdateDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (StringUtils.hasText(dto.getRealName())) {
            user.setRealName(dto.getRealName());
        }
        if (StringUtils.hasText(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (StringUtils.hasText(dto.getPreferences())) {
            user.setPreferences(dto.getPreferences());
        }

        userMapper.updateById(user);
        return convertToVO(user);
    }

    @Override
    public UserVO updatePreferences(Long userId, String preferences) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPreferences(preferences);
        userMapper.updateById(user);
        return convertToVO(user);
    }

    @Override
    public void sendCode(String phone) {
        // 生成6位随机验证码
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));

        // 存入 Redis，5分钟过期
        redisUtil.set("sms_code:" + phone, code, 5, TimeUnit.MINUTES);

        // TODO: 接入真实短信发送服务（如阿里云SMS）
        log.info("【验证码】向 {} 发送验证码: {}", phone, code);
    }

    @Override
    public PageVO<UserVO> getUsers(String username, Integer pageNum, Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }

        wrapper.orderByDesc(User::getCreateTime);
        Page<User> resultPage = userMapper.selectPage(page, wrapper);

        List<UserVO> records = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageVO<>(records, resultPage.getTotal());
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取用户行程总数
     */
    private int getUserTripsCount(Long userId) {
        LambdaQueryWrapper<Itinerary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Itinerary::getUserId, userId);
        return Math.toIntExact(itineraryMapper.selectCount(wrapper));
    }

    /**
     * 根据行程数计算会员等级
     */
    private String calculateLevel(int tripsCount) {
        if (tripsCount >= 50) return "旅行达人";
        if (tripsCount >= 20) return "活跃旅行者";
        if (tripsCount >= 5) return "旅行爱好者";
        return "旅行新手";
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setRealName(user.getRealName());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setPreferences(user.getPreferences());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
