package com.academic.mapper;

import com.academic.common.entity.Major;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 专业Mapper接口
 */
@Mapper
public interface MajorMapper extends BaseMapper<Major> {
}
