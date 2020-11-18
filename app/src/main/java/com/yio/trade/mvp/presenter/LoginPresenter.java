package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.mvp.contract.LoginContract;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.common.AppConfig;
import com.yio.trade.common.Const;
import com.yio.trade.common.JApplication;
import com.yio.trade.model.User;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

@FragmentScope
public class LoginPresenter extends BasePresenter<LoginContract.Model, LoginContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    AppConfig appConfig;

    @Inject
    public LoginPresenter(LoginContract.Model model, LoginContract.View rootView) {
        super(model, rootView);
        appConfig = JApplication.getWanComponent().appconfig();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void login(String userName, String password) {
        appConfig.setAccount(userName);
        appConfig.setPassword(password);
        mModel.login(userName, password)
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<User>>(mRootView) {

                  @Override
                  public void onSuccess(WanAndroidResponse<User> result) {
                          if (result.getErrorCode() == Const.HttpConst.HTTP_CODE_SUCCESS) {
                              User user = result.getData();
                              if (user != null) {
                                  AppConfig.getInstance().setLogin(true);
                                  AppConfig.getInstance().setUserName(user.getUsername());
                                  mRootView.loginSuccess();
                              }
                          }
                          else {
                              mRootView.showMessage(result.getErrorMsg());
                          }
                  }

                  @Override
                  public void onError(Throwable e) {
                      super.onError(e);
                      AppConfig.getInstance().setAccount("");
                      AppConfig.getInstance().setPassword("");
                      AppConfig.getInstance().setLogin(false);
                  }
              });
    }
}
