package com.here.service;

import com.here.entity.ExamInfo;
import com.here.entity.QuestionWithBLOBs;

import java.util.List;

public interface ExamInfoService {
    /**
     * 批量新增考卷考题
     * @param questionIdList
     * @param examId
     * @return
     */
    boolean saveBatch(List<Integer> questionIdList, Integer examId);

    /**
     * 修改考卷考题
     * @param questionIdList
     * @param examId
     * @return
     */
    boolean modifyExamInfo(List<Integer> questionIdList, Integer examId);

    /**
     * 查询考卷下考题集合
     * @param examId
     * @return
     */
    List<QuestionWithBLOBs> selectExamQuestionList(Integer examId);
}
