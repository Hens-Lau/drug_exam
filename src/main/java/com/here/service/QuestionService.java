package com.here.service;

import com.github.pagehelper.PageInfo;
import com.here.entity.QuestionWithBLOBs;
import com.here.entity.vo.QuestionRequest;

public interface QuestionService {
    /**
     * 查询问题列表
     * @param questionRequest
     * @return
     */
    PageInfo<QuestionWithBLOBs> selectQuestionList(QuestionRequest questionRequest);

    /**
     * 保存问题
     * @param question
     * @return
     */
    int addQuestion(QuestionWithBLOBs question);

    /**
     * 修改问题
     * @param question
     * @return
     */
    boolean modifyQuestion(QuestionWithBLOBs question);

    /**
     * 删除问题
     * @param questionId
     * @return
     */
    boolean deleteQuestion(Integer questionId);
}
