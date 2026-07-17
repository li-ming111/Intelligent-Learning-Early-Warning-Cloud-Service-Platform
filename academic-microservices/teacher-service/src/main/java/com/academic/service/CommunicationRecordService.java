package com.academic.service;

import com.academic.entity.CommunicationRecord;
import com.academic.mapper.CommunicationRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunicationRecordService {
    @Autowired
    private CommunicationRecordMapper recordMapper;
    
    public void saveCommunication(Long teacherId, Long studentId, String content, String type, String studentName) {
        CommunicationRecord record = new CommunicationRecord();
        record.setTeacherId(teacherId);
        record.setStudentId(studentId);
        record.setContent(content);
        record.setType(type);
        record.setStudentName(studentName);
        recordMapper.insertRecord(record);
    }
    
    public List<CommunicationRecord> getRecordsByTeacherId(Long teacherId) {
        return recordMapper.getRecordsByTeacherId(teacherId);
    }
}