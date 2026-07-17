package com.academic.mapper;

import com.academic.common.entity.College;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学院数据访问层
 */
@Mapper
public interface CollegeMapper extends BaseMapper<College> {
}
