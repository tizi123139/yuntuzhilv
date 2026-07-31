package com.travel.backtravel.controller;

import com.travel.backtravel.annotation.OperationLog;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "用户管理", description = "用户注册、登录、登出、信息管理等接口")
public class UserController {

    private final UserService userService;

    // ==================== 普通用户接口 ====================

    /**
     * 用户注册
     * POST /user/register
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户通过手机号注册账号")
    public ResultUtil<UserVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        return ResultUtil.success(userService.register(dto));
    }

    /**
     * 用户登录
     * POST /user/login
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过账号密码登录系统")
    public ResultUtil<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        return ResultUtil.success(userService.login(dto));
    }

    /**
     * 用户登出
     * POST /user/logout
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户退出登录，清除token")
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
    @Operation(summary = "发送验证码", description = "发送短信验证码用于注册")
    public ResultUtil<Map<String, String>> sendCode(@Valid @RequestBody SendCodeDTO dto) {
        userService.sendCode(dto.getPhone());
        return ResultUtil.success("验证码已发送", Map.of("message", "验证码已发送"));
    }

    /**
     * 获取当前登录用户的个人信息与统计摘要
     * GET /user/getInfo
     */
    @GetMapping("/getInfo")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的个人信息和统计摘要")
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
    @Operation(summary = "更新用户信息", description = "更新用户的基础信息如昵称、头像等")
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
    @Operation(summary = "更新旅游偏好", description = "更新用户的旅游偏好设置")
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
    @Operation(summary = "获取用户列表", description = "管理员分页查询用户列表")
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
    @OperationLog(value = "修改用户状态", module = "用户管理", type = "update")
    @Operation(summary = "修改用户状态", description = "管理员启用或禁用用户账号")
    public ResultUtil<Void> updateUserStatus(@Valid @RequestBody UpdateStatusDTO dto) {
        userService.updateUserStatus(dto.getUserId(), dto.getStatus());
        return ResultUtil.success();
    }
}
