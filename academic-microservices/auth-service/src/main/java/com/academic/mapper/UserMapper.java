package com.academic.mapper;

import com.academic.common.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND deleted_at IS NULL")
    User findByUsername(@Param("username") String username);

    /**
     * 检查用户名是否已存在
     */
    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username} AND deleted_at IS NULL")
    boolean existsByUsername(@Param("username") String username);
}
