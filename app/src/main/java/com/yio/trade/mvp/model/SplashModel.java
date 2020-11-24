package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.bean.SplashBean;
import com.yio.trade.mvp.contract.SplashContract;
import com.yio.trade.result.BaseBean;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/22/2019 11:33
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class SplashModel extends BaseModel implements SplashContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    private WanAndroidService wanAndroidService;

    @Inject
    public SplashModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        wanAndroidService = mRepositoryManager.obtainRetrofitService(WanAndroidService.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<BaseBean<SplashBean>> vestSign(String vestCode, String channelCode, String version, String deviceId, long timestamp) {
        return wanAndroidService.vestSign(vestCode, channelCode, version, deviceId, timestamp);
    }
}