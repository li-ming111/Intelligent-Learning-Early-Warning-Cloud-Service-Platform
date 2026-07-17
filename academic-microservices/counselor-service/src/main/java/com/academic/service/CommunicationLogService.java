package com.academic.service;

import com.academic.common.entity.CommunicationLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 沟通记录服务接口
 */
public interface CommunicationLogService extends IService<CommunicationLog> {
    /**
     * 根据学生ID获取沟通记录列表
     * @param studentId 学生ID
     * @return 沟通记录列表
     */
    List<CommunicationLog> getLogsByStudentId(Long studentId);

    /**
     * 根据辅导员ID获取沟通记录列表
     * @param counselorId 辅导员ID
     * @return 沟通记录列表
     */
    List<CommunicationLog> getLogsByCounselorId(Long counselorId);

    /**
     * 创建沟通记录
     * @param log 沟通记录
     * @return 是否创建成功
     */
    boolean createLog(CommunicationLog log);

    /**
     * 更新沟通记录
     * @param log 沟通记录
     * @return 是否更新成功
     */
    boolean updateLog(CommunicationLog log);

    /**
     * 删除沟通记录
     * @param id 沟通记录ID
     * @return 是否删除成功
     */
    boolean deleteLog(Long id);
}
