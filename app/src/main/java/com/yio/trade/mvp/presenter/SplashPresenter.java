package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.base.BaseObserver;
import com.yio.trade.bean.SplashBean;
import com.yio.trade.http.RetryWithDelay;
import com.yio.trade.mvp.contract.SplashContract;
import com.yio.trade.result.BaseBean;
import com.yio.trade.utils.rx.RxScheduler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

@ActivityScope
public class SplashPresenter extends BasePresenter<SplashContract.Model, SplashContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SplashPresenter(SplashContract.Model model, SplashContract.View rootView) {
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

    public void vestSign(String vestCode, String channelCode, String version, String deviceId, long timestamp) {
        mModel.vestSign(vestCode, channelCode, version, deviceId, timestamp)
                .compose(RxScheduler.Obs_io_main())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .retryWhen(new RetryWithDelay(1000L))
                .subscribe(new BaseObserver<BaseBean<SplashBean>>(mRootView) {
                    @Override
                    public void onSuccess(BaseBean<SplashBean> splashBeanBaseBean) {
                        mRootView.vestSignSuccess(splashBeanBaseBean.getData());
                    }
                });
    }
}
