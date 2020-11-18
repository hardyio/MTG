package com.yio.trade.common;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = WanModule.class)
public interface WanComponent {

    AppConfig appconfig();
}
