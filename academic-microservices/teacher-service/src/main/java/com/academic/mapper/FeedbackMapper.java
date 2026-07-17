package com.academic.mapper;

import com.academic.entity.Feedback;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface FeedbackMapper {
    @Select("SELECT * FROM feedback WHERE teacher_id = #{teacherId} ORDER BY create_time DESC")
    List<Feedback> getFeedbacksByTeacherId(Long teacherId);
    
    @Select("SELECT * FROM feedback WHERE teacher_id = #{teacherId} AND category = #{category} ORDER BY create_time DESC")
    List<Feedback> getFeedbacksByTeacherIdAndCategory(Long teacherId, String category);
    
    @Update("UPDATE feedback SET reply = #{reply}, status = 1, reply_time = NOW() WHERE id = #{id}")
    int replyFeedback(Long id, String reply);
    
    @Select("SELECT * FROM feedback WHERE id = #{id}")
    Feedback getFeedbackById(Long id);
}