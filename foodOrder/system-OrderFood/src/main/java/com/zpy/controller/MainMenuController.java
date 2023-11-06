package com.zpy.controller;

import com.zpy.pojo.CountNumber;
import com.zpy.pojo.MainMenu;
import com.zpy.pojo.MainMenu1;
import com.zpy.service.OrderService;
// import jdk.internal.dynalink.linker.LinkerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("main")
public class MainMenuController {

    @Autowired
    private OrderService orderService;
    @RequestMapping("mainMenu")
    public List<MainMenu>queryCount(){
        List<CountNumber>list=orderService.queryCount();
        List<MainMenu>mainMenuList=new ArrayList<>();
        for (CountNumber countNumber : list) {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setType(countNumber.getName());
            mainMenu.setMount(Integer.valueOf(countNumber.getCount()));
            mainMenuList.add(mainMenu);
        }

        if (mainMenuList.size()<12){
            return mainMenuList;
        }

        List<MainMenu> arrList=new ArrayList<>();
        for (int i = 0; i <12; i++) {
            arrList.add(mainMenuList.get(i));
        }

        return arrList;
    }

    @RequestMapping("mainMenu2")
    public  List<MainMenu1> mainMenu2(){
        List<CountNumber>list=orderService.queryTotal();
        List<MainMenu1>mainMenuList=new ArrayList<>();
        for (CountNumber countNumber : list) {
            MainMenu1 mainMenu1 = new MainMenu1();
            mainMenu1.setType(countNumber.getName());
            mainMenu1.setMount(Double.valueOf(countNumber.getCount()));
            mainMenuList.add(mainMenu1);
        }

        if (mainMenuList.size()<12){
            return mainMenuList;
        }

        List<MainMenu1> arrList=new ArrayList<>();
        for (int i = 0; i <12; i++) {
            arrList.add(mainMenuList.get(i));
        }

        return arrList;

    }
}
