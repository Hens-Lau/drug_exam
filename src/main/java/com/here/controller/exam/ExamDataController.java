package com.here.controller.exam;

import com.github.pagehelper.PageInfo;
import com.here.entity.ExamPaper;
import com.here.entity.vo.request.ExamPaperRequest;
import com.here.service.ExamPaperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ExamDataController {
    private final static Logger LOG = LoggerFactory.getLogger(ExamDataController.class);
    @Autowired
    private ExamPaperService examPaperService;

    /**
     * 删除考卷
     * @param request
     * @param examPaperRequest
     * @return
     */
    @RequestMapping(value = "/admin/deleteExamPaper")
    public boolean deleteExamPaper(HttpServletRequest request, @RequestBody ExamPaperRequest examPaperRequest){
        if(examPaperRequest==null || examPaperRequest.getId()==null){
            LOG.error("考卷不能为空");
            return false;
        }
        boolean delFlag = examPaperService.deleteExamPaper(examPaperRequest.getId());
        return delFlag;
    }

    /**
     * 新增考卷
     * @param request
     * @param examPaper
     * @return
     */
    @RequestMapping(value = "/admin/addExamPaper")
    public boolean addExamPaper(HttpServletRequest request, @RequestBody ExamPaper examPaper){
        if(examPaper==null){
            LOG.error("新增信息为空");
            return false;
        }
        return examPaperService.saveExamPaper(examPaper);
    }

    /**
     * 分页查询考卷信息
     * @param request
     * @param examPaperRequest
     * @return
     */
    @RequestMapping(value = "/admin/getAllExamPaper")
    public PageInfo<ExamPaper> selectExamPaperList(HttpServletRequest request,@RequestBody ExamPaperRequest examPaperRequest){
        if(examPaperRequest==null){
            examPaperRequest = new ExamPaperRequest();
        }
        return examPaperService.selectExamPaper(examPaperRequest);
    }

    /**
     * 修改考卷
     * @param request
     * @param examPaper
     * @return
     */
    @RequestMapping(value = "/admin/modifyExamPaper")
    public boolean modifyExamPaper(HttpServletRequest request, @RequestBody ExamPaper examPaper){
        if(examPaper==null || examPaper.getId()==null){
            LOG.error("修改考卷失败,数据为空");
            return false;
        }
        return examPaperService.modifyExamPaper(examPaper);
    }
}
