package com.academic.mapper;

import com.academic.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("SELECT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN roles r ON rp.role_id = r.id " +
            "WHERE r.code = #{roleCode}")
    List<Permission> selectByRoleCode(@Param("roleCode") String roleCode);
}
