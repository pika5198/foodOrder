package com.zpy.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("store")
public class Store {
    private Integer id;
    private String storeName;
    private String descr;
}
