package com.here.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.here.dao.QuestionMapper;
import com.here.entity.QuestionExample;
import com.here.entity.QuestionWithBLOBs;
import com.here.entity.vo.QuestionRequest;
import com.here.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionSeriviceImpl implements QuestionService {
    private final static Logger LOG = LoggerFactory.getLogger(QuestionSeriviceImpl.class);
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public PageInfo<QuestionWithBLOBs> selectQuestionList(QuestionRequest questionRequest) {
        if(questionRequest==null){
            LOG.error("分页问题查询参数不能为空");
            return null;
        }
        QuestionExample example = new QuestionExample();
        QuestionExample.Criteria criteria = example.createCriteria();
        if(questionRequest.getPageId()!=null){//编号
            criteria.andIdEqualTo(questionRequest.getPageId());
        }
        if(questionRequest.getCategoryId()!=null) {//问题类别
            criteria.andCatIdEqualTo(questionRequest.getCategoryId());
        }
        //问题关键字
        if(questionRequest.getLevel()!=null){
            criteria.andLevelEqualTo(questionRequest.getLevel().shortValue());
        }
        //问题类型
        if(questionRequest.getType()!=null){
            criteria.andTypeEqualTo(questionRequest.getType().shortValue());
        }
        List<QuestionWithBLOBs> questionWithBLOBsList = questionMapper.selectByExampleWithBLOBs(example);
        PageHelper.startPage(questionRequest.getPage(),questionRequest.getPageSize());
        PageInfo<QuestionWithBLOBs> pageInfo = new PageInfo<>(questionWithBLOBsList);
        pageInfo.setTotal(questionWithBLOBsList==null?0:questionWithBLOBsList.size());
        return pageInfo;
    }

    @Override
    public int addQuestion(QuestionWithBLOBs question) {
        if(question==null){
            LOG.error("数据不能为空");
            return -1;
        }
        if(StringUtils.isBlank(question.getTitle())){
            LOG.error("新增问题为空");
            return -1;
        }
        if(question.getType()==null){
            LOG.error("新增问题类型为空");
            return -1;
        }
        return questionMapper.insertSelective(question);
    }

    @Override
    public boolean modifyQuestion(QuestionWithBLOBs question) {
        if(question==null || question.getId()==null || question.getId()<1){
            LOG.error("修改问题id为空");
            return false;
        }
        if(StringUtils.isBlank(question.getTitle())){
            LOG.error("问题不能为空,{}",question.getId());
            return false;
        }
        if(question.getType()==null || question.getType()<1){
            LOG.error("问题类型不能为空,{}", question.getId());
            return false;
        }
        return questionMapper.updateByPrimaryKey(question)>0?true:false;
    }

    public boolean deleteQuestion(Integer questionId){
        if(questionId==null || questionId<=0){
            LOG.error("问题id不能为空");
            return false;
        }
        return questionMapper.deleteByPrimaryKey(questionId)>0?true:false;
    }
}
