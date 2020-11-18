package com.yio.trade.di.module;

import com.yio.trade.mvp.contract.SignupContract;

import dagger.Binds;
import dagger.Module;

import com.yio.trade.mvp.model.SignupModel;

@Module
public abstract class SignupModule {

    @Binds
    abstract SignupContract.Model bindSignupModel(SignupModel model);
}