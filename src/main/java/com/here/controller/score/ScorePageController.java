package com.here.controller.score;

import com.github.pagehelper.PageInfo;
import com.here.entity.Score;
import com.here.entity.vo.request.ScoreRequest;
import com.here.service.ScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ScorePageController {
    private final static Logger LOG = LoggerFactory.getLogger(ScorePageController.class);
    @Autowired
    private ScoreService scoreService;

    @RequestMapping(value = "/admin/score.html")
    public ModelAndView scorePage(){
        ModelAndView mav = new ModelAndView();
        //查询成绩列表
        List<Score> scoreList = getScoreList();
        mav.addObject("listScore",scoreList);
        mav.setViewName("_admin/scoreList");
        return mav;
    }

    private List<Score> getScoreList(){
        ScoreRequest request = new ScoreRequest();
        request.setPage(1);
        request.setPageSize(1000);
        PageInfo<Score> pageInfo = scoreService.selectScoreList(request);
        return pageInfo.getList();
    }
}
