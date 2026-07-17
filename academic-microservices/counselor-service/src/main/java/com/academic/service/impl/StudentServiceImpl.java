package com.academic.service.impl;

import com.academic.common.entity.Student;
import com.academic.mapper.StudentMapper;
import com.academic.mapper.ClassInfoMapper;
import com.academic.service.StudentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 学生服务实现类
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Override
    public List<Student> getByCounselorId(Long counselorId) {
        // 通过 classes.counselor_id 查询辅导员管辖班级的学生
        List<Map<String, Object>> classes = classInfoMapper.getClassesByCounselorIdMap(counselorId);
        if (classes == null || classes.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建 classId → className 映射
        List<Long> classIds = new ArrayList<>();
        java.util.Map<Long, String> classIdToName = new java.util.HashMap<>();
        for (Map<String, Object> c : classes) {
            Object id = c.get("id");
            if (id != null) {
                Long cid = ((Number) id).longValue();
                classIds.add(cid);
                classIdToName.put(cid, (String) c.get("name"));
            }
        }
        if (classIds.isEmpty()) return new ArrayList<>();

        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Student::getClassId, classIds);
        List<Student> students = baseMapper.selectList(wrapper);
        
        // 填充 className（非数据库字段，需手动赋值）
        for (Student s : students) {
            String cn = classIdToName.get(s.getClassId());
            if (cn != null) s.setClassName(cn);
        }
        return students;
    }

    @Override
    public List<Student> searchStudents(String keyword) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Student::getName, keyword)
               .or()
               .like(Student::getStudentId, keyword);
        return baseMapper.selectList(wrapper);
    }
}
