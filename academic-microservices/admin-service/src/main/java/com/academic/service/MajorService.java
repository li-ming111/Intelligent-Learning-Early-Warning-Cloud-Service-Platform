package com.academic.service;

import com.academic.common.entity.Major;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 专业服务接口
 */
public interface MajorService extends IService<Major> {

    /**
     * 根据学院ID获取专业列表
     * @param collegeId 学院ID
     * @return 专业列表
     */
    List<Major> getMajorsByCollegeId(Long collegeId);

    /**
     * 根据ID获取专业
     * @param id 专业ID
     * @return 专业信息
     */
    Major getMajorById(Long id);

    /**
     * 创建专业
     * @param major 专业信息
     * @return 是否成功
     */
    boolean createMajor(Major major);

    /**
     * 更新专业
     * @param major 专业信息
     * @return 是否成功
     */
    boolean updateMajor(Major major);

    /**
     * 删除专业
     * @param id 专业ID
     * @return 是否成功
     */
    boolean deleteMajor(Long id);
}
