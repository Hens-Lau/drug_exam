package com.here.service;

import com.github.pagehelper.PageInfo;
import com.here.entity.ExamRoom;
import com.here.entity.vo.request.ExamRoomRequest;

public interface ExamRoomService {
    /**
     * 保存考场信息
     * @param examRoom
     * @return
     */
    boolean saveExamRoom(ExamRoom examRoom);

    /**
     * 修改考场信息
     * @param examRoom
     * @return
     */
    boolean modifyExamRoom(ExamRoom examRoom);

    /**
     * 分页查询考场信息
     * @param roomRequest
     * @return
     */
    PageInfo<ExamRoom> selectExamRoom(ExamRoomRequest roomRequest);

    /**
     * 查询考场信息
     * @param id
     * @return
     */
    ExamRoom selectExamRoom(Integer id);
}
