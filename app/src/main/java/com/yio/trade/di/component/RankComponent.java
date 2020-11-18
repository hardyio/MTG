package com.yio.trade.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.yio.trade.di.module.RankModule;
import com.yio.trade.mvp.contract.RankContract;

import com.jess.arms.di.scope.FragmentScope;

import com.yio.trade.mvp.ui.fragment.RankFragment;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 12/13/2019 17:02
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
@Component(modules = RankModule.class, dependencies = AppComponent.class)
public interface RankComponent {

    void inject(RankFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        RankComponent.Builder view(RankContract.View view);

        RankComponent.Builder appComponent(AppComponent appComponent);

        RankComponent build();
    }
}