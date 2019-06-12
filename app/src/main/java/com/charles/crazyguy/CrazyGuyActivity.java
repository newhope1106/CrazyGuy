package com.charles.crazyguy;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.charles.crazyguy.activities.BaseActivity;
import com.charles.crazyguy.menu.MenuPresenter;

public class CrazyGuyActivity extends BaseActivity {
    private MenuPresenter mMenuPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPresenters();
    }

    private void initPresenters(){
        if(mMenuPresenter == null) {
            mMenuPresenter = new MenuPresenter(this);
        }
        mMenuPresenter.showView((RecyclerView)findViewById(R.id.menu_recycler_view));
        mMenuPresenter.loadData();
    }
}
