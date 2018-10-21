package com.here.controller.score;

import com.github.pagehelper.PageInfo;
import com.here.entity.Score;
import com.here.entity.vo.ScoreRequest;
import com.here.service.ScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ScoreDataController {
    private final static Logger LOG = LoggerFactory.getLogger(ScoreDataController.class);
    @Autowired
    private ScoreService scoreService;

    /**
     * 分页查询分数
     * @param request
     * @param scoreRequest
     * @return
     */
    @RequestMapping(value = "/admin/getAllScore")
    public PageInfo<Score> getUserScoreList(HttpServletRequest request, @RequestBody ScoreRequest scoreRequest){
        if(scoreRequest==null){
            LOG.warn("分数查询对象为空");
            scoreRequest = new ScoreRequest();
        }
        return scoreService.selectScoreList(scoreRequest);
    }

    /**
     * 查询单条得分
     * @param request
     * @param scoreRequest
     * @return
     */
    @RequestMapping(value = "/user/getScore")
    public Score getUserScore(HttpServletRequest request,@RequestBody ScoreRequest scoreRequest){
        if(scoreRequest==null || scoreRequest.getId()==null){
            LOG.error("查询条件错误");
            return null;
        }
        return scoreService.selectScore(scoreRequest.getId());
    }

}
