package com.yio.trade.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.yio.trade.di.module.ShareModule;
import com.yio.trade.mvp.contract.ShareContract;

import com.jess.arms.di.scope.FragmentScope;

import com.yio.trade.mvp.ui.fragment.ShareFragment;

@FragmentScope
@Component(modules = ShareModule.class, dependencies = AppComponent.class)
public interface ShareComponent {

    void inject(ShareFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        ShareComponent.Builder view(ShareContract.View view);

        ShareComponent.Builder appComponent(AppComponent appComponent);

        ShareComponent build();
    }
}