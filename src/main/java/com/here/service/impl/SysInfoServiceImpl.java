package com.here.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.here.dao.SysInfoMapper;
import com.here.entity.SysInfo;
import com.here.entity.SysInfoExample;
import com.here.entity.vo.request.SysInfoRequest;
import com.here.service.SysInfoService;
import com.here.utils.LOCALMAC;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

@Service
public class SysInfoServiceImpl implements SysInfoService {
    private final static Logger LOG = LoggerFactory.getLogger(SysInfoServiceImpl.class);
    @Autowired
    private SysInfoMapper sysInfoMapper;

    @Override
    public boolean saveSysInfo(SysInfo sysInfo) {
        //mac地址不能为空
        if(StringUtils.isBlank(sysInfo.getMac())){
            LOG.error("mac地址不能为空");
            return false;
        }
        //水印不能为空
        if(StringUtils.isBlank(sysInfo.getSysMark())){
            LOG.error("水印不能为空");
            return false;
        }
        return sysInfoMapper.insertSelective(sysInfo)>0?true:false;
    }

    @Override
    public boolean modifySysInfo(SysInfo sysInfo) {
        if(sysInfo.getId()==null || sysInfo.getId()<1){
            LOG.error("系统信息主键为空");
            return false;
        }
        if(sysInfo.getMac()!=null && StringUtils.isBlank(sysInfo.getMac())){
            LOG.error("mac地址不能为空");
            return false;
        }
        return sysInfoMapper.updateByPrimaryKeySelective(sysInfo)>0?true:false;
    }

    @Override
    public PageInfo<SysInfo> selectSysInfoList(SysInfoRequest request) {
        SysInfoExample example = new SysInfoExample();
        SysInfoExample.Criteria criteria = example.createCriteria();
        //id
        if(request.getId()!=null){
            criteria.andIdEqualTo(request.getId());
        }
        //mac地址
        if(StringUtils.isNotBlank(request.getMac())){
            criteria.andMacEqualTo(request.getMac());
        }
        //水印
        if(StringUtils.isNotBlank(request.getSysMark())){
            criteria.andSysMarkLike("*"+request.getSysMark()+"*");
        }
        PageHelper.startPage(request.getPage(),request.getPageSize());
        List<SysInfo> sysInfoList = sysInfoMapper.selectByExample(example);
        PageInfo<SysInfo> pageInfo = new PageInfo<>(sysInfoList);
        return pageInfo;
    }

    @Override
    public List<SysInfo> getSysInfoList() {
        SysInfoExample example = new SysInfoExample();
        return sysInfoMapper.selectByExample(example);
    }

    @Override
    public SysInfo getLocalSysInfo() {
        InetAddress ia = null;
        String ip = null;
        try {
            ia = InetAddress.getLocalHost();
            ip = LOCALMAC.getLocalMac(ia);
            LOG.info("本机ip是,{}",ip);
        } catch (UnknownHostException e) {
            LOG.error("查询本机ip异常");
        } catch (SocketException e) {
            e.printStackTrace();
        }
        SysInfoExample example = new SysInfoExample();
        example.createCriteria().andMacEqualTo(ip);
        List<SysInfo> sysInfos = sysInfoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(sysInfos)){
            LOG.warn("系统设置为空");
            return null;
        }
        return sysInfos.get(0);
    }
}
