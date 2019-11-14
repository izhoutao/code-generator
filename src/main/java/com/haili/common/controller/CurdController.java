package com.haili.common.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haili.framework.model.response.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Cenyol mail: mr.cenyol@gmail.com
 * @date 2019-09-23 11:21
 * <p>
 * provide the basic curd method
 * include list/insert/view/update/delete
 */
@Slf4j
public class CurdController<T> {

    @Autowired
    private BaseMapper<T> mapper;

    /**
     * 在搜索的时候，去除这几个 map 参数
     */
    private String[] pageParams = {"size", "current", "orders"};

    /**
     * example:
     * {
     * "size": 15,            默认：10
     * "current": 1,          当前页码
     * "id": 1162,            SQL查询条件
     * "orders": ["id desc"]  排序条件，可以设置多个
     * }
     *
     * @param map
     * @return
     */
    @PostMapping(value = {"/listByPage", "/index", "/list"})
    @ResponseBody
    public QueryResponseResult listByPage(@RequestBody Map<String, Object> map) {
        IPage<T> iPage = mapper.selectPage(
                extractPageFromRequestMap(map),
                extractWrapperFromRequestMap(map));
        QueryResult<T> queryResult = new QueryResult<>();
        queryResult.setList(iPage.getRecords());
        queryResult.setTotal(iPage.getTotal());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    @PostMapping(value = {"/save", "/insert"})
    @ResponseBody
    public ModelResopnseResult<T> save(@RequestBody T map) {
        try {
            Date date = new Date();
            Class<?> clazz = map.getClass();
            Field createTimeField = clazz.getDeclaredField("createTime");
            createTimeField.setAccessible(true);
            createTimeField.set(map, date);
            Field updateTimeField = clazz.getDeclaredField("updateTime");
            updateTimeField.setAccessible(true);
            updateTimeField.set(map, date);
        } catch (Exception e) {
        }
        mapper.insert(map);

        return new ModelResopnseResult<T>(CommonCode.SUCCESS, map);
    }

    @GetMapping(value = {"/getById/{id}", "/get/{id}"})
    @ResponseBody
    public ModelResopnseResult<T> getById(@PathVariable("id") Long id) {
        T obj = mapper.selectById(id);
        return new ModelResopnseResult<T>(CommonCode.SUCCESS, obj);
    }

    @PostMapping(value = {"/updateById", "/update"})
    @ResponseBody
    public ResponseResult updateById(@RequestBody T map) {
        try {
            Date date = new Date();
            Class<?> clazz = map.getClass();
            Field updateTimeField = clazz.getDeclaredField("updateTime");
            updateTimeField.setAccessible(true);
            updateTimeField.set(map, date);
        } catch (Exception e) {
        }
        mapper.updateById(map);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @PostMapping(value = {"/deleteById", "/delete"})
    @ResponseBody
    public ResponseResult deleteById(@RequestBody Long id) {
        mapper.deleteById(id);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * 从请求体中提取查询参数
     *
     * @param map
     * @return
     */
    private QueryWrapper<T> extractWrapperFromRequestMap(Map<String, Object> map) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        for (String pageParam : pageParams) {
            map.remove(pageParam);
        }
        queryWrapper.allEq(map, false);
        return queryWrapper;
    }

    /**
     * 从请求体中提取分页参数
     *
     * @param map
     * @return
     */
    private Page<T> extractPageFromRequestMap(Map<String, Object> map) {

        Page<T> page = new Page<>();

        String key = pageParams[0];
        if (map.containsKey(key) && map.get(key) instanceof Integer) {
            page.setSize((Integer) map.get(key));
        }

        key = pageParams[1];
        if (map.containsKey(key) && map.get(key) instanceof Integer) {
            page.setCurrent((Integer) map.get(key));
        }

        // 排序
        key = pageParams[2];
        if (map.containsKey(key) && map.get(key) instanceof List) {
            List<OrderItem> orderItemList = new ArrayList<>();
            for (String orderArrStr : (List<String>) map.get(key)) {
                if (StringUtils.isBlank(orderArrStr) || !orderArrStr.contains(" ")) {
                    continue;
                }
                String[] orderArr = orderArrStr.split(" ");
                if ("desc".equals(orderArr[1])) {
                    orderItemList.add(OrderItem.desc(orderArr[0]));
                } else {
                    orderItemList.add(OrderItem.asc(orderArr[0]));
                }
            }
            page.setOrders(orderItemList);
        }

        return page;
    }
}
