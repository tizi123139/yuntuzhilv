package com.travel.backtravel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.UserLoginDTO;
import com.travel.backtravel.dto.UserRegisterDTO;
import com.travel.backtravel.dto.UserUpdateDTO;
import com.travel.backtravel.service.UserService;
import com.travel.backtravel.util.ResultUtil;
import com.travel.backtravel.vo.LoginVO;
import com.travel.backtravel.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResultUtil<UserVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        return ResultUtil.success(userService.register(dto));
    }

    @PostMapping("/login")
    public ResultUtil<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        return ResultUtil.success(userService.login(dto));
    }

    @PostMapping("/logout")
    public ResultUtil<Void> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            userService.logout(token.substring(7));
        }
        return ResultUtil.success();
    }

    @GetMapping("/profile")
    public ResultUtil<UserVO> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(userService.getCurrentUser(userId));
    }

    @PutMapping("/profile")
    public ResultUtil<UserVO> updateProfile(@RequestBody UserUpdateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return ResultUtil.success(userService.updateUser(userId, dto));
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Page<UserVO>> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResultUtil.success(userService.getUsers(username, role, status, pageNum, pageSize));
    }

    @PutMapping("/admin/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultUtil<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return ResultUtil.success();
    }
}
