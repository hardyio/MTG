package com.yio.trade.mvp.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yio.mtg.trade.R;
import com.yio.trade.model.Todo;
import com.yio.trade.model.TodoSection;
import com.yio.trade.widgets.LabelView;

import java.util.List;

public class TodoAdapter extends BaseSectionQuickAdapter<TodoSection, BaseViewHolder> {

    public TodoAdapter(int layoutResId, int sectionHeadResId, List<TodoSection> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, TodoSection item) {
        helper.setText(R.id.tvDate, item.header);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TodoSection item) {
        Todo todo = item.t;
        helper.setText(R.id.tvTitle, todo.getTitle());
        helper.setText(R.id.dvCoin, todo.getContent());
        helper.setText(R.id.tvDate, todo.getDateStr());
        LabelView view = helper.itemView.findViewById(R.id.viewImportant);
        view.setVisibility(todo.important() ? View.VISIBLE : View.GONE);
    }
}
