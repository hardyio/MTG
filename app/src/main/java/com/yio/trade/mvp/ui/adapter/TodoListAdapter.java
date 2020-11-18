package com.yio.trade.mvp.ui.adapter;


import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yio.mtg.trade.R;
import com.yio.trade.model.Todo;

import java.util.List;


import com.yio.trade.model.TodoWrapper;

/**
 * @author ZJC
 * Deprecated since 2019/12/30, use
 */
@Deprecated
public class TodoListAdapter extends BaseMultiItemQuickAdapter<TodoWrapper, BaseViewHolder> {

    private static final int TYPE_TITLE = 1;
    private static final int TYPE_CONTENT = 2;

    public TodoListAdapter(List<TodoWrapper> data) {
        super(data);
        addItemType(TYPE_TITLE, R.layout.item_todo_title);
        addItemType(TYPE_CONTENT, R.layout.item_my_coin);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TodoWrapper item) {
        switch (helper.getItemViewType()) {
            case TYPE_TITLE:
                helper.setText(R.id.tvDate, item.getDateStr());
                break;
            case TYPE_CONTENT:
                Todo todo = item.getTodo();
                helper.setText(R.id.tvReason, todo.getTitle());
                helper.setText(R.id.tvDesc, todo.getContent());
                break;
            default:
                break;
        }
        expandAll();
    }
}
