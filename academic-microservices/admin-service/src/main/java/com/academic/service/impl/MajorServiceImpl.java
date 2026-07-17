package com.academic.service.impl;

import com.academic.common.entity.Major;
import com.academic.mapper.MajorMapper;
import com.academic.service.MajorService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {

    private static final Logger log = LoggerFactory.getLogger(MajorServiceImpl.class);

    @Override
    public List<Major> getMajorsByCollegeId(Long collegeId) {
        try {
            QueryWrapper<Major> wrapper = new QueryWrapper<>();
            wrapper.eq("college_id", collegeId);
            return this.list(wrapper);
        } catch (Exception e) { log.error("操作异常", e); return new ArrayList<>(); }
    }

    @Override
    public Major getMajorById(Long id) {
        try { return this.getById(id); } catch (Exception e) { log.error("操作异常", e); return null; }
    }

    @Override
    public boolean createMajor(Major major) {
        try { return this.save(major); } catch (Exception e) { log.error("操作异常", e); return false; }
    }

    @Override
    public boolean updateMajor(Major major) {
        try { return this.updateById(major); } catch (Exception e) { log.error("操作异常", e); return false; }
    }

    @Override
    public boolean deleteMajor(Long id) {
        try { return this.removeById(id); } catch (Exception e) { log.error("操作异常", e); return false; }
    }
}
