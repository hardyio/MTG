package com.yio.trade.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.yio.trade.base.BaseWanObserver;
import com.yio.trade.http.RetryWithDelay;
import com.yio.trade.model.BannerImg;
import com.yio.trade.mvp.contract.HomeContract;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.observers.ResourceObserver;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import com.yio.trade.common.CollectHelper;
import com.yio.trade.http.NetWorkManager;
import com.yio.trade.model.Article;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.result.WanAndroidResponse;
import com.yio.trade.utils.rx.RxScheduler;

@FragmentScope
public class HomePresenter extends BasePresenter<HomeContract.Model, HomeContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    HomePresenter(HomeContract.Model model, HomeContract.View rootView) {
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

    public void requestArticle(int page) {
//        mModel.getArticle(page)
//                .compose(RxScheduler.Obs_io_main())
//                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
//                .retryWhen(new RetryWithDelay(1000L))
//                .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {
//
//                    @Override
//                    public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
//                        ArticleInfo info = response.getData();
//                        mRootView.showMoreArticles(info);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        mRootView.showLoadMoreFail();
//                    }
//
//                    @Override
//                    protected void onException(ExceptionReason reason) {
//                        super.onException(reason);
//                        mRootView.showLoadMoreFail();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        mRootView.hideLoading();
//                    }
//
//                });
        ArticleInfo info = new ArticleInfo();
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("","",249,"Majors",false,13,"","",false,12554,"link","Nov 05, 12:36","","","",1606665600000L,0,"EURUSD","EUR/USD: Extra gains likely in the near-term – UOB",1,-1,1,0,null,false,0,"",0));
        articles.add(new Article("","",250,"Crosses",false,14,"","",false,12554,"link","Nov 01, 18:21","","","",1606665600000L,0,"EURGBP","EUR/GBP climbs to over one-week tops, beyond mid-0.9000s ahead of BoE",1,-1,1,0,null,false,0,"",0));
        articles.add(new Article("","",251,"Metals",false,15,"","",false,12554,"link","Nov 01, 08:10","","","",1606665600000L,0,"Gold","Gold Price Analysis: XAU/USD refreshes intraday high above $1,900 on triangle break",1,-1,1,0,null,false,0,"",0));
        articles.add(new Article("","",252,"Eurozone",false,16,"","",false,12554,"link","Oct 30, 09:20","","","",1606665600000L,0,"Germany","Coronavirus update: Germany reports about 20K new cases, record daily rise",1,-1,1,0,null,false,0,"",0));
        articles.add(new Article("","",253,"Currencies",false,17,"","",false,12554,"link","Oct 30, 07:12","","","",1606665600000L,0,"NZDUSD","NZD/USD Price Analysis: Gyrates near six-week-old ascending channel resistance",1,-1,1,0,null,false,0,"",0));
        articles.add(new Article("","",254,"Majors",false,18,"","",false,12554,"link","Oct 30, 08:39","","","",1606665600000L,0,"USDCHF","USD/CHF options show weakest bearish bias since May 2019",1,-1,1,0,null,false,0,"",0));
        info.setDatas(articles);
        mRootView.showMoreArticles(info);
        mRootView.hideLoading();
    }


    public void requestHomeData() {
        // 无网络时加载本地数据
        if (!NetWorkManager.isNetWorkAvailable()) {
            mRootView.showMessage("请检查网络连接");
            return;
        }
//        loadLocalData();
        loadRemoteData();
    }

    private void loadLocalData() {
        mModel.getArticleLocal()
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new ResourceObserver<WanAndroidResponse<ArticleInfo>>() {
                    @Override
                    public void onNext(WanAndroidResponse<ArticleInfo> response) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //    private void loadRemoteData() {
    //        //使用zip合并首页三个创建网络访问的observable
    //        Observable.zip(mModel.getBanner(), mModel.getTopArticles(), mModel.getArticle(0),
    //                (bannerResponse, topResponse, commonResponse) -> {
    //                    Timber.e("zip线程%s", Thread.currentThread().getName());
    //                    ArticleInfo topArticleInfo = topResponse.getData();
    //                    List<Article> topArticles = topArticleInfo.getDatas();
    //                    for (Article article : topArticles) {
    //                        article.setTop(true);
    //                    }
    //                    List<Article> articleList = commonResponse.getData().getDatas();
    //                    topArticles.addAll(articleList);
    //                    WanAndroidResponse<List<Article>> articleResponse = new WanAndroidResponse<>();
    //                    articleResponse.setData(topArticles);
    //                    // 首页文章保存入库,保存置顶及第一页文章
    //                    mModel.saveTopFirstPage(topArticles);
    //                    WanAndroidResponse<ZipEntity> wanAndroidResponse = new WanAndroidResponse<>();
    //                    wanAndroidResponse.setData(new ZipEntity(bannerResponse, articleResponse));
    //                    return wanAndroidResponse;
    //                })
    //                  .compose(RxScheduler.Obs_io_main())
    //                  .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
    //                  .retryWhen(new RetryWithDelay(3, 2000))
    //                  .doOnNext(response -> {
    //                      Timber.e("doOnNext线程%s", Thread.currentThread().getName());
    //                      ZipEntity zipEntity = response.getData();
    //                      List<BannerImg> bannerImgs = zipEntity.getBannerResponse().getData();
    //                      List<Article> articles = zipEntity.getArticleResponse().getData();
    //                      if (ObjectUtils.isNotEmpty(bannerImgs)) {
    //                          mRootView.showBanner(bannerImgs);
    //                      }
    //                      if (ObjectUtils.isNotEmpty(articles)) {
    //                          mRootView.refresh(articles);
    //                      }
    //                  })
    //                  //                  .doOnComplete(() -> mRootView.hideLoading())
    //                  // 回到io线程发起下一个请求
    //                  .observeOn(Schedulers.io())
    //                  // FlatMap将一个发送事件的上游Observable变换为多个发送事件的Observables，然后将它们发射的事件合并后放进一个单独的Observable里.
    //                  // 使用flatMap进行嵌套请求，完成以上三个网络请求后再请求每日一图
    //                  // 特别注意：因为flatMap是对初始被观察者作变换，所以对于旧被观察者，它是新观察者，所以通过observeOn切换线程
    //                  // 这里需要注意的是, flatMap并不保证事件的顺序,如果需要保证顺序则需要使用concatMap
    //                  .flatMap(
    //                          new Function<WanAndroidResponse<ZipEntity>, ObservableSource<ResponseBody>>() {
    //                              @Override
    //                              public ObservableSource<ResponseBody> apply(
    //                                      WanAndroidResponse<ZipEntity> zipEntityWanAndroidResponse) throws Exception {
    //                                  // 判断本地是否获取到，若无则获取今日诗词并保存本地
    //                                  Timber.e("flatMap线程%s", Thread.currentThread().getName());
    //                                  String poem = AppConfig.getInstance().getPoem();
    //                                  String appName = JApplication.getInstance()
    //                                                               .getString(R.string.app_name);
    //                                  if (StringUtils.isEmpty(poem) || StringUtils.equals(poem,
    //                                          appName)) {
    //                                      PoemUtils.getPoemSync();
    //                                  }
    //                                  // 获取每日一图
    //                                  return mModel.getBingImg();
    //                              }
    //                          })
    //                  .observeOn(AndroidSchedulers.mainThread())
    //                  .subscribe(new ResourceObserver<ResponseBody>() {
    //                      @Override
    //                      protected void onStart() {
    //                          if (!NetWorkManager.isNetWorkAvailable()) {
    //                              mRootView.showNoNetwork();
    //                              dispose();
    //                          }
    //                      }
    //
    //                      @Override
    //                      public void onNext(ResponseBody responseBody) {
    //                          String url = null;
    //                          try {
    //                              url = responseBody.string();
    //                          }
    //                          catch (IOException e) {
    //                              e.printStackTrace();
    //                          }
    //                          mRootView.addDailyPic(url);
    //                      }
    //
    //                      @Override
    //                      public void onError(Throwable e) {
    //
    //                      }
    //
    //                      @Override
    //                      public void onComplete() {
    //                          mRootView.hideLoading();
    //                      }
    //                  });
    //    }

    /**
     * 收藏或取消收藏文章
     */
    public void collectArticle(Article article, int position) {
        CollectHelper.with(mRootView).target(article).position(position).collect();
    }

    public void loadRemoteData() {
        // 请求轮播图
        requestBanner();
        // 请求文章(置顶和第一页)，使用zip合并
        requestFirstPage();
    }

    /**
     * 请求轮播图
     */
    private void requestBanner() {
//        Timber.d("requestBanner");
//        Observable.zip(mModel.getBanner(), mModel.getBingImg(),
//                new BiFunction<WanAndroidResponse<List<BannerImg>>, ResponseBody, List<BannerImg>>() {
//                    @Override
//                    public List<BannerImg> apply(WanAndroidResponse<List<BannerImg>> response,
//                                                 ResponseBody responseBody) throws Exception {
//                        Timber.d("requestBanner 线程:%s", Thread.currentThread().getName());
//                        String url = responseBody.string();
//                        BannerImg bannerImg = new BannerImg();
//                        bannerImg.setImagePath(url);
//                        bannerImg.setTitle("每日一图");
//                        List<BannerImg> banners = response.getData();
//                        banners.add(0, bannerImg);
//                        return banners;
//                    }
//                })
//                  .compose(RxScheduler.Obs_io_main())
//                  .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
//                  .retryWhen(new RetryWithDelay(3, 2000))
//                  .subscribe(new BaseObserver<List<BannerImg>>(mRootView) {
//                      @Override
//                      public void onSuccess(List<BannerImg> bannerImgs) {
//                          Timber.e("requestBanner 处理请求成功");
//                          mRootView.showBanner(bannerImgs);
//                      }
//                  });
        List<BannerImg> bannerImgs = new ArrayList<>();
        BannerImg bannerImg = new BannerImg("jpy", 1, "https://editorial.fxstreet.com/images/Markets/Currencies/Majors/USDJPY/yen-japones-billetes-de-banco_Small.jpg", 0, 0, "USD/JPY: Bears eye 104.00 amid US election jitters", 0, "");
        bannerImgs.add(bannerImg);
        BannerImg bannerImg2 = new BannerImg("gold", 2, "https://editorial.fxstreet.com/images/Markets/Commodities/Metals/Gold/stack-of-golden-bars-in-the-bank-vault-60756080_Small.jpg", 0, 0, "Gold eyes key $1922 upside level amid US election chaos", 0, "");
        bannerImgs.add(bannerImg2);
        mRootView.showBanner(bannerImgs);
    }

    private void requestFirstPage() {
//        Observable.zip(mModel.getTopArticles(), mModel.getArticle(0),
//                new BiFunction<WanAndroidResponse<List<Article>>, WanAndroidResponse<ArticleInfo>, List<Article>>() {
//                    @Override
//                    public List<Article> apply(WanAndroidResponse<List<Article>> topResponse,
//                                               WanAndroidResponse<ArticleInfo> commonResponse) throws Exception {
//                        Timber.d("requestFirstPage线程:%s", Thread.currentThread().getName());
//                        List<Article> tops = topResponse.getData();
//                        for (Article top : tops) {
//                            top.setTop(true);
//                        }
//                        List<Article> articles = commonResponse.getData().getDatas();
//                        tops.addAll(articles);
//                        return tops;
//                    }
//                })
//                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
//                .compose(RxScheduler.Obs_io_main())
//                .retryWhen(new RetryWithDelay(3, 2000))
//                .subscribe(new BaseObserver<List<Article>>(mRootView) {
//                    @Override
//                    public void onSuccess(List<Article> articles) {
//                        Timber.e("requestFirstPage请求成功，共有%s条数据", articles.size());
//                        mRootView.refresh(articles);
//                    }
//                });
        mRootView.hideLoading();
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("","",249,"Majors",false,13,"","",true,12554,"link","Nov 05, 12:36","","","",1606665600000L,0,"EURUSD","EUR/USD: Extra gains likely in the near-term – UOB",1,-1,1,0,null,true,0,"",0));
        articles.add(new Article("","",250,"Crosses",false,14,"","",true,12554,"link","Nov 01, 18:21","","","",1606665600000L,0,"EURGBP","EUR/GBP climbs to over one-week tops, beyond mid-0.9000s ahead of BoE",1,-1,1,0,null,true,0,"",0));
        articles.add(new Article("","",251,"Metals",false,15,"","",true,12554,"link","Nov 01, 08:10","","","",1606665600000L,0,"Gold","Gold Price Analysis: XAU/USD refreshes intraday high above $1,900 on triangle break",1,-1,1,0,null,true,0,"",0));
        articles.add(new Article("","",252,"Eurozone",false,16,"","",true,12554,"link","Oct 30, 09:20","","","",1606665600000L,0,"Germany","Coronavirus update: Germany reports about 20K new cases, record daily rise",1,-1,1,0,null,true,0,"",0));
        articles.add(new Article("","",253,"Currencies",false,17,"","",true,12554,"link","Oct 30, 07:12","","","",1606665600000L,0,"NZDUSD","NZD/USD Price Analysis: Gyrates near six-week-old ascending channel resistance",1,-1,1,0,null,true,0,"",0));
        articles.add(new Article("","",254,"Majors",false,18,"","",true,12554,"link","Oct 30, 08:39","","","",1606665600000L,0,"USDCHF","USD/CHF options show weakest bearish bias since May 2019",1,-1,1,0,null,true,0,"",0));
        mRootView.refresh(articles);
    }

    /**
     * 合并首页banner及文章列表的实体
     */
    private static class ZipEntity {

        private WanAndroidResponse<List<BannerImg>> bannerResponse;

        private WanAndroidResponse<List<Article>> articleResponse;

        public ZipEntity(WanAndroidResponse<List<BannerImg>> bannerResponse,
                         WanAndroidResponse<List<Article>> articleResponse) {
            this.bannerResponse = bannerResponse;
            this.articleResponse = articleResponse;
        }

        public WanAndroidResponse<List<BannerImg>> getBannerResponse() {
            return bannerResponse;
        }

        public void setBannerResponse(WanAndroidResponse<List<BannerImg>> bannerResponse) {
            this.bannerResponse = bannerResponse;
        }

        public WanAndroidResponse<List<Article>> getArticleResponse() {
            return articleResponse;
        }

        public void setArticleResponse(WanAndroidResponse<List<Article>> articleResponse) {
            this.articleResponse = articleResponse;
        }
    }
}
