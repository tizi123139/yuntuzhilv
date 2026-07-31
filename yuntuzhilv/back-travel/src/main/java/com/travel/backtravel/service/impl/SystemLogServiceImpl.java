package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.SystemLog;
import com.travel.backtravel.entity.User;
import com.travel.backtravel.mapper.SystemLogMapper;
import com.travel.backtravel.mapper.UserMapper;
import com.travel.backtravel.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogMapper systemLogMapper;
    private final UserMapper userMapper;

    @Override
    public void saveLog(SystemLog log) {
        systemLogMapper.insert(log);
    }

    @Override
    public Page<SystemLog> getLogs(String operatorType, String module, Integer pageNum, Integer pageSize) {
        Page<SystemLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(operatorType)) {
            wrapper.eq(SystemLog::getOperatorType, operatorType);
        }
        if (StringUtils.hasText(module)) {
            wrapper.eq(SystemLog::getModule, module);
        }

        wrapper.orderByDesc(SystemLog::getCreateTime);
        Page<SystemLog> result = systemLogMapper.selectPage(page, wrapper);

        // 填充操作人用户名
        List<SystemLog> records = result.getRecords();
        if (!records.isEmpty()) {
            List<Long> operatorIds = records.stream()
                    .map(SystemLog::getOperatorId)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());

            if (!operatorIds.isEmpty()) {
                List<User> users = userMapper.selectBatchIds(operatorIds);
                Map<Long, String> userNameMap = new HashMap<>();
                for (User u : users) {
                    userNameMap.put(u.getUserId(), u.getUsername());
                }
                for (SystemLog log : records) {
                    String name = userNameMap.get(log.getOperatorId());
                    log.setOperator(name != null ? name : "未知用户");
                }
            } else {
                for (SystemLog log : records) {
                    log.setOperator("系统");
                }
            }
        }

        return result;
    }
}
