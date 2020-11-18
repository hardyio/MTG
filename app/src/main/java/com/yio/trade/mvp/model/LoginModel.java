package com.yio.trade.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;
import com.yio.trade.http.NetWorkManager;
import com.yio.trade.model.User;
import com.yio.trade.mvp.contract.LoginContract;
import com.yio.trade.result.WanAndroidResponse;

import javax.inject.Inject;

import io.reactivex.Observable;

@FragmentScope
public class LoginModel extends BaseModel implements LoginContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public LoginModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<User>> login(String userName, String password) {
        // 方案一：原始做法
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Const.Url.WAN_ANDROID)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        WanAndroidService service = retrofit.create(WanAndroidService.class);
//        LinkedHashMap<String, String> map = new LinkedHashMap<>();
//        map.put("userName", userName);
//        map.put("password", password);
//        return service.login(userName, password);

        //方案二：使用mRepositoryManager
//        RetrofitUrlManager.getInstance().setGlobalDomain(Const.Url.WAN_ANDROID);
//        return Observable.just(mRepositoryManager
//                .obtainRetrofitService(WanAndroidService.class)
//                .login(userName, password))
//                .flatMap(
//                        (Function<Observable<WanAndroidResponse<User>>, ObservableSource<WanAndroidResponse<User>>>)wanAndroidResultObservable ->
//                                wanAndroidResultObservable);

        //方案三 封装后动态切换url
//        RetrofitUrlManager.getInstance().setGlobalDomain(Const.Url.GITHUB);
        return NetWorkManager.getInstance()
                      .getWanAndroidService(mRepositoryManager)
                      .login(userName, password);
    }
}