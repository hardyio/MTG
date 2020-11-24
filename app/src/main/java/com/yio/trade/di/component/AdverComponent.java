package com.yio.trade.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.yio.trade.di.module.AdverModule;
import com.yio.trade.di.module.WebModule;
import com.yio.trade.mvp.contract.AdverContract;
import com.yio.trade.mvp.contract.WebContract;
import com.yio.trade.mvp.ui.activity.AdverActivity;
import com.yio.trade.mvp.ui.activity.WebActivity;

import dagger.BindsInstance;
import dagger.Component;

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
@Component(modules = AdverModule.class, dependencies = AppComponent.class)
public interface AdverComponent {

    void inject(AdverActivity activity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        AdverComponent.Builder view(AdverContract.View view);

        AdverComponent.Builder appComponent(AppComponent appComponent);

        AdverComponent build();
    }
}