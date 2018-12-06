package com.here.service;

import com.github.pagehelper.PageInfo;
import com.here.entity.SysInfo;
import com.here.entity.vo.request.SysInfoRequest;

import java.util.List;

public interface SysInfoService {
    /**
     * 新增系统信息
     * @param sysInfo
     * @return
     */
    boolean saveSysInfo(SysInfo sysInfo);

    /**
     * 修改系统信息
     * @param sysInfo
     * @return
     */
    boolean modifySysInfo(SysInfo sysInfo);

    /**
     * 分页查询
     * @param request
     * @return
     */
    PageInfo<SysInfo> selectSysInfoList(SysInfoRequest request);

    /**
     * 获取所有系统设置
     * @return
     */
    List<SysInfo> getSysInfoList();

    /**
     * 获取本机系统设置
     * @return
     */
    SysInfo getLocalSysInfo();
}
