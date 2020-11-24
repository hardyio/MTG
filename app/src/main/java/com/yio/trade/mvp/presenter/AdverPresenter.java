package com.yio.trade.mvp.presenter;

import android.annotation.SuppressLint;
import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.base.BaseObserver;
import com.yio.trade.bean.LoginBean;
import com.yio.trade.http.RetryWithDelay;
import com.yio.trade.mvp.contract.AdverContract;
import com.yio.trade.result.BaseBean;
import com.yio.trade.utils.rx.RxScheduler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

@ActivityScope
public class AdverPresenter extends BasePresenter<AdverContract.Model, AdverContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public AdverPresenter(AdverContract.Model model, AdverContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    @SuppressLint("CheckResult")
    public void getToken(String id, String name, String email, String sign, String type, String host) {
        mModel.googleSignIn(id, name, email, sign, type, host)
                .compose(RxScheduler.Obs_io_main())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .retryWhen(new RetryWithDelay(1000L))
                .subscribe(new BaseObserver<BaseBean<LoginBean>>(mRootView) {
                    @Override
                    public void onSuccess(BaseBean<LoginBean> loginBeanBaseBean) {
                        if (200 == loginBeanBaseBean.getCode()) {
                            LoginBean loginBeanData = loginBeanBaseBean.getData();
                            mRootView.getTokenSuccess(host, loginBeanData.getToken1(), loginBeanData.getToken2(), loginBeanData.getUrl());
                        }
                    }
                });
    }
}
