package com.here.service;

import com.github.pagehelper.PageInfo;
import com.here.entity.ExamPaper;
import com.here.entity.vo.request.ExamPaperRequest;

public interface ExamPaperService {
    /**
     * 新增考卷
     * @param examPaper
     * @return
     */
    boolean saveExamPaper(ExamPaper examPaper);

    /**
     * 修改考卷
     * @param examPaper
     * @return
     */
    boolean modifyExamPaper(ExamPaper examPaper);

    /**
     * 删除考卷
     * @param id
     * @return
     */
    boolean deleteExamPaper(Integer id);

    /**
     * 分页查询考卷
     * @param examPaperRequest
     * @return
     */
    PageInfo<ExamPaper> selectExamPaper(ExamPaperRequest examPaperRequest);

    /**
     * 查询考卷
     * @param id
     * @return
     */
    ExamPaper selectExamPaper(Integer id);

    /**
     * 开卷
     * @param examId
     * @return
     */
    boolean startExam(Integer examId);

    ExamPaper getExamPaper();
}
