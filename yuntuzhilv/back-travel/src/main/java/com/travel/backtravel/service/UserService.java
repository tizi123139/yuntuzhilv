package com.travel.backtravel.service;

import com.travel.backtravel.dto.UserLoginDTO;
import com.travel.backtravel.dto.UserRegisterDTO;
import com.travel.backtravel.dto.UserUpdateDTO;
import com.travel.backtravel.entity.User;
import com.travel.backtravel.vo.LoginVO;
import com.travel.backtravel.vo.PageVO;
import com.travel.backtravel.vo.UserInfoVO;
import com.travel.backtravel.vo.UserVO;

public interface UserService {

    UserVO register(UserRegisterDTO dto);

    LoginVO login(UserLoginDTO dto);

    void logout(String token);

    /** 获取当前登录用户信息（含统计摘要） */
    UserInfoVO getUserInfo(Long userId);

    /** 更新用户基础信息 */
    UserVO updateUser(Long userId, UserUpdateDTO dto);

    /** 单独更新旅游偏好 */
    UserVO updatePreferences(Long userId, String preferences);

    /** 发送短信验证码 */
    void sendCode(String phone);

    /** 管理员分页查询用户列表 */
    PageVO<UserVO> getUsers(String username, Integer pageNum, Integer pageSize);

    /** 管理员修改用户状态 */
    void updateUserStatus(Long userId, Integer status);

    User getUserById(Long userId);
}
