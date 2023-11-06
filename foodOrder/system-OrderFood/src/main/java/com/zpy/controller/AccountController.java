package com.zpy.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zpy.pojo.Customer;
import com.zpy.pojo.User;
import com.zpy.service.AccountService;
import com.zpy.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomerService customerService;

    @RequestMapping("login")
    public String login(String userName, String userPwd, Model model, HttpSession session){
        if (userName.equals("admin")){
            boolean b=accountService.login(userName,userPwd);
            if (b){
                QueryWrapper<User>qw=new QueryWrapper<>();
                qw.eq("username",userName);
                User one = accountService.getOne(qw);
                session.setAttribute("currentUser",one.getUsername());
                session.setAttribute("password",one.getPassword());
                session.setAttribute("email",one.getEmail());
                session.setAttribute("image",one.getImage());
                return "foodMainMenu";
            }else {
                model.addAttribute("msg","用户名或密码错误");
                return "index";
            }
        }else {
            boolean b=customerService.login(userName,userPwd);
            if (b){
                QueryWrapper<Customer>qw=new QueryWrapper<>();
                qw.eq("customer_name",userName);
                Customer customer=customerService.getOne(qw);
                session.setAttribute("currentUser",customer.getCustomerName());
                session.setAttribute("password",customer.getPassword());
                session.setAttribute("email",customer.getEmail());
                session.setAttribute("image",customer.getCimage());
                session.setAttribute("userId",customer.getId());
                return "foodMainMenu1";
            }else {
                model.addAttribute("msg","用户名或密码错误");
                return "index";
            }
        }
    }

    @RequestMapping("count")
    public String count(){
        return "chart_count";
    }

    @RequestMapping("total")
    public String total(){
        return "chart_total";
    }

    @RequestMapping("pwd")
    public String pwd(){
        return "modify";
    }

    @PostMapping("pwdUser")
    public String pwdUser(String userPwd,String newPwd,HttpSession session,Model model){
        String username = (String) session.getAttribute("currentUser");
        boolean login = accountService.login(username, userPwd);
        if (login){
            User user = new User();
            user.setUsername(username);
            String s = DigestUtil.md5Hex(newPwd);
            user.setPassword(s);
            QueryWrapper<User>qw=new QueryWrapper<>();
            qw.eq("username",user.getUsername());
            boolean update = accountService.update(user, qw);
            if (update){
                return "index";
            }else {
                model.addAttribute("loginFail","修改密码失败");
            }
        }else {
            model.addAttribute("loginFail","用户验证失败");
        }
        return "modify";
    }

    @RequestMapping("logout")
    public String logout(){
        return "index";
    }

    @RequestMapping("toLogin")
    public String toLogin(){
        return "index";


    }

    @RequestMapping("toRegister")
    public String toRegister(){
        return "register";
    }

    @RequestMapping("register")
    public String register(String userName,String userPwd,String confirmPwd,Model model){
        if (!userPwd.equals(confirmPwd)){
            model.addAttribute("msg","两次输入密码不一致");
            return "register";
        }
        Customer customer = new Customer();
        customer.setCustomerName(userName);
        String s = DigestUtil.md5Hex(userPwd);
        customer.setPassword(s);
        customerService.save(customer);
        return "index";
    }

    @RequestMapping("profile")
    public String profile(HttpSession session,Model model){
        String currentUser = (String) session.getAttribute("currentUser");
        String password = (String) session.getAttribute("password");
        if (currentUser.equals("admin")){
            QueryWrapper<User>qw=new QueryWrapper<>();
            qw.eq("username",currentUser);
            User user = accountService.getOne(qw);
            user.setPassword(password);
            model.addAttribute("user",user);
            return "profile-admin";
        }else {
            QueryWrapper<Customer>qw=new QueryWrapper<>();
            qw.eq("customer_name",currentUser);
            Customer user = customerService.getOne(qw);
            user.setPassword(password);
            model.addAttribute("user",user);
            return "profile-customer";
        }
    }

    @RequestMapping("updateAdminProfile")
    public String updateAdminProfile(User user){
        user.setPassword(DigestUtil.md5Hex(user.getPassword()));
        boolean b = accountService.updateById(user);
        return "index";
    }

    @RequestMapping("updateCustomerProfile")
    public String updateCustomerProfile(Customer customer){
        customer.setPassword(DigestUtil.md5Hex(customer.getPassword()));
        boolean b = customerService.updateById(customer);
        return "index";
    }
}
