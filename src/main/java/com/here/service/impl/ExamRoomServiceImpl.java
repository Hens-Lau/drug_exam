package com.here.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.here.dao.ExamRoomMapper;
import com.here.entity.ExamRoom;
import com.here.entity.ExamRoomExample;
import com.here.entity.vo.request.ExamRoomRequest;
import com.here.service.ExamRoomService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ExamRoomServiceImpl implements ExamRoomService {
    private final static Logger LOG = LoggerFactory.getLogger(ExamRoomServiceImpl.class);
    @Autowired
    private ExamRoomMapper examRoomMapper;

    @Override
    public boolean saveExamRoom(ExamRoom examRoom) {
        //id
        if(examRoom==null){
            LOG.error("新增信息为空");
            return false;
        }
        //考卷id
        if(examRoom.getPaperId()==null){
            LOG.error("考卷信息为空");
            return false;
        }
        //用户id
        if(StringUtils.isBlank(examRoom.getUserId())){
            LOG.error("考生信息为空");
            return false;
        }
        return examRoomMapper.insertSelective(examRoom)>0?true:false;
    }

    @Override
    public boolean modifyExamRoom(ExamRoom examRoom) {
        //id
        if(examRoom==null || examRoom.getId()==null){
            LOG.error("考场id为空");
            return false;
        }
        //用户id
        if(StringUtils.isBlank(examRoom.getUserId())){
            LOG.error("用户id为空");
            return false;
        }
        //考卷id
        if(examRoom.getPaperId()==null){
            LOG.error("考卷id为空");
            return false;
        }
        //时间
        examRoom.setUpdateTime(new Date(System.currentTimeMillis()));
        return examRoomMapper.updateByPrimaryKeySelective(examRoom)>0?true:false;
    }

    @Override
    public PageInfo<ExamRoom> selectExamRoom(ExamRoomRequest roomRequest) {
        //id
        ExamRoomExample example = new ExamRoomExample();
        ExamRoomExample.Criteria criteria = example.createCriteria();
        if(roomRequest.getId()==null){
            criteria.andIdEqualTo(roomRequest.getId());
        }
        //考卷id
        if(roomRequest.getPaperId()==null){
            criteria.andPaperIdEqualTo(roomRequest.getPaperId());
        }
        //用户编号
        if(StringUtils.isNotBlank(roomRequest.getUserId())){
            criteria.andUserIdEqualTo(roomRequest.getUserId());
        }
        //花费时间
        if(roomRequest.getTakeTime()!=null){
            criteria.andTakeTimeLessThanOrEqualTo(roomRequest.getTakeTime());
        }
        //状态
        if(roomRequest.getStatus()!=null){
            criteria.andStatusEqualTo(roomRequest.getStatus().shortValue());
        }
        List<ExamRoom> examRoomList = examRoomMapper.selectByExample(example);
        PageHelper.startPage(roomRequest.getPage(),roomRequest.getPageSize());
        PageInfo<ExamRoom> pageInfo = new PageInfo<>(examRoomList);
        return pageInfo;
    }

    @Override
    public ExamRoom selectExamRoom(Integer id) {
        if(id==null){
            LOG.error("考场id为空");
            return null;
        }

        return examRoomMapper.selectByPrimaryKey(id);
    }
}
