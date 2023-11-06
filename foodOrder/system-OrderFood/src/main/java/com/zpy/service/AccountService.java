package com.zpy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zpy.pojo.User;

public interface AccountService extends IService<User> {
    boolean login(String userName, String userPwd);
}
