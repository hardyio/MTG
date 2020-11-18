package com.yio.trade.mvp.contract;

import com.jess.arms.mvp.IModel;

import io.reactivex.Observable;

import com.jess.arms.mvp.IView;
import com.yio.trade.model.Article;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.result.WanAndroidResponse;

public interface CollectionContract {

    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void showData(ArticleInfo data);

        void updateStatus(Article article, int position);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<WanAndroidResponse<ArticleInfo>> getCollection(int page);

        Observable<WanAndroidResponse> unCollect(int id, int originId);

        Observable<WanAndroidResponse> collect(int id, int originId);
    }
}
