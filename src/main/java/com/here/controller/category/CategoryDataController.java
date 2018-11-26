package com.here.controller.category;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.here.entity.Category;
import com.here.entity.vo.response.BaseResponse;
import com.here.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CategoryDataController {
    private final static Logger LOG = LoggerFactory.getLogger(CategoryDataController.class);
    @Autowired
    private CategoryService categoryService;
    @RequestMapping(value = "/admin/getCatList")
    public List<Category> getCategoryList(HttpServletRequest request,Category parent){
        int parentId = 0;
        if(parent!=null && parent.getId()!=null){
            LOG.info("查询子类别");
            parentId = parent.getId();
        }
        final List<Category> categories = categoryService.getAllCategory(parentId);
        if(!CollectionUtils.isEmpty(categories)){
            List<Category> categoryList = Lists.transform(categories, new Function<Category, Category>() {
                @Override
                public Category apply(Category category) {
                    category.setText(category.getName());
                    return category;
                }
            });
            return categoryList;
        }
        return categories;
    }

    /**
     * 校验分类名称是否可用
     * @param request
     * @param name
     * @return
     */
    @RequestMapping(value = "/admin/validName")
    public BaseResponse validName(HttpServletRequest request,String name){
        if(StringUtils.isBlank(name)){
            LOG.error("分类名称不能为空");
            return BaseResponse.newErrorResponse(100,"分类名称不能为空");
        }
        Category category = categoryService.getCategoryByName(name);
        if(category!=null){
            return BaseResponse.newErrorResponse(101,"分类名已存在");
        }
        return BaseResponse.newResponseInstance(null);
    }

    /**
     * 修改分类名
     * @param request
     * @param category
     * @return
     */
    @RequestMapping(value = "/admin/editCategory")
    public BaseResponse editCategory(HttpServletRequest request,@RequestBody Category category){
        if(category==null || category.getId()==null){
            LOG.error("没有找到有效的分类信息");
            return BaseResponse.newErrorResponse(100,"分类id为空");
        }
        if(StringUtils.isBlank(category.getName())){
            LOG.error("分类名称为空,{}",category.getId());
            return BaseResponse.newErrorResponse(101,"分类名称不能为空");
        }
        categoryService.modifyCategory(category);
        return BaseResponse.newResponseInstance(null);
    }

    /**
     * 查看是否可以删除子类
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = "/admin/validDeleteCategory")
    public BaseResponse validDelCategory(HttpServletRequest request,Integer id){
        if(id==null || id<1){
            LOG.error("待删除的类别id为空,{}",id);
            return BaseResponse.newErrorResponse(100,"待删除的类别id为空");
        }
        List<Category> categoryList = categoryService.getAllCategory(id);
        if(CollectionUtils.isEmpty(categoryList)){
            LOG.error("子类不为空，不能删除,{}",id);
            return BaseResponse.newErrorResponse(200,"请先删除子类");
        }
        return BaseResponse.newResponseInstance(null);
    }

    /**
     * 删除分类
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = "/admin/deleteCategory")
    public BaseResponse deleteCategory(HttpServletRequest request, Integer id){
        if(id==null || id<1){
            LOG.error("无法删除分类,{}",id);
            return BaseResponse.newErrorResponse(100,"删除的分类id无效");
        }
        boolean del = categoryService.deleteCategory(id);
        if(del){
            return BaseResponse.newResponseInstance(del);
        } else {
            return BaseResponse.newErrorResponse(300,"删除失败,请稍后重试");
        }
    }

    /**
     * 新增分类
     * @param request
     * @param category
     * @return
     */
    @RequestMapping(value = "/admin/addCategory")
    public BaseResponse addCategory(HttpServletRequest request,@RequestBody Category category){
        if(category==null){
            LOG.error("分类信息为空");
            return BaseResponse.newErrorResponse(100,"分类信息为空");
        }
        if(StringUtils.isBlank(category.getName())){
            LOG.error("分类名称为空");
            return BaseResponse.newErrorResponse(101,"分类名不能为空");
        }
        if(category.getParent()==null || category.getParent()<1){
            LOG.error("父类不能为空,{},{}",category.getName(),category.getParent());
            return BaseResponse.newErrorResponse(102,"父类不能为空");
        }
        Integer flag = categoryService.saveCategory(category);
        return BaseResponse.newResponseInstance(flag);
    }
}
