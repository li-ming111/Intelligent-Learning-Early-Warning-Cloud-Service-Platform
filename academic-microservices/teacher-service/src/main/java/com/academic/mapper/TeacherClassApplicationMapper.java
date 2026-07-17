package com.academic.mapper;

import com.academic.entity.TeacherClassApplication;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface TeacherClassApplicationMapper {
    @Insert("INSERT INTO teacher_class_application(teacher_id, class_id, reason, status, create_time, class_name, teacher_name, teacher_username, application_type) VALUES(#{teacherId}, #{classId}, #{reason}, 0, NOW(), #{className}, #{teacherName}, #{teacherUsername}, #{applicationType})")
    int insertApplication(TeacherClassApplication application);
    
    @Select("SELECT * FROM teacher_class_application WHERE teacher_id = #{teacherId} ORDER BY create_time DESC")
    List<TeacherClassApplication> getApplicationsByTeacherId(Long teacherId);
    
    @Select("SELECT * FROM teacher_class_application ORDER BY create_time DESC")
    List<TeacherClassApplication> getAllApplications();
    
    @Select("SELECT * FROM teacher_class_application WHERE id = #{id}")
    TeacherClassApplication getApplicationById(Long id);
    
    @Select("SELECT * FROM teacher_class_application WHERE status = #{status} ORDER BY create_time DESC")
    List<TeacherClassApplication> getApplicationsByStatus(Integer status);
    
    @Update("UPDATE teacher_class_application SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(Long id, Integer status);
    
    @Select("SELECT * FROM teacher_class_application WHERE teacher_id = #{teacherId} AND class_id = #{classId} AND status IN (0, 1)")
    List<TeacherClassApplication> getApplicationsByTeacherAndClass(Long teacherId, Long classId);
    
    @Update("UPDATE teacher_class_application SET teacher_id = #{teacherId}, class_id = #{classId}, reason = #{reason}, status = #{status}, class_name = #{className}, teacher_name = #{teacherName}, teacher_username = #{teacherUsername}, update_time = NOW() WHERE id = #{id}")
    int updateById(TeacherClassApplication application);
}