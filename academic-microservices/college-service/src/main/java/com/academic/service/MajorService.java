package com.academic.service;

import com.academic.common.entity.Major;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 专业服务接口
 */
public interface MajorService {
    /**
     * 根据ID获取专业
     */
    Major getById(Long id);

    /**
     * 根据代码获取专业
     */
    Major getByCode(String code);

    /**
     * 保存专业
     */
    boolean save(Major major);

    /**
     * 更新专业
     */
    boolean updateById(Major major);

    /**
     * 删除专业
     */
    boolean removeById(Long id);

    /**
     * 分页查询专业
     */
    Page<Major> page(Page<Major> page, QueryWrapper<Major> queryWrapper);

    /**
     * 查询专业列表
     */
    List<Major> list(QueryWrapper<Major> queryWrapper);

    /**
     * 根据学院ID查询专业列表
     */
    List<Major> getByCollegeId(Long collegeId);

    /**
     * 统计专业数量
     */
    long count(QueryWrapper<Major> queryWrapper);

    /**
     * 统计专业总数
     */
    long count();
}
