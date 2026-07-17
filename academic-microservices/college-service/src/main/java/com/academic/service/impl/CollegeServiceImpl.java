package com.academic.service.impl;

import com.academic.common.entity.College;
import com.academic.mapper.CollegeMapper;
import com.academic.service.CollegeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学院服务实现类
 */
@Slf4j
@Service
public class CollegeServiceImpl implements CollegeService {

    private final CollegeMapper collegeMapper;

    public CollegeServiceImpl(CollegeMapper collegeMapper) {
        this.collegeMapper = collegeMapper;
    }

    @Override
    public College getById(Long id) {
        try {
            log.info("Get college by id: {}", id);
            return collegeMapper.selectById(id);
        } catch (Exception e) {
            log.error("Get college by id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public College getByCode(String code) {
        try {
            log.info("Get college by code: {}", code);
            QueryWrapper<College> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", code);
            return collegeMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("Get college by code failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean save(College college) {
        try {
            log.info("Save college: {}", college.getName());
            return collegeMapper.insert(college) > 0;
        } catch (Exception e) {
            log.error("Save college failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateById(College college) {
        try {
            log.info("Update college: {}", college.getId());
            return collegeMapper.updateById(college) > 0;
        } catch (Exception e) {
            log.error("Update college failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean removeById(Long id) {
        try {
            log.info("Remove college: {}", id);
            return collegeMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("Remove college failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<College> page(Page<College> page, QueryWrapper<College> queryWrapper) {
        try {
            log.info("Page query colleges");
            return collegeMapper.selectPage(page, queryWrapper);
        } catch (Exception e) {
            log.error("Page query colleges failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<College> list(QueryWrapper<College> queryWrapper) {
        try {
            log.info("List colleges");
            return collegeMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("List colleges failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count(QueryWrapper<College> queryWrapper) {
        try {
            log.info("Count colleges");
            return collegeMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("Count colleges failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count() {
        try {
            log.info("Count all colleges");
            return collegeMapper.selectCount(null);
        } catch (Exception e) {
            log.error("Count all colleges failed: {}", e.getMessage());
            throw e;
        }
    }
}
