package com.academic.service.impl;

import com.academic.common.entity.ScoreRecord;
import com.academic.mapper.ScoreRecordMapper;
import com.academic.service.ScoreRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ScoreRecordServiceImpl extends ServiceImpl<ScoreRecordMapper, ScoreRecord> implements ScoreRecordService {
}