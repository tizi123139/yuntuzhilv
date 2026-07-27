package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.SystemLog;
import com.travel.backtravel.mapper.SystemLogMapper;
import com.travel.backtravel.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogMapper systemLogMapper;

    @Override
    public void saveLog(SystemLog log) {
        systemLogMapper.insert(log);
    }

    @Override
    public Page<SystemLog> getLogs(String module, String operation, Integer pageNum, Integer pageSize) {
        Page<SystemLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(module)) {
            wrapper.eq(SystemLog::getModule, module);
        }
        if (StringUtils.hasText(operation)) {
            wrapper.eq(SystemLog::getOperatorType, operation);
        }

        wrapper.orderByDesc(SystemLog::getCreateTime);
        return systemLogMapper.selectPage(page, wrapper);
    }
}
