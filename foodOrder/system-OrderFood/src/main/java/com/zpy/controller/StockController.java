package com.zpy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zpy.pojo.Food;
import com.zpy.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("stock")
public class StockController {
    @Autowired
    private FoodService foodService;

    @RequestMapping("listStock")
    public String listStock(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                            @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model, Food food){
        if (pageNum<0||pageNum.equals("")||pageNum==null){
            pageNum=1;
        }
        if (pageSize<0||pageSize.equals("")||pageSize==null){
            pageSize=1;
        }
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Food>qw=new QueryWrapper<>();
        if (food.getFoodName()!=null){
            qw.like("food_name",food.getFoodName());
        }
        qw.eq("status",1);
        List<Food> list = foodService.list(qw);
        PageInfo<Food>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "stock-list";
    }

    @RequestMapping("addStock/{id}")
    public String addStock(@PathVariable Integer id){
        Food byId = foodService.getById(id);
        byId.setStock(byId.getStock()+100);
        foodService.updateById(byId);
        return "redirect:/stock/listStock";
    }
}
