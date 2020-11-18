package com.yio.trade.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.yio.trade.di.module.CollectionModule;
import com.yio.trade.mvp.contract.CollectionContract;

import com.jess.arms.di.scope.FragmentScope;
import com.yio.trade.mvp.ui.fragment.CollectionFragment;

@FragmentScope
@Component(modules = CollectionModule.class, dependencies = AppComponent.class)
public interface CollectionComponent {

    void inject(CollectionFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        CollectionComponent.Builder view(CollectionContract.View view);

        CollectionComponent.Builder appComponent(AppComponent appComponent);

        CollectionComponent build();
    }
}