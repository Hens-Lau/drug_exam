package com.here.controller.question;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.here.entity.Question;
import com.here.entity.QuestionWithBLOBs;
import com.here.entity.vo.request.QuestionRequest;
import com.here.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

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
        //处理选择题选项
        if(question.getType()==1 || question.getType()==2){
            String options = trans2Json(question.getOptionList());
            LOG.info("新增的选择题选项内容:{}",options);
            question.setOptions(options);
        }
        return questionService.addQuestion(question);
    }

    @RequestMapping(value = "/admin/updatequestionbank")
    public boolean modifyQuestion(HttpServletRequest request, @RequestBody QuestionWithBLOBs question){
        if(question==null ||question.getId()==null){
            LOG.error("修改的问题id有误");
            return false;
        }
        return questionService.modifyQuestion(question);
    }

    @RequestMapping(value = "/admin/deletequestionbank")
    public boolean delete(HttpServletRequest request, @RequestBody QuestionWithBLOBs question){
        if(question==null || CollectionUtils.isEmpty(question.getIdList())){
            LOG.error("无法删除问题，id为空");
            return false;
        }
        return questionService.deleteQuestion(question.getIdList());
    }

    private String trans2Json(List<String> optionList){
        if(CollectionUtils.isEmpty(optionList)){
            return null;
        }
        JSONObject obj = new JSONObject();
        obj.put("A",optionList.get(0));
        if(optionList.size()>1){
            obj.put("B",optionList.get(1));
        } else {
            obj.put("B",null);
        }
        if(optionList.size()>2){
            obj.put("C",optionList.get(2));
        } else {
            obj.put("C",null);
        }
        if(optionList.size()>3){
            obj.put("D",optionList.get(3));
        } else {
            obj.put("D",null);
        }
        return obj.toJSONString();
    }
}
