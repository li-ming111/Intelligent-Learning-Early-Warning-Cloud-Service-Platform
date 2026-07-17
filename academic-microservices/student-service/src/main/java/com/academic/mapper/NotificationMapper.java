package com.academic.mapper;

import com.academic.entity.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    @Select("SELECT * FROM notifications WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY created_at DESC")
    List<Notification> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} AND is_read = 0 AND is_deleted = 0")
    int countUnreadByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM notifications WHERE user_id = #{userId} AND is_read = 0 AND is_deleted = 0 ORDER BY created_at DESC")
    List<Notification> selectUnreadByUserId(@Param("userId") Long userId);

    @Update("UPDATE notifications SET is_read = 1, updated_at = NOW() WHERE id = #{id}")
    int markAsRead(@Param("id") Long id);

    @Update("UPDATE notifications SET is_read = 1, updated_at = NOW() WHERE user_id = #{userId} AND id IN (${ids})")
    int markBatchRead(@Param("userId") Long userId, @Param("ids") String ids);

    @Update("UPDATE notifications SET is_deleted = 1, updated_at = NOW() WHERE id = #{id}")
    int softDelete(@Param("id") Long id);

    @Update("UPDATE notifications SET is_deleted = 1, updated_at = NOW() WHERE user_id = #{userId} AND is_read = 1")
    int clearReadNotifications(@Param("userId") Long userId);
}