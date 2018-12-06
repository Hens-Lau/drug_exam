package com.here.controller.user;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.here.entity.*;
import com.here.entity.vo.request.UserRequest;
import com.here.service.ExamInfoService;
import com.here.service.ExamPaperService;
import com.here.service.SysInfoService;
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
    @Autowired
    private ExamPaperService examPaperService;
    @Autowired
    private ExamInfoService examInfoService;
    @Autowired
    private SysInfoService sysInfoService;

    @RequestMapping(value = "/admin/index.html")
    public ModelAndView adminIndex(HttpServletRequest req, HttpServletResponse resp){
        LOG.info("请求admin试图页面");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("_admin/index");
        return mav;
    }

    @RequestMapping(value = "/user/exam.html")
    public ModelAndView userIndex(HttpServletRequest request,HttpServletResponse response){
        Integer examId = 6;
        ModelAndView mav = new ModelAndView();
        //查询考卷信息,TODO 动态传参
        ExamPaper examPaper = examPaperService.selectExamPaper(examId);
        mav.addObject("exam",examPaper);
        //查询考题信息
        List<QuestionWithBLOBs> questionList = examInfoService.selectExamQuestionList(examId);
        //考题数
        mav.addObject("total",questionList.size());
        //题型,1:单选,2:多选,3:填空,4:判断,5:综合
        //单选
        List<QuestionWithBLOBs> sQuestionList = Lists.newArrayList(Collections2.filter(questionList, new Predicate<QuestionWithBLOBs>() {
            public boolean apply(QuestionWithBLOBs question) {
                return question.getType()==1;
            }
            public boolean test(QuestionWithBLOBs question){
                return apply(question);
            }
        }));
        mav.addObject("single",sQuestionList);
        //多选
        List<QuestionWithBLOBs> mQuestonList = Lists.newArrayList(Collections2.filter(questionList, new Predicate<QuestionWithBLOBs>() {
            public boolean apply(QuestionWithBLOBs question) {
                return question.getType()==2;
            }
            public boolean test(QuestionWithBLOBs question){
                return apply(question);
            }
        }));
        mav.addObject("multi",mQuestonList);
        //填空
        List<QuestionWithBLOBs> bQuestionList = Lists.newArrayList(Collections2.filter(questionList, new Predicate<QuestionWithBLOBs>() {
            public boolean apply(QuestionWithBLOBs question) {
                return question.getType()==3;
            }
            public boolean test(QuestionWithBLOBs question){
                return apply(question);
            }
        }));
        mav.addObject("blank",bQuestionList);
        //判断
        List<QuestionWithBLOBs> jQuestionList = Lists.newArrayList(Collections2.filter(questionList, new Predicate<QuestionWithBLOBs>() {
            public boolean apply(QuestionWithBLOBs question) {
                return question.getType()==4;
            }
            public boolean test(QuestionWithBLOBs question){
                return apply(question);
            }
        }));
        mav.addObject("judge",jQuestionList);
        //综合
        List<QuestionWithBLOBs> cQuestionList = Lists.newArrayList(Collections2.filter(questionList, new Predicate<QuestionWithBLOBs>() {
            public boolean apply(QuestionWithBLOBs question) {
                return question.getType()==5;
            }
            public boolean test(QuestionWithBLOBs question){
                return apply(question);
            }
        }));
        mav.addObject("comprehension",cQuestionList);
        //用户信息
        mav.addObject("userInfo",request.getSession().getAttribute("user"));
        //水印信息
        mav.addObject("mark",getSysMark());
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

    @RequestMapping(value = "/user/examList.html")
    public ModelAndView examList(HttpServletRequest request,HttpServletResponse response){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("_user/examList");
        return mav;
    }
    private List<UserInfo> getUserList(){
        UserRequest request = new UserRequest();
        request.setPage(1);
        request.setPageSize(1000);
        PageInfo<UserInfo> pageInfo = userInfoService.selectUserInfo(request);
        return pageInfo.getList();
    }

    private String getSysMark(){
        SysInfo sysInfo = sysInfoService.getLocalSysInfo();
        if(sysInfo==null){
            LOG.error("没有找到系统设置，使用默认值");
            return "佛山一中禁毒考试";
        }
        return sysInfo.getSysMark();
    }
}
