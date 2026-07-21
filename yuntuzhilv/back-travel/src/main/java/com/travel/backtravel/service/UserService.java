package com.travel.backtravel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.dto.UserLoginDTO;
import com.travel.backtravel.dto.UserRegisterDTO;
import com.travel.backtravel.dto.UserUpdateDTO;
import com.travel.backtravel.entity.User;
import com.travel.backtravel.vo.LoginVO;
import com.travel.backtravel.vo.UserVO;

public interface UserService {

    UserVO register(UserRegisterDTO dto);

    LoginVO login(UserLoginDTO dto);

    void logout(String token);

    UserVO getCurrentUser(Long userId);

    UserVO updateUser(Long userId, UserUpdateDTO dto);

    Page<UserVO> getUsers(String username, String role, Integer status, Integer pageNum, Integer pageSize);

    void updateUserStatus(Long userId, Integer status);

    User getUserById(Long userId);
}
