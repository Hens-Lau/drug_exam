package com.here.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.here.dao.ExamPaperMapper;
import com.here.entity.ExamPaper;
import com.here.entity.ExamPaperExample;
import com.here.entity.vo.ExamPaperRequest;
import com.here.service.ExamPaperService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExamPaperServiceImpl implements ExamPaperService {
    private final static Logger LOG = LoggerFactory.getLogger(ExamPaperServiceImpl.class);

    @Autowired
    private ExamPaperMapper examPaperMapper;
    @Override
    public boolean saveExamPaper(ExamPaper examPaper) {
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

        return examPaperMapper.insertSelective(examPaper)>0?true:false;
    }

    @Override
    public boolean modifyExamPaper(ExamPaper examPaper) {
        if(examPaper.getId()==null){
            LOG.error("考卷id为空");
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
        return examPaperMapper.updateByPrimaryKey(examPaper)>0?true:false;
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
        if(examPaperRequest.getName()!=null){
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
}
