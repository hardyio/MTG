package com.yio.trade.mvp.ui.adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yio.mtg.trade.R;

import java.util.List;

public class SimpleListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public SimpleListAdapter(@Nullable List<String> data) {
        super(R.layout.base_popup_item_list, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        helper.setText(R.id.tvItem, item);
    }
}
