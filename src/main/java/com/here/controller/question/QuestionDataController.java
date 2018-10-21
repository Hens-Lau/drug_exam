package com.here.controller.question;

import com.github.pagehelper.PageInfo;
import com.here.entity.Question;
import com.here.entity.QuestionWithBLOBs;
import com.here.entity.vo.QuestionRequest;
import com.here.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class QuestionDataController {
    private final static Logger LOG = LoggerFactory.getLogger(QuestionDataController.class);
    private final static Integer DEFAULT_PAGE=1;
    private final static Integer DEFAULT_PAGE_SIZE=20;
    @Autowired
    private QuestionService questionService;

    @RequestMapping(value = "/admin/getAllQuestionBank")
    public PageInfo<QuestionWithBLOBs> queryQuestionInfo(HttpServletRequest request,@RequestBody QuestionRequest questionRequest){
        if(questionRequest==null){
            return new PageInfo<>();
        }
        if(questionRequest.getPage()==null){
            questionRequest.setPage(DEFAULT_PAGE);
        }
        if(questionRequest.getPageSize()==null){
            questionRequest.setPageSize(DEFAULT_PAGE_SIZE);
        }
        return questionService.selectQuestionList(questionRequest);
    }

    @RequestMapping(value = "/admin/addquestionbank")
    public Integer addQuestion(HttpServletRequest request,@RequestBody QuestionWithBLOBs question){
        if(question==null){
            LOG.error("数据不能为空");
            return -1;
        }
        return questionService.addQuestion(question);
    }

    @RequestMapping(value = "/admin/updatequestionbank-{id}")
    public boolean modifyQuestion(HttpServletRequest request, @RequestBody QuestionWithBLOBs question, @PathVariable String id){
        if(StringUtils.isBlank(id) || !StringUtils.isNumeric(id)){
            LOG.error("修改的问题id有误,{}",id);
            return false;
        }
        return questionService.modifyQuestion(question);
    }

    @RequestMapping(value = "/admin/deletequestionbank")
    public boolean delete(HttpServletRequest request, @RequestBody Question question){
        if(question==null || question.getId()==null || question.getId()<1){
            LOG.error("无法删除问题，id为空");
            return false;
        }
        return questionService.deleteQuestion(question.getId());
    }

}
