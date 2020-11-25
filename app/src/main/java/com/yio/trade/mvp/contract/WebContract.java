package com.yio.trade.mvp.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.yio.trade.bean.LoginBean;
import com.yio.trade.model.Article;
import com.yio.trade.result.BaseBean;
import com.yio.trade.result.WanAndroidResponse;

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
public interface WebContract {

    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void updateCollectStatus(boolean collect, Article article);

        void getTokenSuccess(String host, String token1, String token2, String url);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<WanAndroidResponse> collect(int id);

        Observable<WanAndroidResponse> unCollect(int id);

        Observable<BaseBean<LoginBean>> googleSignIn(String id, String name, String email, String sign, String type, String host);
    }
}
