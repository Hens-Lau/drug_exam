package com.here.controller.question;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class QuestionPageController {
    private final static Logger LOG = LoggerFactory.getLogger(QuestionPageController.class);

    @RequestMapping(value = "/admin/question-bank.html")
    public ModelAndView questionBank(){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("_admin/question-bank");
        return mav;
    }
}
