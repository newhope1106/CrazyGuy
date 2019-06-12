package com.charles.crazyguy.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * 定义菜单MVP协议
 * */
public interface MenuContract {
    interface IPresenter {
        /**
         * 加载菜单数据
         * */
        void loadData();
        /**
         * 显示View
         * */
        void showView(RecyclerView rv);
        /**
         * 获取上下文
         * */
        Context getContext();
    }

    interface IView {

    }
}
