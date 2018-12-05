package com.here.entity.vo.response;

public class ExamResponse {
    private Integer examLogId;
    private String score;
    private String errorMsg;

    public Integer getExamLogId() {
        return examLogId;
    }

    public void setExamLogId(Integer examLogId) {
        this.examLogId = examLogId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
