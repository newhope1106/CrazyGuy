package com.charles.crazyguy.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.charles.crazyguy.fake.MenuFake;

/**
 * 首页菜单列表MVP模型的Presenter: 连接Model和View的桥梁
 * */
public class MenuPresenter implements MenuContract.IPresenter {
    private MenuView mMenuView;
    private Context mContext;

    public MenuPresenter(Context context){
        mContext = context;
    }

    @Override
    public void loadData() {
        if(mMenuView != null) {
            /*模拟Model能力，提供数据*/
            mMenuView.updateUI(MenuFake.loadMenuData());
        }
    }

    @Override
    public void showView(RecyclerView rv) {
        if(mMenuView == null) {
            mMenuView = new MenuView(rv);
            mMenuView.setPresenter(this);
        }
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
