package com.zpy.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zpy.pojo.Customer;
import com.zpy.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Value("${location}")
    public String location;


    @RequestMapping("listCustomer")
    public String listCustomer(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                               @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model, Customer customer){
        if (pageNum<0 || pageNum==null || pageNum.equals("")){
            pageNum=1;
        }
        if (pageSize<0 || pageSize==null || pageSize.equals("")){
            pageSize=10;
        }
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Customer>qw=new QueryWrapper<>();
        if (!(customer.getCustomerName()==null)){
            qw.like("customer_name",customer.getCustomerName());
        }
        List<Customer> list = customerService.list(qw);
        PageInfo<Customer>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "customer-list";
    }

    @RequestMapping("preSaveCustomer")
    public String preSaveCustomer(){
        return "customer-save";
    }

    @RequestMapping("saveCustomer")
    public String saveCustomer(Customer customer, MultipartFile file){
        transFile(customer,file);
        boolean save = customerService.save(customer);
        return "redirect:/customer/listCustomer";
    }

    private void transFile(Customer customer, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(index);
        String prefix=System.nanoTime()+"";
        String path=prefix+suffix;
        File file1 = new File(location);
        if (!file1.exists()){
            file1.mkdirs();
        }
        File file2 = new File(file1, path);
        try {
            file.transferTo(file2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        customer.setCimage(path);
    }

    @RequestMapping("preUpdateCustomer/{cid}")
    public String preUpdateCustomer(@PathVariable Integer cid,Model model){
        Customer customer = customerService.getById(cid);
        model.addAttribute("customer",customer);
        return "customer-update";
    }

    @RequestMapping("updateCustomer")
    public String updateCustomer(Customer customer){
        boolean b = customerService.updateById(customer);
        return "redirect:/customer/listCustomer";
    }

    @RequestMapping("delCustomer/{cid}")
    public String delCustomer(@PathVariable Integer cid){
        boolean b = customerService.removeById(cid);
        return "redirect:/customer/listCustomer";
    }


    @RequestMapping("batchDeleteCustomer")
    @ResponseBody
    public String batchDeleteCustomer(String idList){
        String[] split = StrUtil.split(idList, ",");
        List<Integer>list=new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()){
                Integer integer = Integer.valueOf(s);
                list.add(integer);
            }
        }

        boolean b = customerService.removeByIds(list);
        if (b){
            return "OK";
        }else {
            return "error";
        }

    }
}
