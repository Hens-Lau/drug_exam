package com.here.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.here.dao.ExamLogMapper;
import com.here.entity.ExamLog;
import com.here.entity.ExamLogExample;
import com.here.entity.vo.request.ExamLogRequest;
import com.here.service.ExamLogService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExamLogServiceImpl implements ExamLogService {
    private final static Logger LOG = LoggerFactory.getLogger(ExamLogServiceImpl.class);
    @Autowired
    private ExamLogMapper examLogMapper;
    @Override
    public boolean saveBatch(List<ExamLog> examLogList, Integer paperId) {
        //判断用户id
        if(CollectionUtils.isEmpty(examLogList) || paperId==null){
            LOG.error("无有效考题答案,{}",paperId);
            return false;
        }
        int counter=0;
        for(ExamLog examLog: examLogList){
            //用户id
            if(examLog.getUserId()==null){
                LOG.error("无有效考生信息,{}",paperId);
                return false;
            }
            //问题id
            if(examLog.getQuestionId()==null){
                LOG.error("无有效考题,{}",paperId);
            }
            examLog.setPaperId(paperId);
            int id = examLogMapper.insertSelective(examLog);
            if(id>0){
                counter++;
            } else {
                LOG.error("保存考卷答案失败,{},{},{},{}",examLog.getUserId(),examLog.getQuestionId(),paperId,examLog.getAnswer());
            }
        }
        if(examLogList.size()!=counter){
            LOG.error("考题报错错误,考生:{},考卷:{}总量:{},保存成功:{}",examLogList.get(0).getUserId(),paperId,examLogList.size(),counter);
        }
        return true;
    }

    @Override
    public boolean modifyLog(ExamLog examLog) {
        if(examLog.getId()==null){
            LOG.error("修改失败,id为空,{},{},{}",examLog.getUserId(),examLog.getQuestionId(),examLog.getPaperId());
            return false;
        }
        return examLogMapper.updateByPrimaryKeySelective(examLog)>0?true:false;
    }

    @Override
    public boolean modifyBatchLog(List<ExamLog> examLogList) {
        if(CollectionUtils.isEmpty(examLogList)){
            LOG.error("考题答案为空");
            return false;
        }
        int counter = 0;
        for(ExamLog examLog: examLogList){
            if(examLog.getId()==null){
                LOG.error("考题答案id为空,{},{},{},{}",examLog.getUserId(),examLog.getQuestionId(),examLog.getAnswer(),examLog.getPaperId());
                continue;
            }
            if(examLogMapper.updateByPrimaryKeySelective(examLog)>0){
                counter++;
            } else {
                LOG.error("考卷答案修改失败,{},{},{},{}",examLog.getUserId(),examLog.getQuestionId(),examLog.getAnswer(),examLog.getPaperId());
            }
        }
        if(counter!=examLogList.size()){
            LOG.error("修改考题答案异常,用户:{},考卷:{},修改总量:{},成功总量:{}",examLogList.get(0).getUserId(),examLogList.get(0).getPaperId(),examLogList.size(),counter);
        }
        return true;
    }

    @Override
    public PageInfo<ExamLog> selectExamLog(ExamLogRequest request) {
        //id
        ExamLogExample example = new ExamLogExample();
        ExamLogExample.Criteria criteria = example.createCriteria();
        //考卷id
        if(request.getId()!=null){
            criteria.andIdEqualTo(request.getId());
        }
        //用户编号
        if(StringUtils.isNotBlank(request.getUserId())){
            criteria.andUserIdEqualTo(request.getUserId());
        }
        //考题id
        if(request.getQuestionId()!=null){
            criteria.andQuestionIdEqualTo(request.getQuestionId());
        }
        //分数
        if(request.getScore()!=null){
            criteria.andScoreGreaterThanOrEqualTo(new BigDecimal(request.getScore()));
        }
        //批卷老师编号
        if(StringUtils.isNotBlank(request.getTeacherId())){
            criteria.andTeacherIdEqualTo(request.getTeacherId());
        }
        //考卷考题编号
        if(request.getQuestionNo()!=null){
            criteria.andQuestionNoEqualTo(request.getQuestionNo());
        }
        //考题状态
        if(request.getStatus()!=null){
            criteria.andStatusEqualTo(request.getStatus().shortValue());
        }
        List<ExamLog> examLogList = examLogMapper.selectByExample(example);
        PageHelper.startPage(request.getPage(),request.getPageSize());
        PageInfo<ExamLog> pageInfo = new PageInfo<>(examLogList);
        return pageInfo;
    }

    @Override
    public ExamLog selectExamLog(Integer id) {
        if(id==null){
            LOG.error("查询考题答案id为空");
            return null;
        }
        return examLogMapper.selectByPrimaryKey(id);
    }
}
