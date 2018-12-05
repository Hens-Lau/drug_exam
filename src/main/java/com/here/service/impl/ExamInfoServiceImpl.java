package com.here.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.here.dao.ExamInfoMapper;
import com.here.dao.QuestionMapper;
import com.here.entity.ExamInfo;
import com.here.entity.ExamInfoExample;
import com.here.entity.QuestionWithBLOBs;
import com.here.service.ExamInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service
public class ExamInfoServiceImpl implements ExamInfoService {
    private final static Logger LOG = LoggerFactory.getLogger(ExamInfoServiceImpl.class);
    @Autowired
    private ExamInfoMapper examInfoMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public boolean saveBatch(List<Integer> questionIdList, final Integer examId) {
        if(CollectionUtils.isEmpty(questionIdList)){
            LOG.error("新增考卷试题不能为空,{}",examId);
            return false;
        }
        //重复考题
        Set<Integer> idSet = Sets.newHashSet(questionIdList);
        if(idSet.size()!=questionIdList.size()){
            LOG.error("存在重复试题,{}",examId);
            return false;
        }
        List<ExamInfo> examInfoList = Lists.transform(questionIdList, new Function<Integer, ExamInfo>() {
            @Override
            public ExamInfo apply(Integer integer) {
                ExamInfo examInfo = new ExamInfo();
                examInfo.setExamId(examId);
                examInfo.setQuestionId(integer);
                return examInfo;
            }
        });
        int counter = 0;
        for(ExamInfo examInfo: examInfoList){
            int id = examInfoMapper.insertSelective(examInfo);
            if(id>0){
                counter++;
            } else {
                LOG.error("试题保存失败,{},{}",examInfo.getExamId(),examInfo.getQuestionId());
            }
        }
        if(counter<examInfoList.size()){
            LOG.error("试卷报错错误,错误数量:{}", examInfoList.size()-counter);
        }
        return true;
    }

    @Override
    public boolean modifyExamInfo(List<Integer> questionIdList, Integer examId) {
        //删除旧数据
        ExamInfoExample examInfoExample = new ExamInfoExample();
        examInfoExample.createCriteria().andExamIdEqualTo(examId);
        int delTotal = examInfoMapper.deleteByExample(examInfoExample);
        LOG.info("修改考卷{},共删除旧数据:{}",examId,delTotal);
        //插入新数据
        return saveBatch(questionIdList,examId);
    }

    @Override
    public List<QuestionWithBLOBs> selectExamQuestionList(Integer examId) {
        if(examId==null || examId<1){
            LOG.error("考卷不能为空,{}",examId);
            return Lists.newArrayList();
        }
        List<QuestionWithBLOBs> questionList = questionMapper.selectExamQuestions(examId);
        if(CollectionUtils.isEmpty(questionList)){
            LOG.error("考卷内容为空,{}",examId);
            return Lists.newArrayList();
        }
        return questionList;
    }
}
