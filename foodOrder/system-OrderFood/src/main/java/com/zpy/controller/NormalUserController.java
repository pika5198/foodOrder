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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("normalUser")
public class NormalUserController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private FoodService foodService;

    @RequestMapping("listFood")
    public String listFood(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                           @RequestParam(required = false,value = "pageSize",defaultValue = "12")Integer pageSize, Model model, Food food){
        if (pageNum<0 || pageNum==null || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize<0 || pageSize==null || pageSize.equals("")){
            pageSize=12;
        }
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Food>qw=new QueryWrapper<>();
        if (food.getFoodName()!=null){
            qw.like("food_name",food.getFoodName());
        }
        List<Food> list = foodService.list(qw);
        PageInfo<Food>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "customer-food-list";
    }

    @RequestMapping("collection")
    public String collection(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                             @RequestParam(required = false,value = "pageSize",defaultValue = "12")Integer pageSize, Model model, HttpSession session){
        if (pageNum<0 || pageNum==null || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize<0 || pageSize==null || pageSize.equals("")){
            pageSize=12;
        }
        PageHelper.startPage(pageNum,pageSize);
        Integer userId = (Integer) session.getAttribute("userId");
        PageInfo<Order>pageInfo=orderService.myCollection(userId);
        model.addAttribute("pageInfo",pageInfo);
        return "myCollection";

    }

    @RequestMapping("cart")
    public String cart(HttpSession session,Model model){
        Integer userId = (Integer) session.getAttribute("userId");
        PageInfo<Order>pageInfo=orderService.myCart(userId);
        List<Order> list = pageInfo.getList();
        double a=0;
        for (Order order : list) {
            a+=order.getTotal();
        }
        model.addAttribute("totalMoney",a);
        model.addAttribute("pageInfo",pageInfo);
        return "cart";
    }
    @RequestMapping("myOrders")
    public String myOrders(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                           @RequestParam(required = false,value = "pageSize",defaultValue = "12")Integer pageSize, Model model, HttpSession session,Food food){
        if (pageNum<0 || pageNum==null || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize<0 || pageSize==null || pageSize.equals("")){
            pageSize=12;
        }
        PageHelper.startPage(pageNum,pageSize);
        Integer userId = (Integer) session.getAttribute("userId");
        String foodName = food.getFoodName();
        PageInfo<Order>pageInfo=orderService.listMyOrders(userId,foodName);
        model.addAttribute("pageInfo",pageInfo);
        return "myOrders-list";
    }

    @RequestMapping("foodToCart/{foodId}")
    public String foodToCart(@PathVariable Integer foodId, HttpSession session){

        Integer userId =(Integer) session.getAttribute("userId");
        QueryWrapper<Order>qw=new QueryWrapper<>();
        qw.eq("cid",userId);
        qw.eq("fid",foodId);
        qw.eq("isorder",0);
        Order one = orderService.getOne(qw);
        if (one!=null){
            session.setAttribute("msg","此菜品已加入购物车");
            return "redirect:/normalUser/listFood";
        }
        Food food = foodService.getById(foodId);
        Order order = new Order();
        order.setFid(foodId);
        order.setCid(userId);
        order.setCount(1);
        order.setTotal(food.getPrice());
        orderService.save(order);
        session.removeAttribute("msg");
        return "redirect:/normalUser/cart";
    }

    @RequestMapping("toFoodSingle/{foodId}")
    public String toFoodSingle(@PathVariable  Integer foodId,Model model){
        Food food = foodService.getById(foodId);
        model.addAttribute("food",food);
        QueryWrapper<Food>qw=new QueryWrapper<>();
        qw.eq("store",food.getStore());
        List<Food> list = foodService.list(qw);
        model.addAttribute("foodList",list);
        return "food-single";
    }

    @RequestMapping("foodToCollection/{foodId}")
    public String foodToCollection(@PathVariable Integer foodId,HttpSession session){
        Integer userId =(Integer) session.getAttribute("userId");
        QueryWrapper<Order>qw=new QueryWrapper<>();
        qw.eq("cid",userId);
        qw.eq("fid",foodId);
        qw.eq("isorder",2);
        Order order = orderService.getOne(qw);
        if (order!=null){
            session.setAttribute("msg","此菜品已收藏");
            return "redirect:/normalUser/listFood";
        }
        Food food = foodService.getById(foodId);
        Order order1 = new Order();
        order1.setFid(foodId);
        order1.setCid(userId);
        order1.setCount(1);
        order1.setTotal(food.getPrice());
        order1.setIsorder(2);
        orderService.save(order1);
        session.removeAttribute("msg");
        return "redirect:/normalUser/collection";
    }

    //取消收藏
    @RequestMapping("delCollection/{orderId}")
    public String delCollection(@PathVariable Integer orderId){
        boolean b = orderService.removeById(orderId);
        return "redirect:/normalUser/collection";
    }

    @RequestMapping("payOrder")
    public String payOrder(HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");

        int i=orderService.payOrder(userId);
        return "redirect:/normalUser/myOrders";
    }

    @RequestMapping("updateOrder")
    @ResponseBody
    public String updateOrder(Integer orderId,Integer count){
        Order order = orderService.getById(orderId);
        Integer oldCount = order.getCount();
        Integer fid = order.getFid();
        Food food = foodService.getById(fid);
        Double price = food.getPrice();
        order.setId(orderId);
        order.setCount(count);
        order.setTotal(count*price);
        boolean b = orderService.updateById(order);
        //修改库存
        if (oldCount>count){
            food.setStock(food.getStock()+(oldCount-count));
            foodService.updateById(food);
        }else {
            food.setStock(food.getStock()-(count-oldCount));
            foodService.updateById(food);
        }

        if (b){
            return "OK";
        }else {
            return "error";
        }
    }

    @RequestMapping("delCart/{orderId}")
    public String delCart(@PathVariable Integer orderId,HttpSession session){

        boolean remove = orderService.removeById(orderId);
        return "redirect:/normalUser/cart";
    }

    @RequestMapping("confirmOrder/{orderId}")
    public String confirmOrder(@PathVariable Integer orderId){
        Order order = orderService.getById(orderId);
        order.setStatus(2);
        orderService.updateById(order);
        return "redirect:/normalUser/myOrders";
    }

    @RequestMapping("invoice/{orderId}")
    public String invoice(@PathVariable Integer orderId,Model model){
        Order order = orderService.getInvoiceById(orderId);
        model.addAttribute("order",order);
        return "invoice";

    }

    //批量删除
    @PostMapping("batchDeleteMyOrder")
    @ResponseBody
    public String batchDeleteReaderBook(String idList){
        String[]split= StrUtil.split(idList,",");
        List<Integer> list=new ArrayList<>();

        for (String s : split) {
            if (!s.isEmpty()){
                int i = Integer.parseInt(s);
                list.add(i);
            }
        }
        boolean b = orderService.removeByIds(list);
        if (b){
            return "OK";
        }else {
            return "error";
        }
    }

    @RequestMapping("preUpdateMyOrder/{id}")
    public String preUpdateMyOrder(@PathVariable Integer id,Model model){
        Order order = orderService.getById(id);
        Integer cid = order.getCid();
        Integer fid = order.getFid();
        Customer customer = customerService.getById(cid);
        Food food = foodService.getById(fid);
        model.addAttribute("food",food);
        model.addAttribute("customer",customer);
        model.addAttribute("order",order);
        return "myOrders-update";
    }

    @RequestMapping("updateMyOrder")
    public String updateMyOrder(Integer id,Integer count){
        Order order = orderService.getById(id);
        Integer oldCount = order.getCount();
        Integer fid = order.getFid();
        Food food = foodService.getById(fid);
        order.setCount(count);
        order.setTotal(count* food.getPrice());
        orderService.updateById(order);
        //修改库存
        if (oldCount>count){
            //库存加
            food.setStock(food.getStock()+(oldCount-count));
            foodService.updateById(food);
        }else {
            //库存减
            food.setStock(food.getStock()-(count-oldCount));
            foodService.updateById(food);
        }
        return "redirect:/normalUser/myOrders";
    }

    @RequestMapping("delMyOrder/{id}")
    public String delMyOrder(@PathVariable Integer id){
        boolean b = orderService.removeById(id);
        return "redirect:/normalUser/myOrders";
    }
}
