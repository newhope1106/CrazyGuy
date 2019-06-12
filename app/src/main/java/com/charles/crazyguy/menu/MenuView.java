package com.charles.crazyguy.menu;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.charles.crazyguy.dto.MenuItem;

import java.util.List;

import com.charles.crazyguy.R;
import com.charles.crazyguy.util.NavUtil;

/**
 * 首页菜单列表MVP模型的View
 * */
class MenuView<P extends MenuContract.IPresenter> implements MenuContract.IView {
    private P mPresenter;
    private RecyclerView mMenuRecyclerView;
    private MenuAdapter mMenuAdapter;

    MenuView(RecyclerView rv) {
        if(rv == null) {
            throw new IllegalArgumentException("argument: RecyclerView cannot be null");
        }

        mMenuRecyclerView = rv;
    }

    void setPresenter(P presenter){
        mPresenter = presenter;
    }

    void updateUI(List<MenuItem> menuItemList){
        if(mPresenter == null) {
            return;
        }
        if(mMenuAdapter == null){
            mMenuAdapter = new MenuAdapter(mPresenter.getContext());
            mMenuAdapter.updateData(menuItemList);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mPresenter.getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mMenuRecyclerView.setLayoutManager(layoutManager);

            mMenuRecyclerView.setAdapter(mMenuAdapter);
        } else {
            mMenuAdapter.updateData(menuItemList);
            mMenuAdapter.notifyDataSetChanged();
        }

    }

    private static class MenuAdapter extends RecyclerView.Adapter{
        private List<MenuItem> mMenuItemList;
        private Context mContext;

        /**
         * 菜单点击事件
         * */
        View.OnClickListener itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view instanceof TextView) {
                    Integer position = (Integer)view.getTag();
                    if(position != null){
                        MenuItem menuItem = mMenuItemList.get(position);
                        NavUtil.doAction(menuItem.action);
                    }
                }
            }
        };

        MenuAdapter(Context context) {
            mContext = context;
        }

        void updateData(List<MenuItem> menuItemList) {
            mMenuItemList = menuItemList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemContentView = LayoutInflater.from(mContext).inflate(R.layout.menu_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(itemContentView);
            viewHolder.textView = (TextView) itemContentView.findViewById(R.id.menu_item);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MenuItem menuItem = mMenuItemList.get(position);
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.textView.setText(menuItem.title);
            viewHolder.textView.setTag(position);
            viewHolder.textView.setOnClickListener(itemClickListener);
        }

        @Override
        public int getItemCount() {
            return mMenuItemList == null ? 0 : mMenuItemList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
