package com.travel.backtravel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.Feedback;
import com.travel.backtravel.mapper.FeedbackMapper;
import com.travel.backtravel.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;

    @Override
    public void submit(Feedback feedback) {
        feedback.setStatus(0);
        feedback.setIsDeleted(0);
        feedback.setCreateTime(LocalDateTime.now());
        feedbackMapper.insert(feedback);
    }

    @Override
    public Page<Feedback> list(Integer status, Integer pageNum, Integer pageSize) {
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.eq(Feedback::getIsDeleted, 0);
        wrapper.orderByDesc(Feedback::getCreateTime);

        return feedbackMapper.selectPage(page, wrapper);
    }

    @Override
    public void updateStatus(Long feedbackId, Integer status) {
        LambdaUpdateWrapper<Feedback> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Feedback::getFeedbackId, feedbackId)
                .set(Feedback::getStatus, status)
                .set(Feedback::getUpdateTime, LocalDateTime.now());
        feedbackMapper.update(null, wrapper);
    }
}
