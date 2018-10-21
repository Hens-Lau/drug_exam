package com.here.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.here.dao.ScoreMapper;
import com.here.entity.Score;
import com.here.entity.ScoreExample;
import com.here.entity.vo.ScoreRequest;
import com.here.service.ScoreService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {
    private final static Logger LOG = LoggerFactory.getLogger(ScoreServiceImpl.class);
    @Autowired
    private ScoreMapper scoreMapper;

    @Override
    public PageInfo<Score> selectScoreList(ScoreRequest scoreRequest) {
        ScoreExample example = new ScoreExample();
        ScoreExample.Criteria criteria = example.createCriteria();
        //考场
        if(scoreRequest.getRoomId()!=null){
            criteria.andRoomIdEqualTo(scoreRequest.getRoomId());
        }
        //用户
        if(StringUtils.isNotBlank(scoreRequest.getUserId())){
            criteria.andUserIdEqualTo(scoreRequest.getUserId());
        }
        //状态
        if(scoreRequest.getStatus()!=null){
            criteria.andStatusEqualTo(scoreRequest.getStatus().shortValue());
        }
        //获奖状态

        //打印状态
        if(scoreRequest.getPrintStatus()!=null){
            criteria.andPrintStatusEqualTo(scoreRequest.getPrintStatus().shortValue());
        }
        List<Score> scoreList = scoreMapper.selectByExample(example);
        PageHelper.startPage(scoreRequest.getPage(),scoreRequest.getPageSize());
        PageInfo<Score> pageInfo = new PageInfo<>(scoreList);
        pageInfo.setTotal(scoreList==null?0:scoreList.size());
        return pageInfo;
    }

    @Override
    public boolean modifyScore(Score score) {
        if(score==null || score.getId()==null || score.getId()<1){
            LOG.error("修改分数时,id为空");
            return false;
        }
        return scoreMapper.updateByPrimaryKey(score)>0?true:false;
    }

    @Override
    public boolean saveScore(Score score) {
        //考场
        if(score.getRoomId()==null || score.getRoomId()<1){
            LOG.error("考场为空");
            return false;
        }
        //用户
        if(StringUtils.isBlank(score.getUserId())){
            LOG.error("用户不能为空");
            return false;
        }
        //时间
        return scoreMapper.insertSelective(score)>0?true:false;
    }

    @Override
    public Score selectScore(Integer id) {
        if(id==null){
            LOG.error("查询id为空");
            return null;
        }
        return scoreMapper.selectByPrimaryKey(id);
    }
}
