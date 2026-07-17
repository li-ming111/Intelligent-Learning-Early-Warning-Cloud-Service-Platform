package com.academic.mapper;

import com.academic.entity.SubscriptionPreference;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SubscriptionPreferenceMapper extends BaseMapper<SubscriptionPreference> {

    @Select("SELECT * FROM subscription_preferences WHERE student_id = #{studentId}")
    SubscriptionPreference selectByStudentId(@Param("studentId") Long studentId);

    @Insert("INSERT INTO subscription_preferences (student_id, subscribe_warnings, subscribe_low, subscribe_medium, subscribe_high, subscribe_assistance, subscribe_system, push_email, push_sms, push_app, created_at, updated_at) " +
            "VALUES (#{studentId}, 1, 0, 1, 1, 1, 1, 1, 0, 1, NOW(), NOW())")
    int insertDefault(@Param("studentId") Long studentId);

    @Update("UPDATE subscription_preferences SET ${column} = #{value}, updated_at = NOW() WHERE student_id = #{studentId}")
    int updateField(@Param("studentId") Long studentId, @Param("column") String column, @Param("value") Integer value);
}