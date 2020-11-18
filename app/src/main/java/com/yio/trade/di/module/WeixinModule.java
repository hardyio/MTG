package com.yio.trade.di.module;

import com.jess.arms.integration.IRepositoryManager;
import com.yio.trade.mvp.contract.WeixinContract;
import com.yio.trade.mvp.model.WeixinModel;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.http.NetWorkManager;

@Module
public abstract class WeixinModule {

    @Binds
    abstract WeixinContract.Model bindWeixinModel(WeixinModel model);

    @Singleton
    @Provides
    static WanAndroidService provideService(IRepositoryManager repositoryManager) {
        return NetWorkManager.getInstance().getWanAndroidService(repositoryManager);
    }
}