package com.zpy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zpy.pojo.CountNumber;
import com.zpy.pojo.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper extends BaseMapper<Order> {
    List<Order> queryAll(Integer id);

    Order getOrderById(Integer orderId);

    List<CountNumber> queryCount();

    List<CountNumber> queryTotal();

    List<Order> myCollection(Integer userId);

    List<Order> myCart(Integer userId);

    List<Order> listMyOrders(Integer userId, String foodName);

    int payOrder(Integer userId);

    Order getInvoiceById(Integer orderId);
}
