package com.yio.trade.di.module;

import com.jess.arms.integration.IRepositoryManager;
import com.yio.trade.mvp.contract.MyCoinContract;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import com.yio.trade.api.WanAndroidService;
import com.yio.trade.http.NetWorkManager;
import com.yio.trade.mvp.model.MyCoinModel;

@Module
public abstract class MyCoinModule {

    @Binds
    abstract MyCoinContract.Model bindMyCoinModel(MyCoinModel model);

    @Provides
    static WanAndroidService provideWanAndroidService(IRepositoryManager repositoryManager) {
        return NetWorkManager.getInstance().getWanAndroidService(repositoryManager);
    }
}