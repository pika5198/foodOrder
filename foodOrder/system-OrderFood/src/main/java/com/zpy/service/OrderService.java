package com.zpy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zpy.pojo.CountNumber;
import com.zpy.pojo.Order;

import java.util.List;

public interface OrderService extends IService<Order> {
    PageInfo<Order> queryAll(Integer id);

    Order getOrderById(Integer orderId);

    List<CountNumber> queryCount();

    List<CountNumber> queryTotal();

    PageInfo<Order> myCollection(Integer userId);

    PageInfo<Order> myCart(Integer userId);

    PageInfo<Order> listMyOrders(Integer userId, String foodName);

    int payOrder(Integer userId);

    Order getInvoiceById(Integer orderId);
}
