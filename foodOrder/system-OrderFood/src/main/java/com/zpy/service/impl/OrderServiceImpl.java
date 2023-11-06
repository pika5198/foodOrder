package com.zpy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.zpy.mapper.OrderMapper;
import com.zpy.pojo.CountNumber;
import com.zpy.pojo.Order;
import com.zpy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Override
    public PageInfo<Order> queryAll(Integer id) {
        List<Order>list=orderMapper.queryAll(id);
        return new PageInfo<>(list);
    }

    @Override
    public Order getOrderById(Integer orderId) {
        return orderMapper.getOrderById(orderId);
    }

    @Override
    public List<CountNumber> queryCount() {
        return orderMapper.queryCount();
    }

    @Override
    public List<CountNumber> queryTotal() {
        return orderMapper.queryTotal();
    }

    @Override
    public PageInfo<Order> myCollection(Integer userId) {
        List<Order>list=orderMapper.myCollection(userId);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Order> myCart(Integer userId) {
        List<Order>list=orderMapper.myCart(userId);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Order> listMyOrders(Integer userId, String foodName) {
        List<Order>list=orderMapper.listMyOrders(userId,foodName);
        return new PageInfo<>(list);
    }

    @Override
    public int payOrder(Integer userId) {
        return orderMapper.payOrder(userId);
    }

    @Override
    public Order getInvoiceById(Integer orderId) {
        return orderMapper.getInvoiceById(orderId);
    }
}
