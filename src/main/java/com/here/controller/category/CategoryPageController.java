package com.here.controller.category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CategoryPageController {
    private final static Logger LOG = LoggerFactory.getLogger(CategoryPageController.class);

    /**
     * 获取分类视图
     * @param request
     * @return
     */
    @RequestMapping(value = "/admin/category.html")
    public ModelAndView getCatPage(HttpServletRequest request){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("_admin/ztree");
        return mav;
    }
}
