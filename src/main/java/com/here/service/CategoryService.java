package com.here.service;

import com.here.entity.Category;

import java.util.List;

public interface CategoryService {
    /**
     * 查询所有子类
     * @param id
     * @return
     */
    List<Category> getChildrenList(Integer id);

    /**
     * 获取直接子类
     * @param id
     * @return
     */
    List<Category> getDirectChildren(Integer id);

    /**
     * 根据名称进行查询
     * @param name
     * @return
     */
    Category getCategoryByName(String name);

    /**
     * 模糊查询
     * @param name
     * @return
     */
    List<Category> getCategoryListByName(String name);

    /**
     * 新增类别
     * @param category
     * @return
     */
    boolean saveCategory(Category category);

    /**
     * 修改类别-只能修改名称
     * @param category
     * @return
     */
    boolean modifyCategory(Category category);

    /**
     * 删除分类
     * @param id
     * @return
     */
    boolean deleteCategory(Integer id);
}
