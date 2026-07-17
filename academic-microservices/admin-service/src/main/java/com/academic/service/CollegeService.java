package com.academic.service;

import com.academic.common.entity.College;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 学院服务接口
 */
public interface CollegeService extends IService<College> {

    /**
     * 获取所有学院
     * @return 学院列表
     */
    List<College> getAllColleges();

    /**
     * 根据ID获取学院
     * @param id 学院ID
     * @return 学院信息
     */
    College getCollegeById(Long id);

    /**
     * 创建学院
     * @param college 学院信息
     * @return 是否成功
     */
    boolean createCollege(College college);

    /**
     * 更新学院
     * @param college 学院信息
     * @return 是否成功
     */
    boolean updateCollege(College college);

    /**
     * 删除学院
     * @param id 学院ID
     * @return 是否成功
     */
    boolean deleteCollege(Long id);
}
