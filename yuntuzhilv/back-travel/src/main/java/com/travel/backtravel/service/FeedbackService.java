package com.travel.backtravel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.backtravel.entity.Feedback;

public interface FeedbackService {

    void submit(Feedback feedback);

    Page<Feedback> list(Integer status, Integer pageNum, Integer pageSize);

    void updateStatus(Long feedbackId, Integer status);
}
