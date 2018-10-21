package com.here.service;

import com.github.pagehelper.PageInfo;
import com.here.entity.UserInfo;
import com.here.entity.vo.UserRequest;

import java.util.List;

public interface UserInfoService {
    /**
     * 查询用户类型
     * @param userInfo
     * @return
     */
    Short queryUserType(UserInfo userInfo);

    /**
     * 新增用户
     * @param userInfo
     */
    boolean addUserInfo(UserInfo userInfo);

    /**
     * 修改用户信息
     * @param userInfo
     * @return
     */
    boolean modifyUserInfo(UserInfo userInfo);

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    UserInfo queryUserInfo(int userId);

    /**
     * 分页查询用户列表
     * @param user
     * @return
     */
    PageInfo<UserInfo> selectUserInfo(UserRequest user);

    /**
     * 审核用户
     * @param userId
     * @return
     */
    boolean verify(String userId);

    /**
     * 批量审核
     * @param userIdList
     * @return
     */
    boolean verify(List<String> userIdList);
}
