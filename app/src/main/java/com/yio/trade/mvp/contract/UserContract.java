package com.yio.trade.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.yio.trade.model.Coin;

import io.reactivex.Observable;
import com.yio.trade.common.ICollectView;
import com.yio.trade.model.ArticleInfo;
import com.yio.trade.model.ShareUserArticles;
import com.yio.trade.result.WanAndroidResponse;
public interface UserContract {

    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends ICollectView {

        void showArticles(ArticleInfo info);

        void showCoin(Coin coin);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<WanAndroidResponse<ShareUserArticles>> getUserArticles(long userId, int page);
    }
}
