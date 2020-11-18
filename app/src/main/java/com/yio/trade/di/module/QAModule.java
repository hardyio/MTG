package com.yio.trade.di.module;

import com.yio.trade.mvp.contract.QAContract;

import dagger.Binds;
import dagger.Module;

import com.yio.trade.mvp.model.QAModel;

@Module
public abstract class QAModule {

    @Binds
    abstract QAContract.Model bindQAModel(QAModel model);
}