package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.model.Coin;
import com.yio.trade.mvp.contract.ContainerContract;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.common.AppConfig;
import com.yio.trade.common.CookiesManager;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

@FragmentScope
public class ContainerPresenter
        extends BasePresenter<ContainerContract.Model, ContainerContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    @Inject
    AppConfig appConfig;
    @Inject
    CookiesManager cookiesManager;

    @Inject
    public ContainerPresenter(ContainerContract.Model model, ContainerContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRootView.killMyself();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void loadCoin() {
        mModel.personalCoin()
              .retryWhen(new RetryWithDelay(5, 3))
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<Coin>>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse<Coin> response) {
                      mRootView.showCoin(response.getData());
                  }
              });
    }

    public void logout() {
        mModel.logout()
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse response) {
                      appConfig.setLogin(false);
                      cookiesManager.clearCookies();
                      mRootView.showLogoutSuccess();
                  }
              });
    }
}
