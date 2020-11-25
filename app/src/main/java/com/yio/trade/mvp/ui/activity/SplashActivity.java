package com.yio.trade.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.yio.mtg.trade.BuildConfig;
import com.yio.mtg.trade.R;
import com.yio.trade.bean.SplashBean;
import com.yio.trade.common.Const;
import com.yio.trade.di.component.DaggerSplashComponent;
import com.yio.trade.mvp.contract.SplashContract;
import com.yio.trade.mvp.presenter.SplashPresenter;
import com.yio.trade.utils.DeviceIdUtil;
import com.yio.trade.utils.RouterHelper;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.View {

    @BindView(R.id.iv_flash)
    ImageView ivFlash;
    @BindView(R.id.tv)
    TextView tv;

    private int recLen = 5;//跳过倒计时提示5秒
    Timer timer = new Timer();
    private Handler handler;
    private Runnable runnable;
    private Class<?> targetClass;
    private String advUrl;
    private String h5Url;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerSplashComponent //如找不到该类,请编译一下项目
                .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LaunchTheme);
        super.onCreate(savedInstanceState);
        //定义全屏参数
//        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
//        getWindow().setFlags(flag, flag);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @Override
                public void run() {
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("Jump " + recLen);
                    recLen--;
                    if (recLen < 0) {
                        timer.cancel();
                        tv.setVisibility(View.GONE);//倒计时到0隐藏字体
                    }
                }
            });
        }
    };

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_splash;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        handler = new Handler();
        mPresenter.vestSign(Const.VEST_CODE, Const.CHANNEL_CODE, BuildConfig.VERSION_NAME, DeviceIdUtil.getDeviceId(this), System.currentTimeMillis());
    }

    public void showNoNetwork() {
        startCountdown();
    }

    @Override
    public void showMessage(@NonNull String message) {
        startCountdown();
    }

    @Override
    public void vestSignSuccess(SplashBean splashBean) {
        h5Url = splashBean.getH5Url();
        if (splashBean.getStatus() == 0) {
            setTargetClass(WebActivity.class);
            String advImg = splashBean.getAdvImg();
            if (splashBean.getAdvOn() == 1 && !TextUtils.isEmpty(advImg)) {
                //显示广告
                Glide.with(this).load(advImg).into(ivFlash);
                this.advUrl = splashBean.getAdvUrl();
            }
        } else {
            setTargetClass(MainActivity.class);
        }
        startCountdown();
    }

    private void setTargetClass(Class<?> clazz) {
        this.targetClass = clazz;
    }

    private Class getTargetClass() {
        return this.targetClass;
    }

    private void startCountdown() {
        timer.schedule(task, 0, 1000);//等待时间一秒，停顿时间一秒
        /**
         * 正常情况下不点击跳过
         */
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                //从闪屏界面跳转到首界面
                if (getTargetClass() == WebActivity.class) {
                    RouterHelper.switchToWebPageWithUrl(SplashActivity.this, h5Url, " ");
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }
        }, 6000);//延迟5S后发送handler信息
    }

    private void reset() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        task.cancel();
        timer.cancel();
        task = null;
        timer = null;
    }

    @OnClick({R.id.iv_flash, R.id.tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_flash:
                if (!TextUtils.isEmpty(advUrl)) {
                    RouterHelper.switchToAdverPageWithUrl(this, advUrl, " ");
                }
                break;
            case R.id.tv:
                if (!TextUtils.isEmpty(h5Url)) {
                    //从闪屏界面跳转到首界面
                    RouterHelper.switchToWebPageWithUrl(this, h5Url, " ");
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        reset();
        super.onDestroy();
    }
}