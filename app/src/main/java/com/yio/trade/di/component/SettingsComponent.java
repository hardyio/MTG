package com.yio.trade.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.yio.trade.di.module.SettingsModule;
import com.yio.trade.mvp.contract.SettingsContract;

import com.jess.arms.di.scope.ActivityScope;

import com.yio.trade.mvp.ui.activity.SettingsActivity;

@ActivityScope
@Component(modules = SettingsModule.class, dependencies = AppComponent.class)
public interface SettingsComponent {

    void inject(SettingsActivity activity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        SettingsComponent.Builder view(SettingsContract.View view);

        SettingsComponent.Builder appComponent(AppComponent appComponent);

        SettingsComponent build();
    }
}