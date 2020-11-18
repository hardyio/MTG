package com.yio.trade.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.yio.trade.di.module.TodoEditModule;
import com.yio.trade.mvp.contract.TodoEditContract;

import com.jess.arms.di.scope.ActivityScope;
import com.yio.trade.mvp.ui.activity.TodoEditActivity;

@ActivityScope
@Component(modules = TodoEditModule.class, dependencies = AppComponent.class)
public interface TodoEditComponent {

    void inject(TodoEditActivity activity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        TodoEditComponent.Builder view(TodoEditContract.View view);

        TodoEditComponent.Builder appComponent(AppComponent appComponent);

        TodoEditComponent build();
    }
}