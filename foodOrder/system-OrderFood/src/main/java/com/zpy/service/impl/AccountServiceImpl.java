package com.zpy.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zpy.mapper.AccountMapper;
import com.zpy.pojo.User;
import com.zpy.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, User>implements AccountService {
    @Autowired
    private AccountMapper accountMapper;
    @Override
    public boolean login(String userName, String userPwd) {
        QueryWrapper<User>qw=new QueryWrapper<>();
        qw.eq("username",userName);
        User user = accountMapper.selectOne(qw);
        if (user==null){
            return false;
        }

        String s = DigestUtil.md5Hex(userPwd);
        if (s.equals(user.getPassword())){
            return true;
        }
        return false;
    }
}
