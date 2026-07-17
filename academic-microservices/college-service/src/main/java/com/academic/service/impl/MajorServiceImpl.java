package com.academic.service.impl;

import com.academic.common.entity.Major;
import com.academic.mapper.MajorMapper;
import com.academic.service.MajorService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 专业服务实现类
 */
@Slf4j
@Service
public class MajorServiceImpl implements MajorService {

    private final MajorMapper majorMapper;

    public MajorServiceImpl(MajorMapper majorMapper) {
        this.majorMapper = majorMapper;
    }

    @Override
    public Major getById(Long id) {
        try {
            log.info("Get major by id: {}", id);
            return majorMapper.selectById(id);
        } catch (Exception e) {
            log.error("Get major by id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Major getByCode(String code) {
        try {
            log.info("Get major by code: {}", code);
            QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", code);
            return majorMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            log.error("Get major by code failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean save(Major major) {
        try {
            log.info("Save major: {}", major.getName());
            return majorMapper.insert(major) > 0;
        } catch (Exception e) {
            log.error("Save major failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateById(Major major) {
        try {
            log.info("Update major: {}", major.getId());
            return majorMapper.updateById(major) > 0;
        } catch (Exception e) {
            log.error("Update major failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean removeById(Long id) {
        try {
            log.info("Remove major: {}", id);
            return majorMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("Remove major failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<Major> page(Page<Major> page, QueryWrapper<Major> queryWrapper) {
        try {
            log.info("Page query majors");
            return majorMapper.selectPage(page, queryWrapper);
        } catch (Exception e) {
            log.error("Page query majors failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Major> list(QueryWrapper<Major> queryWrapper) {
        try {
            log.info("List majors");
            return majorMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("List majors failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Major> getByCollegeId(Long collegeId) {
        try {
            log.info("Get majors by college id: {}", collegeId);
            QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("college_id", collegeId);
            return majorMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("Get majors by college id failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count(QueryWrapper<Major> queryWrapper) {
        try {
            log.info("Count majors");
            return majorMapper.selectCount(queryWrapper);
        } catch (Exception e) {
            log.error("Count majors failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public long count() {
        try {
            log.info("Count all majors");
            return majorMapper.selectCount(null);
        } catch (Exception e) {
            log.error("Count all majors failed: {}", e.getMessage());
            throw e;
        }
    }
}
