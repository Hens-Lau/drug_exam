package com.here.service;

import com.github.pagehelper.PageInfo;
import com.here.entity.Score;
import com.here.entity.vo.ScoreRequest;

public interface ScoreService {
    /**
     * 分数查询
     * @param scoreRequest
     * @return
     */
    PageInfo<Score> selectScoreList(ScoreRequest scoreRequest);

    /**
     * 修改分数
     * @param score
     * @return
     */
    boolean modifyScore(Score score);

    /**
     * 新增分数
     * @param score
     * @return
     */
    boolean saveScore(Score score);

    /**
     * 查询单条得分
     * @param id
     * @return
     */
    Score selectScore(Integer id);
}
