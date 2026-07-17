package com.academic.mapper;

import com.academic.entity.CommunicationRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommunicationRecordMapper {
    @Insert("INSERT INTO communication_record(teacher_id, student_id, content, type, create_time, student_name) VALUES(#{teacherId}, #{studentId}, #{content}, #{type}, NOW(), #{studentName})")
    int insertRecord(CommunicationRecord record);
    
    @Select("SELECT * FROM communication_record WHERE teacher_id = #{teacherId} ORDER BY create_time DESC")
    List<CommunicationRecord> getRecordsByTeacherId(Long teacherId);
    
    @Select("SELECT * FROM communication_record WHERE teacher_id = #{teacherId} AND student_id = #{studentId} ORDER BY create_time DESC")
    List<CommunicationRecord> getRecordsByTeacherAndStudent(Long teacherId, Long studentId);
}