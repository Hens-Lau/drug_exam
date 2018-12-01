package com.here.controller.user;

import com.github.pagehelper.PageInfo;
import com.here.entity.UserInfo;
import com.here.entity.vo.request.UserRequest;
import com.here.service.UserInfoService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class UserInfoPageController {
    private final static Logger LOG = LoggerFactory.getLogger(UserInfoPageController.class);

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping(value = "/admin/index.html")
    public ModelAndView adminIndex(HttpServletRequest req, HttpServletResponse resp){
        LOG.info("请求admin试图页面");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("_admin/index");
        return mav;
    }

    @RequestMapping(value = "/user/exam.html")
    public ModelAndView userIndex(HttpServletRequest request,HttpServletResponse response){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("_user/exam");
        return mav;
    }

    @RequestMapping(value = "/admin/user.html")
    public ModelAndView userInfo(HttpServletRequest req, HttpServletResponse resp){
        ModelAndView mav = new ModelAndView();
        //查询用户信息
        List<UserInfo> userInfoList = getUserList();
        mav.addObject("allUsers",userInfoList);
//        mav.setViewName("_admin/user");
        mav.setViewName("_admin/userList");
        return mav;
    }

    @RequestMapping(value="/admin/register.html")
    public ModelAndView register(HttpServletRequest request,HttpServletResponse response){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("_admin/register");
        return mav;
    }

    private List<UserInfo> getUserList(){
        UserRequest request = new UserRequest();
        request.setPage(1);
        request.setPageSize(1000);
        PageInfo<UserInfo> pageInfo = userInfoService.selectUserInfo(request);
        return pageInfo.getList();
    }
}
