package com.yio.trade.mvp.model;

import com.google.gson.Gson;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.yio.trade.api.ApiService;
import com.yio.trade.api.WanAndroidService;
import com.yio.trade.common.Const;
import com.yio.trade.http.NetWorkManager;
import com.yio.trade.model.Article;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.model.BannerImg;
import com.yio.trade.mvp.contract.HomeContract;
import com.yio.trade.result.WanAndroidResponse;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.ResponseBody;

@FragmentScope
public class HomeModel extends BaseModel implements HomeContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    private WanAndroidService wanAndroidService;
    private ApiService apiService;

    @Inject
    public HomeModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        wanAndroidService = mRepositoryManager.obtainRetrofitService(WanAndroidService.class);
        apiService = NetWorkManager.getInstance().getApiService(ApiService.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<ArticleInfo>> getArticle(int page) {
        return wanAndroidService.homeArticles(page);
    }

    @Override
    public Observable<WanAndroidResponse<List<BannerImg>>> getBanner() {
        return wanAndroidService.banner();
    }

    @Override
    public Observable<WanAndroidResponse<List<Article>>> getTopArticles() {
        return wanAndroidService.topArticles();
    }

    @Override
    public Observable<WanAndroidResponse> collect(int id) {
        return wanAndroidService.collectInside(id);
    }

    @Override
    public Observable<WanAndroidResponse> unCollect(int id) {
        return wanAndroidService.unCollect(id);
    }

    @Override
    public Observable<ResponseBody> getBingImg() {
        return apiService.bingImgUrl(Const.Url.DAILY_BING_GUOLIN);
    }

    @Override
    public Observable<WanAndroidResponse<ArticleInfo>> getArticleLocal() {
        return Observable.create(new ObservableOnSubscribe<WanAndroidResponse<ArticleInfo>>() {
            @Override
            public void subscribe(
                    ObservableEmitter<WanAndroidResponse<ArticleInfo>> emitter) throws Exception {

            }
        });

    }

    @Override
    public void saveTopFirstPage(List<Article> articles) {

    }

}