package com.academic.service;

import com.academic.common.entity.TeacherProfile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 教师服务接口
 */
public interface TeacherService extends IService<TeacherProfile> {
    /**
     * 根据用户ID获取教师信息
     * @param userId 用户ID
     * @return 教师信息
     */
    TeacherProfile getByUserId(Long userId);
}