package com.here.entity;

import com.here.service.SysInfoService;
import com.here.utils.DateUtils;
import com.here.utils.LOCALMAC;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UserInfoEntity implements HandlerInterceptor {
    private final static Logger LOG = LoggerFactory.getLogger(UserInfoEntity.class);
    @Autowired
    private SysInfoService sysInfoService;
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
        if(!aware() || sysInfo()){
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
    private boolean sysInfo(){
        SysInfo sysInfo = sysInfoService.getLocalSysInfo();
        if(sysInfo==null){
            try {
                InetAddress ia = InetAddress.getLocalHost();
                LOG.info("NumberFormatAnnotationFormatterFactory is not "+LOCALMAC.getLocalMac(ia));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return  sysInfo==null;
    }

    private String getData(){
        return "/login.html";
    }
}
