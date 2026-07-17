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
}