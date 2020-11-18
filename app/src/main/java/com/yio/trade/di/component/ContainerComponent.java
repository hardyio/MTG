package com.yio.trade.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.yio.trade.common.AppConfig;
import com.yio.trade.common.CookiesManager;
import com.yio.trade.di.module.ContainerModule;
import com.yio.trade.mvp.contract.ContainerContract;

import com.jess.arms.di.scope.FragmentScope;

import com.yio.trade.mvp.ui.fragment.ContainerFragment;

@FragmentScope
@Component(modules = ContainerModule.class, dependencies = AppComponent.class)
public interface ContainerComponent {

    void inject(ContainerFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        ContainerComponent.Builder view(ContainerContract.View view);

        ContainerComponent.Builder appComponent(AppComponent appComponent);

        ContainerComponent build();
    }

    AppConfig appConfig();

    CookiesManager cookiesManager();
}