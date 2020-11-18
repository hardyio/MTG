package com.yio.trade.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.yio.trade.di.module.TodoModule;
import com.yio.trade.mvp.contract.TodoContract;

import com.jess.arms.di.scope.FragmentScope;

import com.yio.trade.mvp.ui.fragment.TodoFragment;

@FragmentScope
@Component(modules = TodoModule.class, dependencies = AppComponent.class)
public interface TodoComponent {

    void inject(TodoFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        TodoComponent.Builder view(TodoContract.View view);

        TodoComponent.Builder appComponent(AppComponent appComponent);

        TodoComponent build();
    }
}