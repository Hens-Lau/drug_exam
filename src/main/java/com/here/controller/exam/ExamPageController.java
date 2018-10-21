package com.here.controller.exam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ExamPageController {
    private final static Logger LOG = LoggerFactory.getLogger(ExamPageController.class);

    @RequestMapping(value = "/admin/testPaper.html")
    public ModelAndView testPaper(){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("_admin/testPaper");
        return mav;
    }
}
