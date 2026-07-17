package com.academic.service;

import com.academic.common.entity.CounselorProfile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 辅导员服务接口
 */
public interface CounselorService extends IService<CounselorProfile> {
    /**
     * 根据用户ID获取辅导员信息
     * @param userId 用户ID
     * @return 辅导员信息
     */
    CounselorProfile getByUserId(Long userId);

    /**
     * 根据辅导员ID获取辅导员信息
     * @param counselorId 辅导员ID
     * @return 辅导员信息
     */
    CounselorProfile getByCounselorId(String counselorId);

    /**
     * 注册辅导员
     * @param counselorProfile 辅导员信息
     * @return 是否注册成功
     */
    boolean register(CounselorProfile counselorProfile);

    /**
     * 更新辅导员信息
     * @param counselorProfile 辅导员信息
     * @return 是否更新成功
     */
    boolean updateProfile(CounselorProfile counselorProfile);

    /**
     * 删除辅导员信息
     * @param id 辅导员ID
     * @return 是否删除成功
     */
    boolean deleteProfile(Long id);
    
    /**
     * 按学院统计辅导员数量
     * @param collegeId 学院ID
     * @return 辅导员数量
     */
    long countByCollege(Long collegeId);
    
    /**
     * 获取所有辅导员列表（支持按学院筛选）
     * @param collegeId 学院ID（可选）
     * @return 辅导员列表
     */
    java.util.List<CounselorProfile> getAllCounselors(Long collegeId);
}
