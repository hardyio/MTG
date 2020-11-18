package com.yio.trade.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.yio.trade.di.module.WeixinModule;
import com.yio.trade.mvp.contract.WeixinContract;

import com.jess.arms.di.scope.FragmentScope;
import com.yio.trade.mvp.ui.fragment.WeixinFragment;

@FragmentScope
@Component(modules = WeixinModule.class, dependencies = AppComponent.class)
public interface WeixinComponent {

    void inject(WeixinFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        WeixinComponent.Builder view(WeixinContract.View view);

        WeixinComponent.Builder appComponent(AppComponent appComponent);

        WeixinComponent build();
    }
}