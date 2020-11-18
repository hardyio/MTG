package com.yio.trade.mvp.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yio.mtg.trade.R;

import java.util.List;


public class TodoTypeAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public TodoTypeAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String type) {
        helper.setText(R.id.tvItem, type);
    }
}
