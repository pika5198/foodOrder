package com.zpy.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zpy.pojo.Customer;
import com.zpy.pojo.Food;
import com.zpy.pojo.Order;
import com.zpy.service.CustomerService;
import com.zpy.service.FoodService;
import com.zpy.service.OrderService;
import com.zpy.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private FoodService foodService;

    @RequestMapping("listOrder")
    public String listOrder(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,
                            @RequestParam(value = "pageSize",defaultValue = "10",required = false)Integer pageSize, Model model, Order order){
        if (pageNum==null||pageNum.equals("")||pageNum<0){
            pageNum=1;
        }
        if (pageSize==null||pageSize.equals("")||pageSize<0){
            pageSize=10;
        }

        PageHelper.startPage(pageNum,pageSize);
        PageInfo<Order>pageInfo=orderService.queryAll(order.getId());
        model.addAttribute("pageInfo",pageInfo);
        return "order-list";
    }

    //修改订单
    @RequestMapping("preUpdateOrder/{orderId}")
    public String preUpdateOrder(@PathVariable Integer orderId,Model model){
        Order order = orderService.getOrderById(orderId);
        List<Food> foodList = foodService.list(null);
        List<Customer> customerList = customerService.list(null);
        model.addAttribute("order",order);
        model.addAttribute("foodList",foodList);
        model.addAttribute("customerList",customerList);
        return "order-update";
    }

    //修改订单
    @RequestMapping("updateOrder")
    public String updateOrder(Integer cid,Integer fid,Integer count){
        QueryWrapper<Order>qw=new QueryWrapper<>();
        qw.eq("cid",cid);
        qw.eq("fid",fid);
        Order order = orderService.getOne(qw);
        Integer oldCount = order.getCount();
        order.setCount(count);
        order.setTotal(foodService.getById(fid).getPrice()*count);
        boolean b = orderService.updateById(order);

        //修改库存
        if (oldCount>count){
            Food food = foodService.getById(fid);
            food.setStock(food.getStock()+(oldCount-count));
            foodService.updateById(food);
        }else {
            Food food = foodService.getById(fid);
            food.setStock(food.getStock()-(count-oldCount));
            foodService.updateById(food);
        }

        return "redirect:/order/listOrder";
    }

    //删除订单
    @RequestMapping("delOrder/{id}")
    public String delOrder(@PathVariable Integer id){
        boolean b = orderService.removeById(id);
        return "redirect:/order/listOrder";
    }

    //批量删除
    @RequestMapping("batchDeleteOrder")
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
        boolean b = orderService.removeByIds(list);
        if (b){
            return "OK";
        }else {
            return "error";
        }

    }

    @RequestMapping("sendMessage/{email}/{foodName}/{orderId}")
    public String sendMessage(@PathVariable String email,@PathVariable String foodName,@PathVariable Integer orderId){
        MailUtils.sendMail(email,"您订购的"+foodName+"正在配送中，请注意查收","餐馆邮件");
        Order byId = orderService.getById(orderId);
        byId.setStatus(1);
        orderService.updateById(byId);
        return "redirect:/order/listOrder";
    }




}
