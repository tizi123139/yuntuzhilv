package com.travel.backtravel.controller;

import com.travel.backtravel.dto.SendCodeDTO;
import com.travel.backtravel.dto.UpdateStatusDTO;
import com.travel.backtravel.dto.UserLoginDTO;
import com.travel.backtravel.dto.UserRegisterDTO;
import com.travel.backtravel.dto.UserUpdateDTO;
import com.travel.backtravel.service.UserService;
import com.travel.backtravel.util.ResultUtil;
import com.travel.backtravel.vo.LoginVO;
import com.travel.backtravel.vo.PageVO;
import com.travel.backtravel.vo.UserInfoVO;
import com.travel.backtravel.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ==================== 普通用户接口 ====================

    /**
     * 用户注册
     * POST /user/register
     */
    @PostMapping("/register")
    public ResultUtil<UserVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        return ResultUtil.success(userService.register(dto));
    }

    /**
     * 用户登录
     * POST /user/login
     */
    @PostMapping("/login")
    public ResultUtil<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        return ResultUtil.success(userService.login(dto));
    }

    /**
     * 用户登出
     * POST /user/logout
     */
    @PostMapping("/logout")
    public ResultUtil<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                   @RequestHeader(value = "token", required = false) String tokenHeader) {
        String token = authHeader != null ? authHeader : tokenHeader;
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token != null) {
            userService.logout(token);
        }
        return ResultUtil.success();
    }

    /**
     * 发送短信验证码（注册用）
     * POST /user/sendCode
     */
    @PostMapping("/sendCode")
    public ResultUtil<Map<String, String>> sendCode(@Valid @RequestBody SendCodeDTO dto) {
        userService.sendCode(dto.getPhone());
        return ResultUtil.success("验证码已发送", Map.of("message", "验证码已发送"));
    }

    /**
     * 获取当前登录用户的个人信息与统计摘要
     * GET /user/getInfo
     */
    @GetMapping("/getInfo")
    public ResultUtil<UserInfoVO> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(userService.getUserInfo(userId));
    }

    /**
     * 更新用户基础信息
     * POST /user/update
     */
    @PostMapping("/update")
    public ResultUtil<UserVO> updateUserInfo(@RequestBody UserUpdateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(userService.updateUser(userId, dto));
    }

    /**
     * 更新旅游偏好
     * POST /user/updatePrefer
     */
    @PostMapping("/updatePrefer")
    public ResultUtil<UserVO> updatePreferences(@RequestBody Map<String, String> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        String preferences = body.get("preferences");
        return ResultUtil.success(userService.updatePreferences(userId, preferences));
    }

    // ==================== 管理员接口 ====================

    /**
     * 分页查询用户列表（管理员）
     * GET /user/list
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<PageVO<UserVO>> getUserList(
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultUtil.success(userService.getUsers(username, pageNum, pageSize));
    }

    /**
     * 修改用户启用/禁用状态（管理员）
     * POST /user/updateStatus
     */
    @PostMapping("/updateStatus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Void> updateUserStatus(@Valid @RequestBody UpdateStatusDTO dto) {
        userService.updateUserStatus(dto.getUserId(), dto.getStatus());
        return ResultUtil.success();
    }
}
