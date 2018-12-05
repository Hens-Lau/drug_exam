package com.here.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.here.dao.ExamLogMapper;
import com.here.entity.ExamLog;
import com.here.entity.ExamLogExample;
import com.here.entity.QuestionWithBLOBs;
import com.here.entity.vo.request.ExamLogRequest;
import com.here.entity.vo.response.ExamResponse;
import com.here.service.ExamInfoService;
import com.here.service.ExamLogService;
import com.here.service.ExamPaperService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ExamLogServiceImpl implements ExamLogService {
    private final static Logger LOG = LoggerFactory.getLogger(ExamLogServiceImpl.class);
    @Autowired
    private ExamLogMapper examLogMapper;
    @Autowired
    private ExamInfoService examInfoService;
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

    @Override
    public ExamResponse saveExam(ExamLogRequest request) {
        ExamResponse response = new ExamResponse();
        //用户编号
        String studentNo = request.getUserId();
        if(StringUtils.isBlank(studentNo)){
            LOG.error("用户编号不能为空");
            response.setErrorMsg("没有找到考生信息!");
            return response;
        }
        //考卷
        Integer examId = request.getPaperId();
        //根据考卷查询考题
        List<QuestionWithBLOBs> questionList = examInfoService.selectExamQuestionList(examId);
        if(CollectionUtils.isEmpty(questionList)){
            LOG.error("考卷为空,{},{}",studentNo,examId);
            response.setErrorMsg("考卷为空!");
            return response;
        }
        //处理答案
        String answer = request.getAnswer();
        Map<Integer,String> answerMap = trans2Answer(answer);
        //根据考题答案，计算得分
        BigDecimal score = rewinding(questionList,answerMap);
        ExamLog examLog = new ExamLog();
        examLog.setUserId(studentNo);
        examLog.setPaperId(examId);
        examLog.setAnswer(answer);
        examLog.setScore(score);
        short init = Short.valueOf("0");
        examLog.setStatus(init);
        //自动批卷时，老师编号是0000
        examLog.setTeacherId("0000");
        examLogMapper.insertSelective(examLog);
        response.setExamLogId(examLog.getId());
        response.setScore(score.toString());
        return response;
    }

    private BigDecimal rewinding(List<QuestionWithBLOBs> questionList, Map<Integer,String> answerMap){
        BigDecimal total = new BigDecimal(0);
        for(QuestionWithBLOBs question: questionList){
            if(StringUtils.equalsIgnoreCase(question.getAnswer(),answerMap.get(question.getId()))){
                if(question.getInitScore()==null){
                    LOG.warn("考题没有设定分数,id:{},使用默认分数:{}",question.getId(),5);
                    total = total.add(new BigDecimal("5"));
                } else {
                    total = total.add(question.getInitScore());
                }
            }
        }
        total.setScale(2,BigDecimal.ROUND_HALF_UP);
        return total;
    }

    private Map<Integer,String> trans2Answer(String answer){
        Map<Integer,String> answerMap = Maps.newHashMap();
        if(StringUtils.isBlank(answer)){
            return answerMap;
        }
        for(String entry:StringUtils.split(answer,"&")){
            String[] array = StringUtils.split(entry,"=");
            if(array==null || array.length<2){
                LOG.error("用户的答案存在问题:{},{}",entry,answer);
                continue;
            }
            Integer key = Integer.parseInt(array[0]);
            if(answerMap.containsKey(key)){
                String preVal = answerMap.get(key);
                answerMap.put(key,preVal+array[1]);
            } else {
                answerMap.put(key,array[1]);
            }
        }
        return answerMap;
    }
}
