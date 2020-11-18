package com.yio.trade.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.yio.trade.di.module.WebModule;
import com.yio.trade.mvp.contract.WebContract;

import com.jess.arms.di.scope.ActivityScope;
import com.yio.trade.mvp.ui.activity.WebActivity;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 08/19/2019 15:29
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = WebModule.class, dependencies = AppComponent.class)
public interface WebComponent {

    void inject(WebActivity activity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        WebComponent.Builder view(WebContract.View view);

        WebComponent.Builder appComponent(AppComponent appComponent);

        WebComponent build();
    }
}