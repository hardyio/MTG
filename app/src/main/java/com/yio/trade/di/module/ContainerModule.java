package com.yio.trade.di.module;

import com.jess.arms.integration.IRepositoryManager;
import com.yio.trade.mvp.contract.ContainerContract;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.common.AppConfig;
import com.yio.trade.common.CookiesManager;
import com.yio.trade.http.NetWorkManager;
import com.yio.trade.mvp.model.ContainerModel;

@Module
public abstract class ContainerModule {

    @Binds
    abstract ContainerContract.Model bindContainerModel(ContainerModel model);

    @Provides
    static AppConfig provideAppConfig() {
        return AppConfig.getInstance();
    }

    @Provides
    static CookiesManager provideCookiesManager() {
        return CookiesManager.getInstance();
    }

    @Provides
    static WanAndroidService provideWanAndroidService(IRepositoryManager repositoryManager) {
        return NetWorkManager.getInstance().getWanAndroidService(repositoryManager);
    }
}