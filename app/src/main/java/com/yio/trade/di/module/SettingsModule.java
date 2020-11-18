package com.yio.trade.di.module;

import com.yio.trade.mvp.contract.SettingsContract;

import dagger.Binds;
import dagger.Module;

import com.yio.trade.mvp.model.SettingsModel;

@Module
public abstract class SettingsModule {

    @Binds
    abstract SettingsContract.Model bindSettingsModel(SettingsModel model);
}