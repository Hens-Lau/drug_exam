package com.here.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.here.dao.QuestionMapper;
import com.here.entity.Question;
import com.here.entity.QuestionExample;
import com.here.entity.QuestionWithBLOBs;
import com.here.entity.vo.ReportInternalException;
import com.here.entity.vo.request.QuestionRequest;
import com.here.service.QuestionService;
import com.here.utils.Excel2007Utils;
import com.here.utils.PoiUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Service
public class QuestionSeriviceImpl implements QuestionService {
    private final static Logger LOG = LoggerFactory.getLogger(QuestionSeriviceImpl.class);
    private final static String TEMP_PATH = "./temp/export/";
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public PageInfo<QuestionWithBLOBs> selectQuestionList(QuestionRequest questionRequest) {
        PageHelper.startPage(questionRequest.getPage(),questionRequest.getPageSize());
        if(questionRequest==null){
            LOG.error("分页问题查询参数不能为空");
            return null;
        }
        QuestionExample example = new QuestionExample();
        QuestionExample.Criteria criteria = example.createCriteria();
        if(questionRequest.getPageId()!=null){//编号
            criteria.andIdEqualTo(questionRequest.getPageId());
        }
        if(questionRequest.getCategoryId()!=null) {//问题类别
            criteria.andCatIdEqualTo(questionRequest.getCategoryId());
        }
        //问题关键字
        if(questionRequest.getLevel()!=null){
            criteria.andLevelEqualTo(questionRequest.getLevel().shortValue());
        }
        //问题类型
        if(questionRequest.getType()!=null){
            criteria.andTypeEqualTo(questionRequest.getType().shortValue());
        }
        List<QuestionWithBLOBs> questionWithBLOBsList = questionMapper.selectByExampleWithBLOBs(example);
        PageInfo<QuestionWithBLOBs> pageInfo = new PageInfo<>(questionWithBLOBsList);
//        pageInfo.setTotal(questionWithBLOBsList==null?0:questionWithBLOBsList.size());
        return pageInfo;
    }

    @Override
    public int addQuestion(QuestionWithBLOBs question) {
        if(question==null){
            LOG.error("数据不能为空");
            return -1;
        }
        if(StringUtils.isBlank(question.getTitle())){
            LOG.error("新增问题为空");
            return -1;
        }
        if(question.getType()==null){
            LOG.error("新增问题类型为空");
            return -1;
        }
        return questionMapper.insertSelective(question);
    }

    @Override
    public boolean modifyQuestion(QuestionWithBLOBs question) {
        if(question==null || question.getId()==null || question.getId()<1){
            LOG.error("修改问题id为空");
            return false;
        }
        if(StringUtils.isBlank(question.getTitle())){
            LOG.error("问题不能为空,{}",question.getId());
            return false;
        }
        if(question.getType()==null || question.getType()<1){
            LOG.error("问题类型不能为空,{}", question.getId());
            return false;
        }
        return questionMapper.updateByPrimaryKey(question)>0?true:false;
    }

    public boolean deleteQuestion(List<Integer> questionIdList){
        if(CollectionUtils.isEmpty(questionIdList)){
            LOG.error("问题id不能为空");
            return false;
        }
        QuestionExample example = new QuestionExample();
        QuestionExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(questionIdList);
        return questionMapper.deleteByExample(example)>0?true:false;
    }

    public String importQuestion(String fileName, MultipartFile mFile){
        String tempFilePath = "./temp/import/";
        File uploadDir = new File(tempFilePath);
        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }
        File tempFile = new File(tempFilePath+fileName+"_"+new Date().getTime()+".xlsx");
        InputStream is = null;
        try {
            mFile.transferTo(tempFile);
            is = new FileInputStream(tempFile);
            Workbook wb = null;
            if(PoiUtils.isExcel2003(fileName)){
                wb = new HSSFWorkbook(is);
            } else if(PoiUtils.isExcel2007(fileName)){
                wb = new XSSFWorkbook(is);
            } else {
                LOG.error("上传的文件格式错误,{}",fileName);
                return "导入的excel错误!";
            }
            String singleError = readForSingleQuestion(wb);
            String multiError = readForMultiQuestion(wb);
            StringBuffer totalError = new StringBuffer();
            if(StringUtils.isNotBlank(singleError)){
                totalError.append("单选题错误:").append(singleError);
            }
            if(StringUtils.isNotBlank(multiError)){
                totalError.append("多选题错误:").append(multiError);
            }
            return totalError.toString();
        } catch (IOException e) {
            LOG.error("上传excel异常",e);
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.error("关闭导入文件异常",e);
                }
            }
        }
        return null;
    }

    @Override
    public String exportQuestion(QuestionRequest questionRequest) throws ReportInternalException, IOException {
        //根据条件查询结果
        List<QuestionWithBLOBs> questionList = queryQuestionList(questionRequest);
        //将结果转换为Object集合
        List<List<Object>> exportData = Lists.transform(questionList, new Function<QuestionWithBLOBs, List<Object>>() {
            @Override
            public List<Object> apply(QuestionWithBLOBs question) {
                return trans2Columns(question);
            }
        });
        //导出excel
        List<String> columnNames = getColumnNames();
        String fileName = System.currentTimeMillis()+"";
        String sheetName = "考题";
        String excelTitle = "佛山第一中学戒毒考题";
        Excel2007Utils.writeExcel(TEMP_PATH,fileName,sheetName,columnNames,excelTitle,exportData,false);
        //返回excel路径
        return TEMP_PATH+fileName+".xlsx";
    }

    /**
     * 导出的列名
     * @return
     */
    private List<String> getColumnNames(){
        List<String> colNames = Lists.newArrayList();
        colNames.add("题型");
        colNames.add("题目");
        colNames.add("答案");
        colNames.add("选项A");
        colNames.add("选项B");
        colNames.add("选项C");
        colNames.add("选项D");
        return colNames;
    }

    /**
     * 将对象转换成列
     * @param question
     * @return
     */
    private List<Object> trans2Columns(QuestionWithBLOBs question){
        List<Object> colList = Lists.newArrayList();
        //题型
        Short type = question.getType();
        String typeName = null;//1:单选,2:多选,3:填空,4:判断,5:综合
        if(type==null){
            typeName="未知";
        } else if(type.intValue()==1){
            typeName="单选";
        } else if(type.intValue()==2){
            typeName="多选";
        } else if(type.intValue()==3){
            typeName="填空";
        } else if(type.intValue()==4){
            typeName="判断";
        } else if(type.intValue()==5){
            typeName="综合";
        } else {
            typeName="其他-"+type;
        }
        colList.add(typeName);
        //题目
        String title = question.getTitle();
        colList.add(title);
        //答案
        String answer = question.getAnswer();
        colList.add(answer);
        //拆解选项
        String option = (String) question.getOptions();
        if(StringUtils.isBlank(option)){
            colList.add("");
            colList.add("");
            colList.add("");
            colList.add("");
        } else {
            JSONObject obj = JSONObject.parseObject(option);
            colList.add(obj.get("A"));
            colList.add(obj.get("B"));
            colList.add(obj.get("C"));
            colList.add(obj.get("D"));
        }
        return colList;
    }

    /**
     * 查询需要导出的数据
     * @param questionRequest
     * @return
     */
    private List<QuestionWithBLOBs> queryQuestionList(QuestionRequest questionRequest){
        QuestionExample example = new QuestionExample();
        QuestionExample.Criteria criteria = example.createCriteria();
        if(questionRequest.getPageId()!=null){//编号
            criteria.andIdEqualTo(questionRequest.getPageId());
        }
        if(questionRequest.getCategoryId()!=null) {//问题类别
            criteria.andCatIdEqualTo(questionRequest.getCategoryId());
        }
        //问题关键字
        if(questionRequest.getLevel()!=null){
            criteria.andLevelEqualTo(questionRequest.getLevel().shortValue());
        }
        //问题类型
        if(questionRequest.getType()!=null){
            criteria.andTypeEqualTo(questionRequest.getType().shortValue());
        }
        List<QuestionWithBLOBs> questionWithBLOBsList = questionMapper.selectByExampleWithBLOBs(example);
        return questionWithBLOBsList;
    }

    /**
     * 处理单选题
     * @param workbook
     * @return
     */
    private String readForSingleQuestion(Workbook workbook){
        StringBuffer errorMsg = new StringBuffer();
        Sheet sheet = workbook.getSheetAt(0);
        int totalRows = sheet.getPhysicalNumberOfRows();
        if(totalRows<2){
            errorMsg.append("单选题信息缺失");
            return errorMsg.toString();
        }
        //读取单选内容
        List<QuestionWithBLOBs> list = train2Entity(sheet,totalRows,errorMsg,true);
        for(QuestionWithBLOBs questionWithBLOBs: list){
            questionMapper.insertSelective(questionWithBLOBs);
        }
        LOG.info("共导入单选题:{}道",list.size());
        return errorMsg.toString();
    }


    private List<QuestionWithBLOBs> train2Entity(Sheet sheet,int totalRows,StringBuffer errorMsg,boolean isSingleQuestion){
        List<QuestionWithBLOBs> list = Lists.newArrayList();
        for(int i=1;i<totalRows;i++){
            Row row = sheet.getRow(i);
            if(row==null){
//                errorMsg.append("第").append(i+1).append("行数据为空!");
                LOG.error("导入的第{}行数据为空,题型:{}",i+1,isSingleQuestion);
                continue;
            } else {
                QuestionWithBLOBs questionWithBLOBs = trans2Entity(row,isSingleQuestion);
                if(questionWithBLOBs==null){
                    LOG.error("导入的第{}行为空白数据,题型:{}",i+1,isSingleQuestion);
                    continue;
                } else if(StringUtils.isBlank(questionWithBLOBs.getTitle())) {
                    errorMsg.append("第").append(i+1).append("行数据题干错误!");
                    continue;
                }
                list.add(questionWithBLOBs);
            }
        }
        return list;
    }

    /**
     * 转换成考题
     * @param row
     * @param isSingleQuestion
     * @return
     */
    private QuestionWithBLOBs trans2Entity(Row row, boolean isSingleQuestion){
        QuestionWithBLOBs question = new QuestionWithBLOBs();
        StringBuffer line = new StringBuffer();
        //题干
        String title = PoiUtils.getCellStringValue(row.getCell(0));
        question.setTitle(title);
        line.append(title);
        //答案
        String answer = PoiUtils.getCellStringValue(row.getCell(1));
        question.setAnswer(answer);
        line.append(answer);
        //A选项
        String options1 = PoiUtils.getCellStringValue(row.getCell(2));
        options1 = StringUtils.replaceOnce(options1,"A.","");
        line.append(options1);
        //B选项
        String option2 = PoiUtils.getCellStringValue(row.getCell(3));
        option2 = StringUtils.replaceOnce(option2,"B.","");
        line.append(option2);
        //C选项
        String option3 = PoiUtils.getCellStringValue(row.getCell(4));
        option3 = StringUtils.replaceOnce(option3,"C.","");
        line.append(option3);
        //D选项
        String option4 = PoiUtils.getCellStringValue(row.getCell(5));
        option4 = StringUtils.replaceOnce(option4,"D.","");
        line.append(option4);
        if(StringUtils.isBlank(line)){
            LOG.error("导入的数据包括空白行");
            return null;
        }
        if(StringUtils.isBlank(title)){
            LOG.error("题干为空,{}",isSingleQuestion);
            return question;
        }
        JSONObject obj = new JSONObject();
        obj.put("A",options1);
        obj.put("B",option2);
        obj.put("C",option3);
        obj.put("D",option4);
        question.setOptions(obj.toJSONString());
        if(isSingleQuestion){
            question.setType(new Short("1"));
        } else {
            question.setType(new Short("2"));
        }
        return question;
    }

    /**
     * 处理多选题
     * @param wb
     * @return
     */
    private String readForMultiQuestion(Workbook wb){
        StringBuffer errorMsg = new StringBuffer();
        Sheet sheet = wb.getSheetAt(1);
        int totalRows = sheet.getPhysicalNumberOfRows();
        if(totalRows<2){
            errorMsg.append("多选题信息缺失");
            return errorMsg.toString();
        }
        //读取多选题
        List<QuestionWithBLOBs> list = train2Entity(sheet,totalRows,errorMsg,false);
        for(QuestionWithBLOBs questionWithBLOBs: list){
            questionMapper.insertSelective(questionWithBLOBs);
        }
        LOG.info("共导入多选题:{}道",list.size());
        return errorMsg.toString();
    }
}
