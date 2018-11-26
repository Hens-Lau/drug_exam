package com.here.service.impl;

import com.google.common.collect.Lists;
import com.here.dao.CategoryMapper;
import com.here.entity.Category;
import com.here.entity.CategoryExample;
import com.here.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final static Logger LOG = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public List<Category> getChildrenList(Integer id) {
        //查询自身序列
        Category cat = categoryMapper.selectByPrimaryKey(id);
        if(cat==null){
            LOG.warn("没有找到分类,{}",id);
            return Lists.newArrayList();
        }
        //根据自身查询子类
        String seq = cat.getSequence();
        if(StringUtils.isBlank(seq)){
            LOG.error("分类序列错误,{}",id);
            return Lists.newArrayList(cat);
        }
        CategoryExample example = new CategoryExample();
        CategoryExample.Criteria criteria = example.createCriteria();
        criteria.andSequenceLike(seq+"*");
        List<Category> categoryList = categoryMapper.selectByExample(example);
        return categoryList;
    }

    @Override
    public List<Category> getDirectChildren(Integer id) {
        CategoryExample example = new CategoryExample();
        CategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentEqualTo(id);
        List<Category> childrenList = categoryMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(childrenList)){
            return Lists.newArrayList();
        }
        return childrenList;
    }

    @Override
    public Category getCategoryByName(String name) {
        CategoryExample example = new CategoryExample();
        CategoryExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        List<Category> categories = categoryMapper.selectByExample(example);
        if(categories==null || categories.size()<1){
            return null;
        }
        if(categories.size()>1){
            LOG.error("存在同名的类别");
        }
        return categories.get(0);
    }

    @Override
    public List<Category> getCategoryListByName(String name) {
        CategoryExample example = new CategoryExample();
        CategoryExample.Criteria criteria = example.createCriteria();
        criteria.andNameLike("*"+name+"*");
        List<Category> categories = categoryMapper.selectByExample(example);
        return categories;
    }

    @Override
    public Integer saveCategory(Category category) {
        if(category==null || category.getParent()==null){
            LOG.error("父类不能为空");
            return null;
        }
        if(StringUtils.isBlank(category.getName())){
            LOG.error("类别名称不能为空");
            return null;
        }
        //查询父类id
        Category parentCat = getCategoryById(category.getParent());
        if(parentCat==null){
            LOG.error("没有找到父节点");
            return null;
        }
        int id = categoryMapper.insertSelective(category);
        if(id<1){
            LOG.error("新增类别失败");
            return null;
        }
        Category updateCat = new Category();
        updateCat.setId(id);
        updateCat.setSequence(parentCat.getSequence()+id);
        categoryMapper.updateByPrimaryKeySelective(updateCat);
        return id;
    }

    @Override
    public boolean modifyCategory(Category category) {
        if(category==null || category.getId()==null || category.getId()<1){
            LOG.error("类别不能为空");
            return false;
        }
        Category mCat = new Category();
        mCat.setId(category.getId());
        mCat.setName(category.getName());
        return categoryMapper.updateByPrimaryKeySelective(mCat)>0?true:false;
    }

    @Override
    public boolean deleteCategory(Integer id) {
        return categoryMapper.deleteByPrimaryKey(id)>0?true:false;
    }

    @Override
    public List<Category> getAllCategory(Integer id) {
        return categoryMapper.getAllCategory(id);
    }

    private Category getParent(Integer parentId){
        CategoryExample example = new CategoryExample();
        CategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentEqualTo(parentId);
        List<Category> parentList = categoryMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(parentList)){
            return null;
        }
        return parentList.get(0);
    }

    private Category getCategoryById(Integer id){
        return categoryMapper.selectByPrimaryKey(id);
    }
}
