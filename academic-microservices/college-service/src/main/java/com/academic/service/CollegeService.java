package com.academic.service;

import com.academic.common.entity.College;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 学院服务接口
 */
public interface CollegeService {
    /**
     * 根据ID获取学院
     */
    College getById(Long id);

    /**
     * 根据代码获取学院
     */
    College getByCode(String code);

    /**
     * 保存学院
     */
    boolean save(College college);

    /**
     * 更新学院
     */
    boolean updateById(College college);

    /**
     * 删除学院
     */
    boolean removeById(Long id);

    /**
     * 分页查询学院
     */
    Page<College> page(Page<College> page, QueryWrapper<College> queryWrapper);

    /**
     * 查询学院列表
     */
    List<College> list(QueryWrapper<College> queryWrapper);

    /**
     * 统计学院数量
     */
    long count(QueryWrapper<College> queryWrapper);

    /**
     * 统计学院总数
     */
    long count();
}
