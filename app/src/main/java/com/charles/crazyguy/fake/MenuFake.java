package com.charles.crazyguy.fake;

import com.charles.crazyguy.common.Action;
import com.charles.crazyguy.dto.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单数据
 * */
public class MenuFake {
    public static List<MenuItem> loadMenuData(){
        List<MenuItem> menuItems = new ArrayList<>();
        MenuItem menuItem = new MenuItem();
        menuItem.title = "发送通知";
        menuItem.action = Action.ACTION_VIEW_NOTIFICATION;
        menuItems.add(menuItem);

        menuItem = new MenuItem();
        menuItem.title = "Activity启动模式";
        menuItem.action = Action.ACTION_VIEW_ACTIVITY_MODE;
        menuItems.add(menuItem);

        menuItem = new MenuItem();
        menuItem.title = "IntentService使用";
        menuItem.action = Action.ACTION_VIEW_INTENT_SERVICE;
        menuItems.add(menuItem);

        return menuItems;
    }
}
