package com.yio.trade.mvp.ui.adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yio.mtg.trade.R;
import com.yio.trade.model.CoinHistory;

import java.util.List;

public class MyCoinAdapter extends BaseQuickAdapter<CoinHistory, BaseViewHolder> {

    public MyCoinAdapter(int layoutResId, @Nullable List<CoinHistory> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CoinHistory item) {
        helper.setText(R.id.tvReason, item.getReason())
              .setText(R.id.tvDesc, item.getDesc())
              .setText(R.id.tvCoinCount, String.format("+%s", item.getCoinCount()));
    }
}
