package com.yio.trade.di.module;

import com.jess.arms.integration.IRepositoryManager;
import com.yio.trade.mvp.contract.CollectionContract;
import com.yio.trade.mvp.model.CollectionModel;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.http.NetWorkManager;

@Module
public abstract class CollectionModule {

    @Binds
    abstract CollectionContract.Model bindCollectionModel(CollectionModel model);

    @Provides
    static WanAndroidService provideWanAndroidService(IRepositoryManager repositoryManager) {
        return NetWorkManager.getInstance().getWanAndroidService(repositoryManager);
    }

}