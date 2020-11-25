package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.blankj.utilcode.util.StringUtils;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.mtg.trade.R;
import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.common.JApplication;
import com.yio.trade.model.User;
import com.yio.trade.mvp.contract.SignupContract;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

@FragmentScope
public class SignupPresenter extends BasePresenter<SignupContract.Model, SignupContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SignupPresenter(SignupContract.Model model, SignupContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        if (mRootView != null) {
            mRootView.killMyself();
        }
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
        super.onDestroy();
    }

    public void signUp(String userName, String originPwd, String rePwd) {
        if (validate(userName, originPwd, rePwd)) {
            mModel.signUp(userName, originPwd, rePwd)
                    .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                    .compose(RxScheduler.Obs_io_main())
                    .subscribe(new BaseWanObserver<WanAndroidResponse<User>>(mRootView) {
                        @Override
                        public void onSuccess(WanAndroidResponse<User> response) {
                            User user = response.getData();
                            if (user != null) {
                                user.setPassword(rePwd);
                                mRootView.showSignUpSuccess(user);
                            }
                        }
                    });
        }
    }

    private boolean validate(String userName, String originPwd, String rePwd) {
        if (StringUtils.isEmpty(userName)) {
            mRootView.showMessage(JApplication.getInstance().getString(R.string.error_no_account));
            return false;
        }
        if (StringUtils.isEmpty(originPwd)) {
            mRootView.showMessage(JApplication.getInstance().getString(R.string.error_no_password));
            return false;
        }
        if (StringUtils.isEmpty(rePwd)) {
            mRootView.showMessage(
                    JApplication.getInstance().getString(R.string.error_confirm_password));
            return false;
        }
        if (!StringUtils.equals(originPwd, rePwd)) {
            mRootView.showMessage(
                    JApplication.getInstance().getString(R.string.error_confirm_password));
            return false;
        }
        return true;
    }
}
