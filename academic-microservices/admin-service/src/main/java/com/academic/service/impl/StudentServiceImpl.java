package com.academic.service.impl;

import com.academic.common.entity.Student;
import com.academic.common.entity.User;
import com.academic.entity.Classes;
import com.academic.mapper.StudentMapper;
import com.academic.mapper.UserMapper;
import com.academic.mapper.ClassesMapper;
import com.academic.service.StudentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学生服务实现类
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ClassesMapper classesMapper;

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = baseMapper.selectList(null);
        // 填充关联字段
        for (Student student : students) {
            // 获取用户信息
            User user = userMapper.selectById(student.getUserId());
            if (user != null) {
                student.setEmail(user.getEmail());
                student.setPhone(user.getPhone());
            }
            // 获取班级名称
            Classes classes = classesMapper.selectById(student.getClassId());
            if (classes != null) {
                student.setClassName(classes.getName());
            }
        }
        return students;
    }

    @Override
    public List<Student> searchStudents(String keyword) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Student::getName, keyword)
               .or()
               .like(Student::getStudentId, keyword);
        List<Student> students = baseMapper.selectList(wrapper);
        // 填充关联字段
        for (Student student : students) {
            // 获取用户信息
            User user = userMapper.selectById(student.getUserId());
            if (user != null) {
                student.setEmail(user.getEmail());
                student.setPhone(user.getPhone());
            }
            // 获取班级名称
            Classes classes = classesMapper.selectById(student.getClassId());
            if (classes != null) {
                student.setClassName(classes.getName());
            }
        }
        return students;
    }

    @Override
    public Student getByUserId(Long userId) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUserId, userId);
        Student student = baseMapper.selectOne(wrapper);
        if (student != null) {
            // 填充关联字段
            User user = userMapper.selectById(student.getUserId());
            if (user != null) {
                student.setEmail(user.getEmail());
                student.setPhone(user.getPhone());
            }
            // 获取班级名称
            Classes classes = classesMapper.selectById(student.getClassId());
            if (classes != null) {
                student.setClassName(classes.getName());
            }
        }
        return student;
    }
}
