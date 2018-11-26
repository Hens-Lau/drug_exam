package com.here.entity.vo.response;

/**
 * 基础返回信息
 */
public class BaseResponse {
    private int errorCode;
    private String errorMsg;
    private Object data;

    public BaseResponse() {

    }
    public BaseResponse(int errorCode, Object data) {
        this.errorCode = errorCode;
        this.data = data;
    }

    public BaseResponse(int errorCode, String errorMsg, Object obj) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.data = obj;
    }

    public static BaseResponse newErrorResponse(int errorCode,String errorMsg){
        return new BaseResponse(errorCode,errorMsg,null);
    }

    public static BaseResponse newResponseInstance(Object data){
        return new BaseResponse(0,data);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
