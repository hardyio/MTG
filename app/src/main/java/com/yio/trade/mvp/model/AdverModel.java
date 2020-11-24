package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.bean.LoginBean;
import com.yio.trade.mvp.contract.AdverContract;
import com.yio.trade.mvp.contract.WebContract;
import com.yio.trade.result.BaseBean;
import com.yio.trade.result.WanAndroidResponse;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 08/19/2019 15:29
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
public class AdverModel extends BaseModel implements AdverContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    private WanAndroidService wanAndroidService;

    @Inject
    public AdverModel(IRepositoryManager repositoryManager) {
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
    public Observable<BaseBean<LoginBean>> googleSignIn(String id, String name, String email, String sign, String type, String host) {
        return wanAndroidService.googleSignIn(id, name, email, sign, type, host);
    }
}