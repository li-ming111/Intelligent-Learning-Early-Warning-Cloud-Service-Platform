package com.academic.mapper;

import com.academic.entity.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    @Select("SELECT r.* FROM roles r " +
            "INNER JOIN role_permissions rp ON r.id = rp.role_id " +
            "WHERE rp.permission_id = #{permissionId}")
    List<com.academic.entity.Role> selectRolesByPermissionId(@Param("permissionId") Long permissionId);
}
