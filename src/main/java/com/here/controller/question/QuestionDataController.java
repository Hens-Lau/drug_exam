package com.here.controller.question;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.here.entity.QuestionWithBLOBs;
import com.here.entity.vo.ReportInternalException;
import com.here.entity.vo.request.QuestionRequest;
import com.here.entity.vo.response.BaseResponse;
import com.here.service.QuestionService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class QuestionDataController {
    private final static Logger LOG = LoggerFactory.getLogger(QuestionDataController.class);
    private final static Integer DEFAULT_PAGE=1;
    private final static Integer DEFAULT_PAGE_SIZE=20;
    @Autowired
    private QuestionService questionService;

    @RequestMapping(value = "/admin/getAllQuestionBank")
    public PageInfo<QuestionWithBLOBs> queryQuestionInfo(HttpServletRequest request,@RequestBody QuestionRequest questionRequest){
        if(questionRequest==null){
            return new PageInfo<>();
        }
        if(questionRequest.getPage()==null){
            questionRequest.setPage(DEFAULT_PAGE);
        }
        if(questionRequest.getPageSize()==null){
            questionRequest.setPageSize(DEFAULT_PAGE_SIZE);
        }
        return questionService.selectQuestionList(questionRequest);
    }

    @RequestMapping(value = "/admin/addquestionbank")
    public Integer addQuestion(HttpServletRequest request,@RequestBody QuestionWithBLOBs question){
        if(question==null){
            LOG.error("数据不能为空");
            return -1;
        }
        //处理选择题选项
        if(question.getType()==1 || question.getType()==2){
            String options = trans2Json(question.getOptionList());
            LOG.info("新增的选择题选项内容:{}",options);
            question.setOptions(options);
        }
        return questionService.addQuestion(question);
    }

    @RequestMapping(value = "/admin/updatequestionbank")
    public boolean modifyQuestion(HttpServletRequest request, @RequestBody QuestionWithBLOBs question){
        if(question==null ||question.getId()==null){
            LOG.error("修改的问题id有误");
            return false;
        }
        return questionService.modifyQuestion(question);
    }

    @RequestMapping(value = "/admin/deletequestionbank")
    public boolean delete(HttpServletRequest request, @RequestBody QuestionWithBLOBs question){
        if(question==null || CollectionUtils.isEmpty(question.getIdList())){
            LOG.error("无法删除问题，id为空");
            return false;
        }
        return questionService.deleteQuestion(question.getIdList());
    }

    @RequestMapping(value = "/admin/importQuestion")
    @ResponseBody
    public BaseResponse importQuestion(@RequestParam(value = "filename")MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        String fileName = file.getOriginalFilename();
        String message = questionService.importQuestion(fileName,file);
        return BaseResponse.newResponseInstance(message);
    }

    @RequestMapping(value = "/admin/exportQuestion")
    public ResponseEntity<byte[]> exportQuestion(HttpServletRequest request, QuestionRequest questionRequest) throws IOException, ReportInternalException {
        String filePath = questionService.exportQuestion(questionRequest);
        File file = new File(filePath);
        HttpHeaders headers = new HttpHeaders();
        String downName = new String("考题.xlsx".getBytes("UTF-8"),"iso-8859-1");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",downName);
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file),headers, HttpStatus.CREATED);
    }

    private String trans2Json(List<String> optionList){
        if(CollectionUtils.isEmpty(optionList)){
            return null;
        }
        JSONObject obj = new JSONObject();
        obj.put("A",optionList.get(0));
        if(optionList.size()>1){
            obj.put("B",optionList.get(1));
        } else {
            obj.put("B",null);
        }
        if(optionList.size()>2){
            obj.put("C",optionList.get(2));
        } else {
            obj.put("C",null);
        }
        if(optionList.size()>3){
            obj.put("D",optionList.get(3));
        } else {
            obj.put("D",null);
        }
        return obj.toJSONString();
    }
}
