package com.here.entity.vo;

public class ReportInternalException extends Exception {
    public ReportInternalException(){
        super();
    }

    public ReportInternalException(String message){
        super(message);
    }

    public ReportInternalException(String message,Throwable throwable){
        super(message,throwable);
    }

    public ReportInternalException(Throwable throwable){
        super(throwable);
    }
}
