package com.here.service;

import com.github.pagehelper.PageInfo;
import com.here.entity.ExamLog;
import com.here.entity.vo.ExamLogRequest;

import java.util.List;

public interface ExamLogService {
    /**
     * 保存答卷
     * @param examLogList
     * @param paperId
     * @return
     */
    boolean saveBatch(List<ExamLog> examLogList, Integer paperId);

    /**
     * 修改答卷
     * @param examLog
     * @return
     */
    boolean modifyLog(ExamLog examLog);

    /**
     * 修改答卷
     * @param examLogList
     * @return
     */
    boolean modifyBatchLog(List<ExamLog> examLogList);

    /**
     * 分页查询
     * @param request
     * @return
     */
    PageInfo<ExamLog> selectExamLog(ExamLogRequest request);

    /**
     * 查询答题信息
     * @param id
     * @return
     */
    ExamLog selectExamLog(Integer id);
}
