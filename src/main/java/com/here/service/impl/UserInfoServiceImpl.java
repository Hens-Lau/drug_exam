package com.here.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.here.dao.UserInfoMapper;
import com.here.entity.UserInfo;
import com.here.entity.UserInfoExample;
import com.here.entity.vo.request.UserRequest;
import com.here.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    private final static Logger LOG = LoggerFactory.getLogger(UserInfoServiceImpl.class);
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Override
    public Short queryUserType(UserInfo userInfo) {
        if(userInfo==null){
            LOG.error("查询用户信息不能未空");
            return -1;
        }
        if (StringUtils.isEmpty(userInfo.getStudentNo())){
            LOG.warn("查询用户编号不能为空");
            return -1;
        }
        if(StringUtils.isEmpty(userInfo.getPassword())){
            LOG.warn("查询用户密码不能为空");
            return -1;
        }
        UserInfoExample example = new UserInfoExample();
        example.createCriteria().andStudentNoEqualTo(userInfo.getStudentNo())
                .andPasswordEqualTo(userInfo.getPassword())
        .andStatusEqualTo(new Short("1"));
        List<UserInfo> userInfoList = userInfoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(userInfoList)){
            LOG.info("没有找到用户,{}",userInfo.getStudentNo());
            return -1;
        }
        return userInfoList.get(0).getRole();
    }

    @Override
    public boolean addUserInfo(UserInfo userInfo) {
        //用户编号
        if(StringUtils.isBlank(userInfo.getStudentNo()) || !StringUtils.isNumeric(userInfo.getStudentNo())){
            LOG.error("用户编号错误,{}",userInfo.getStudentNo());
            return false;
        }
        UserInfo verifyUser = selectUserInfo(userInfo.getStudentNo());
        if(verifyUser!=null){
            LOG.error("用户已存在,{}",userInfo.getStudentNo());
            return false;
        }
        //用户名
        if(StringUtils.isBlank(userInfo.getName())){
            LOG.error("用户名为空");
            return false;
        }
        //密码
        if(StringUtils.isBlank(userInfo.getPassword())){
            LOG.error("用户密码为空");
            return false;
        }
        //电话
        //默认状态
        userInfo.setStatus(new Short("0"));
        return userInfoMapper.insertSelective(userInfo)>0?true:false;
    }

    /**
     * 根据用户编号查询用户
     * @param userId
     * @return
     */
    private UserInfo selectUserInfo(String userId){
        if(StringUtils.isBlank(userId)){
            LOG.warn("用户编号为空");
            return null;
        }
        UserInfoExample example = new UserInfoExample();
        UserInfoExample.Criteria criteria = example.createCriteria();
        criteria.andStudentNoEqualTo(userId);
        List<UserInfo> userInfoList = userInfoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(userInfoList)){
            return null;
        }
        return userInfoList.get(0);
    }

    @Override
    public boolean modifyUserInfo(UserInfo userInfo) {
        //用户编号不能修改
        if(userInfo.getId()==null|| userInfo.getId()<1){
            LOG.error("用户id无效");
            return false;
        }
        userInfo.setStudentNo(null);
        return userInfoMapper.updateByPrimaryKeySelective(userInfo)>0?true:false;
    }

    @Override
    public UserInfo queryUserInfo(int userId) {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        return userInfo;
    }

    @Override
    public PageInfo<UserInfo> selectUserInfo(UserRequest user) {
        UserInfoExample example = new UserInfoExample();
        UserInfoExample.Criteria criteria = example.createCriteria();
        //用户编号
        if(StringUtils.isNotBlank(user.getUserId())){
            criteria.andStudentNoEqualTo(user.getUserId());
        }
        //用户名
        if(StringUtils.isNotBlank(user.getUserName())){
            criteria.andNameLike("*"+user.getUserName()+"*");
        }
        //用户类型
        if(user.getType()!=null){
            criteria.andRoleEqualTo(user.getType().shortValue());
        }
        //用户状态
        if(user.getStatus()!=null){
            criteria.andStatusEqualTo(user.getStatus().shortValue());
        }
        PageHelper.startPage(user.getPage(),user.getPageSize());
        List<UserInfo> userInfoList = userInfoMapper.selectByExample(example);
        return new PageInfo<>(userInfoList);
    }

    @Override
    public boolean verify(String userId) {
        if(StringUtils.isBlank(userId)){
            LOG.error("用户编号为空");
            return false;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setStudentNo(userId);
        userInfo.setStatus(new Short("1"));
        UserInfoExample example = new UserInfoExample();
        UserInfoExample.Criteria criteria = example.createCriteria();
        criteria.andStudentNoEqualTo(userId);
        return userInfoMapper.updateByExampleSelective(userInfo,example)>0?true:false;
    }

    @Override
    public boolean verify(List<String> userIdList) {
        if(CollectionUtils.isEmpty(userIdList)){
            LOG.error("带审核的用户为空");
            return false;
        }
        int counter = 0;
        List<String> failList = Lists.newArrayList();
        for(String userId:userIdList){
            if(verify(userId)){
                counter++;
                failList.add(userId);
            }else {
                LOG.error("用户:{},审核失败",userId);
            }
        }
        if(userIdList.size()!=counter){
            LOG.error("部分用户审核失败,总量:{},审核数量:{},明细:{}",userIdList.size(),counter, Joiner.on(",").join(failList));
        }
        return true;
    }

    @Override
    public boolean validUser(String studentNo) {
        if(StringUtils.isBlank(studentNo)){
            LOG.error("校验的用户编号为空");
            return true;
        }
        UserInfoExample example = new UserInfoExample();
        example.createCriteria().andStudentNoEqualTo(studentNo);
        List<UserInfo> list = userInfoMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list);
    }
}
