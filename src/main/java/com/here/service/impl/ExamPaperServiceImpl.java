package com.here.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.here.dao.ExamInfoMapper;
import com.here.dao.ExamPaperMapper;
import com.here.dao.QuestionMapper;
import com.here.entity.ExamInfo;
import com.here.entity.ExamInfoExample;
import com.here.entity.ExamPaper;
import com.here.entity.ExamPaperExample;
import com.here.entity.vo.request.ExamPaperRequest;
import com.here.service.ExamPaperService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class ExamPaperServiceImpl implements ExamPaperService {
    private final static Logger LOG = LoggerFactory.getLogger(ExamPaperServiceImpl.class);

    @Autowired
    private ExamPaperMapper examPaperMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ExamInfoMapper examInfoMapper;

    @Override
    public boolean saveExamPaper(ExamPaper examPaper) {
        List<Integer> questionIdList = examPaper.getQuestionIdList();
        if(CollectionUtils.isEmpty(questionIdList)){
            questionIdList = getRandomQuestion(20);
        }
        if(CollectionUtils.isEmpty(questionIdList)){
            LOG.error("考题不能为空");
            return false;
        }
        //名称
        if(StringUtils.isBlank(examPaper.getName())){
            LOG.error("考卷名为空");
            return false;
        }
        //类型
        if(examPaper.getType()==null){
            LOG.error("考卷类型为空");
            return false;
        }
        //开始时间
        if(examPaper.getStartTime()==null){
            LOG.error("开始时间为空");
            return false;
        }
        //结束时间
        if(examPaper.getEndTime()==null){
            LOG.error("结束时间为空");
            return false;
        }
        //状态
        examPaperMapper.insertSelective(examPaper);
        final int examId = examPaper.getId();
        if(examPaper.getId()==null || examPaper.getId()<1){
            LOG.error("新增考卷失败,id={}",examId);
            return false;
        }
        return saveQuestionList(questionIdList,examId);
    }

    /**
     * 随机选取考题
     * @param size
     * @return
     */
    private List<Integer> getRandomQuestion(int size){
        List<Integer> idList = Lists.newArrayList();
        List<Integer> questionList = questionMapper.selectQuestionIds();
        if (questionList.size() < size ) {
            LOG.error("没有找到有效考题");
            return idList;
        }
        Collections.shuffle(questionList);
        for(int i=0;i<size;i++){
            idList.add(questionList.get(i));
        }
        return idList;
    }

    /**
     * 新增考题
     * @param questionIdList
     * @param examId
     * @return
     */
    private boolean saveQuestionList(List<Integer> questionIdList,final Integer examId){
        return saveQuestionList(questionIdList,examId,false);
    }
    /**
     * 持久化问题
     * @param questionIdList
     * @param examId
     * @param isModify
     * @return
     */
    private boolean saveQuestionList(List<Integer> questionIdList, final Integer examId,boolean isModify){
        List<ExamInfo> examInfoList = Lists.transform(questionIdList, new Function<Integer, ExamInfo>() {
            @Override
            public ExamInfo apply(Integer integer) {
                ExamInfo examInfo = new ExamInfo();
                examInfo.setQuestionId(integer);
                examInfo.setExamId(examId);
                return examInfo;
            }
        });
        //保存考题信息
        for(ExamInfo examInfo:examInfoList){
            if(examInfoMapper.insertSelective(examInfo)<1){
                LOG.error("保存考卷考题失败,{},{},{}",examInfo.getExamId(),examInfo.getQuestionId(),isModify);
//                return false;
            }
        }
        return true;
    }

    @Override
    public boolean modifyExamPaper(ExamPaper examPaper) {
        if(examPaper.getId()==null){
            LOG.error("考卷id为空");
        }
        //考题
        List<Integer> questionIdList = examPaper.getQuestionIdList();
        if(CollectionUtils.isEmpty(questionIdList)){
            LOG.error("修改考卷时,考题不能为空,{}",examPaper.getId());
            return false;
        }
        //名称
        if(StringUtils.isBlank(examPaper.getName())){
            LOG.error("考卷名为空");
            return false;
        }
        //类型
        if(examPaper.getType()==null){
            LOG.error("考卷类型为空");
            return false;
        }
        int modifyCounter = examPaperMapper.updateByPrimaryKey(examPaper);
        if(modifyCounter<1){
            LOG.error("修改考卷失败,id={},flag={}",examPaper.getId(),modifyCounter);
            return false;
        }
        //删除旧数据
        boolean isDel = deleteExamInfo(examPaper.getId());
        if(!isDel){
            LOG.error("删除考卷考题失败,{}",examPaper.getId());
            return false;
        }

        //重新插入
        return saveQuestionList(questionIdList,examPaper.getId(),true);
    }

    /**
     * 删除旧考题
     * @param examId
     * @return
     */
    private boolean deleteExamInfo(Integer examId){
        ExamInfoExample examInfoExample = new ExamInfoExample();
        examInfoExample.createCriteria().andExamIdEqualTo(examId);
        int delCounter = examInfoMapper.deleteByExample(examInfoExample);
        LOG.info("删除考卷原有数据共:{}",delCounter);
        return true;
    }

    @Override
    public boolean deleteExamPaper(Integer id) {
        return examPaperMapper.deleteByPrimaryKey(id)>0?true:false;
    }

    @Override
    public PageInfo<ExamPaper> selectExamPaper(ExamPaperRequest examPaperRequest) {
        //id
        ExamPaperExample example = new ExamPaperExample();
        ExamPaperExample.Criteria criteria = example.createCriteria();
        if(examPaperRequest.getId()!=null){
            criteria.andIdEqualTo(examPaperRequest.getId());
        }
        //名称
        if(StringUtils.isNotBlank(examPaperRequest.getName())){
            criteria.andNameLike("*"+examPaperRequest.getName()+"*");
        }
        //类型
        if(examPaperRequest.getType()!=null){
            criteria.andTypeEqualTo(examPaperRequest.getType().shortValue());
        }
        //状态
        if(examPaperRequest.getStatus()!=null){
            criteria.andStatusEqualTo(examPaperRequest.getStatus().shortValue());
        }
        //总分
        if(examPaperRequest.getTotalScore()!=null){
            criteria.andTotalScoreGreaterThanOrEqualTo(new BigDecimal(examPaperRequest.getTotalScore()));
        }
        //及格分
        if(examPaperRequest.getPassing()!=null){
            criteria.andPassingEqualTo(new BigDecimal(examPaperRequest.getPassing()));
        }
        List<ExamPaper> examPaperList = examPaperMapper.selectByExample(example);
        PageHelper.startPage(examPaperRequest.getPage(),examPaperRequest.getPageSize());
        PageInfo<ExamPaper> pageInfo = new PageInfo<>(examPaperList);
        return pageInfo;
    }

    @Override
    public ExamPaper selectExamPaper(Integer id) {
        return examPaperMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean startExam(Integer examId) {
        ExamPaper examPaper = new ExamPaper();
        examPaper.setStatus(Short.valueOf("0"));
        ExamPaperExample example = new ExamPaperExample();
        example.createCriteria().andStatusEqualTo(Short.valueOf("1"));
        examPaperMapper.updateByExampleSelective(examPaper,example);
        ExamPaper myExam = new ExamPaper();
        myExam.setId(examId);
        myExam.setStatus(Short.valueOf("1"));
        examPaperMapper.updateByPrimaryKeySelective(myExam);
        return true;
    }

    @Override
    public ExamPaper getExamPaper() {
        ExamPaperExample example = new ExamPaperExample();
        example.createCriteria().andStatusEqualTo(Short.valueOf("1"));
        List<ExamPaper> examPapers = examPaperMapper.selectByExample(example);

        return CollectionUtils.isEmpty(examPapers)?null:examPapers.get(0);
    }
}
