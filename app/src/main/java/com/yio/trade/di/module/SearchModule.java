package com.yio.trade.di.module;

import com.yio.trade.mvp.contract.SearchContract;

import dagger.Binds;
import dagger.Module;

import com.yio.trade.mvp.model.SearchModel;

@Module
public abstract class SearchModule {

    @Binds
    abstract SearchContract.Model bindSearchModel(SearchModel model);
}