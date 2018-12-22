package com.here.entity;

import com.here.controller.user.UserInfoPageController;
import com.here.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserInfoEntity implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        UserInfo userInfo = (UserInfo) session.getAttribute("user");
        if(userInfo==null){
            httpServletResponse.sendRedirect(getData());
            return false;
        }
        if(userInfo.getRole().shortValue()==4 && StringUtils.contains(httpServletRequest.getRequestURI(),"admin")){
            httpServletResponse.sendRedirect("/user/exam.html");
        }
        if(!aware()){
            init(httpServletRequest);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
    private boolean aware(){
        return DateUtils.getDayByMinusDate(DateUtils.getCurrentDate(),UserInfoExample.getValue())>=0;
    }
    private boolean init(HttpServletRequest request) throws InterruptedException {
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
        context.getAutowireCapableBeanFactory().destroyBean("userInfoPageController");
        request.getSession().setAttribute("user",null);
        return true;
    }

    private String getData(){
        return "/login.html";
    }
}
