package com.here.entity.vo;

public class SysInfoRequest extends BaseRequest {
    private Integer id;
    private String mac;
    private String sysMark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSysMark() {
        return sysMark;
    }

    public void setSysMark(String sysMark) {
        this.sysMark = sysMark;
    }
}
