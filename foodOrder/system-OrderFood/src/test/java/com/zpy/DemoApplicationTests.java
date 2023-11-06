package com.zpy;

import com.zpy.mapper.OrderMapper;
import com.zpy.pojo.CountNumber;
import com.zpy.pojo.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private OrderMapper orderMapper;
    @Test
    void contextLoads() {
        List<Order> list = orderMapper.myCollection(1);
        for (Order order : list) {
            System.out.println(order);
        }

    }

}
