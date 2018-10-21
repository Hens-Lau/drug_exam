package com.here.controller.user;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.here.entity.UserInfo;
import com.here.entity.vo.UserRequest;
import com.here.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class UserInfoDataController {
    private final static Logger LOG = LoggerFactory.getLogger(UserInfoDataController.class);
    @Autowired
    private UserInfoService userInfoService;

    @ResponseBody
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(HttpServletRequest request, UserInfo userInfo){
        HttpSession session = request.getSession(false);
        if(session==null || !StringUtils.equals((String)session.getAttribute("code"),userInfo.getCode())){
            return "0";
        }
        Short type = userInfoService.queryUserType(userInfo);
        if(type<0){
            return "0";
        }
        return type.toString();
    }

    /**
     * 分页查询用户列表
     * @param request
     * @param userInfo
     * @return
     */
    @RequestMapping(value = "/admin/getUserList")
    public PageInfo<UserInfo> getAllUserInfo(HttpServletRequest request, @RequestBody UserRequest userInfo){
        if(userInfo==null){
            LOG.warn("查询条件不能为空");
            userInfo = new UserRequest();
        }
        return userInfoService.selectUserInfo(userInfo);
    }

    /**
     * 审核单个用户
     * @param request
     * @param userRequest
     * @return
     */
    @RequestMapping(value = "/admin/verifyUserInfo")
    public boolean verifyUser(HttpServletRequest request, @RequestBody UserRequest userRequest){
        if(userRequest==null || StringUtils.isBlank(userRequest.getUserId())){
            LOG.error("无法确认审核用户");
            return false;
        }
        return userInfoService.verify(userRequest.getUserId());
    }

    /**
     * 用户批量审核
     * @param request
     * @param userIdList
     * @return
     */
    @RequestMapping(value = "/admin/batchVerifyUser")
    public boolean verifyUser(HttpServletRequest request,List<String> userIdList){
        if(CollectionUtils.isEmpty(userIdList)){
            LOG.error("审核失败,用户列表为空");
            return false;
        }
        return userInfoService.verify(userIdList);
    }
}
