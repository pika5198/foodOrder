package com.zpy.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zpy.pojo.Customer;
import com.zpy.pojo.Food;
import com.zpy.pojo.Store;
import com.zpy.service.FoodService;
import com.zpy.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.pattern.PathPattern;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("food")
public class FoodController {
    @Autowired
    private FoodService foodService;
    @Autowired
    private StoreService storeService;

    @Value("${location}")
    private String location;

    @RequestMapping("listFood")
    public String listFood(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,
                           @RequestParam(value = "pageSize",defaultValue = "10",required = false)Integer pageSize, Model model, Food food){
        if (pageNum==null||pageNum.equals("")||pageNum<0){
            pageNum=1;
        }
        if (pageSize==null||pageSize.equals("")||pageSize<0){
            pageSize=10;
        }

        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Food>qw=new QueryWrapper<>();
        if (!(food.getFoodName()==null)){
            qw.like("food_name",food.getFoodName());
        }
        if (!(food.getStore()==null)){
            qw.like("store",food.getStore());
        }
        List<Food> list = foodService.list(qw);
        PageInfo<Food> pageInfo = new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        QueryWrapper<Store>queryWrapper=new QueryWrapper<>();
        List<Store> list1 = storeService.list(null);
        model.addAttribute("storeList",list1);
        return "food-list";

    }

    @RequestMapping("preSaveFood")
    public String preSaveFood(Model model){
        List<Store> list = storeService.list(null);
        model.addAttribute("storeList",list);
        return "food-save";
    }

    @RequestMapping("saveFood")
    public String saveFood(Food food, MultipartFile file){
        transFile(food,file);
        boolean save = foodService.save(food);
        return "redirect:/food/listFood";
    }

    private void transFile(Food food, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(index);
        String prefix=System.nanoTime()+"";
        String path=prefix+suffix;
        File file1 = new File(location);
        if (!file1.exists()){
            file1.mkdirs();
        }
        File file2 = new File(file1, path);
        try {
            file.transferTo(file2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        food.setFimage(path);
    }

    @RequestMapping("preUpdateFood/{foodId}")
    public String preUpdateFood(@PathVariable Integer foodId,Model model){
        List<Store> list = storeService.list(null);
        model.addAttribute("storeList",list);
        Food food = foodService.getById(foodId);
        model.addAttribute("food",food);
        return "food-update";
    }

    @RequestMapping("updateFood")
    public String updateFood(Food food){
        boolean b = foodService.updateById(food);
        return "redirect:/food/listFood";
    }

    @RequestMapping("delFood/{foodId}")
    public String delFood(@PathVariable Integer foodId){
        boolean b = foodService.removeById(foodId);
        return "redirect:/food/listFood";
    }

    @RequestMapping("batchDeleteFood")
    @ResponseBody
    public String batchDeleteFood(String idList){
        String[] split = StrUtil.split(idList, ",");
        List<Integer>list=new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()){
                Integer integer = Integer.valueOf(s);
                list.add(integer);
            }
        }
        boolean b = foodService.removeByIds(list);
        if (b){
            return "OK";
        }else {
            return "error";
        }

    }

    @RequestMapping("shangjia/{foodId}")
    public String shangjia(@PathVariable Integer foodId){
        Food food = foodService.getById(foodId);
        food.setStatus(1);
        boolean b = foodService.updateById(food);
        return "redirect:/food/listFood";

    }

    @RequestMapping("xiajia/{foodId}")
    public String xiajia(@PathVariable Integer foodId){
        Food food = foodService.getById(foodId);
        food.setStatus(2);
        boolean b = foodService.updateById(food);
        return "redirect:/food/listFood";
    }


}
