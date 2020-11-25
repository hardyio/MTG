package com.yio.trade.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.yio.trade.model.PageInfo;

import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;

import com.yio.trade.model.Todo;
import com.yio.trade.model.TodoSection;
import com.yio.trade.result.WanAndroidResponse;

/**
 * @author ZJC
 */
public interface TodoContract {

    interface View extends IView {

        void showData(PageInfo info, List<TodoSection> todoSections, boolean switchType);

        void setTodoType(List<String> todoType);

        void deleteSuccess(Todo todo, int position);

        void updateSuccess(Todo todo, int position);
    }

    interface Model extends IModel {

        Observable<WanAndroidResponse<PageInfo<Todo>>> getTodoList(int page, LinkedHashMap<String, Object> map);

        Observable<WanAndroidResponse> updateTodo(int id, LinkedHashMap<String, Object> map);

        Observable<WanAndroidResponse> deleteTodo(int id);
    }
}
