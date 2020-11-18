package com.yio.trade.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.yio.trade.di.module.QAModule;
import com.yio.trade.mvp.contract.QAContract;

import com.jess.arms.di.scope.FragmentScope;

import com.yio.trade.mvp.ui.fragment.QAFragment;

@FragmentScope
@Component(modules = QAModule.class, dependencies = AppComponent.class)
public interface QAComponent {

    void inject(QAFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        QAComponent.Builder view(QAContract.View view);

        QAComponent.Builder appComponent(AppComponent appComponent);

        QAComponent build();
    }
}