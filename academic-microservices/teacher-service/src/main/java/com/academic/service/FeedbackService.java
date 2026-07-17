package com.academic.service;

import com.academic.entity.Feedback;
import com.academic.mapper.FeedbackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackMapper feedbackMapper;
    
    public List<Feedback> getFeedbacks(Long teacherId, String category) {
        if (category != null && !category.isEmpty()) {
            return feedbackMapper.getFeedbacksByTeacherIdAndCategory(teacherId, category);
        }
        return feedbackMapper.getFeedbacksByTeacherId(teacherId);
    }
    
    public void replyFeedback(Long feedbackId, String reply) {
        feedbackMapper.replyFeedback(feedbackId, reply);
    }
    
    public Feedback getFeedbackById(Long id) {
        return feedbackMapper.getFeedbackById(id);
    }
}