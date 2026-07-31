package com.travel.backtravel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.SystemLog;

public interface SystemLogService {

    void saveLog(SystemLog log);

    Page<SystemLog> getLogs(String operatorType, String module, Integer pageNum, Integer pageSize);
}
