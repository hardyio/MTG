package com.yio.trade.mvp.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gyf.immersionbar.ImmersionBar;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.yio.mtg.trade.R;
import com.yio.trade.common.Const;
import com.yio.trade.common.JApplication;
import com.yio.trade.di.component.DaggerMainComponent;
import com.yio.trade.event.Event;
import com.yio.trade.mvp.contract.MainContract;
import com.yio.trade.mvp.presenter.MainPresenter;
import com.yio.trade.mvp.ui.fragment.ContainerFragment;
import com.yio.trade.mvp.ui.fragment.LoginFragment;
import com.yio.trade.mvp.ui.fragment.SplashFragment;
import com.yio.trade.utils.RouterHelper;

import org.json.JSONObject;
import org.simple.eventbus.Subscriber;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    @BindView(R.id.flContainer)
    FrameLayout flContainer;
    @BindView(R.id.flash_view)
    LinearLayout flashView;
    /* 当前fragment */
    private Fragment curFragment;

    private FragmentManager mFragmentManager;

    private boolean loginShowing;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.colorPrimary)
                .init();
        return R.layout.activity_main; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        flashView.setVisibility(View.VISIBLE);
        flashView.postDelayed(new Runnable() {
            @Override
            public void run() {
                flashView.setVisibility(View.GONE);
            }
        }, 1500);

        curFragment = (ContainerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        // 灰度化方案二(清明节灰化app)，来源：鸿洋公众号：https://mp.weixin.qq.com/s/EioJ8ogsCxQEFm44mKFiOQ
        //        Paint paint = new Paint();
        //        ColorMatrix cm = new ColorMatrix();
        //        cm.setSaturation(0);
        //        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        //        getWindow().getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, paint);
        ToastUtils.getDefaultMaker().setGravity(Gravity.CENTER, 0, 0);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult();
//                Log.i("FcmToken", token);
                SPUtils.getInstance().put("fcmToken", token);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            // do stuff with deep link data (nav to page, display content, etc)
        }
    };

    private void setFullScreen(boolean isFullScreen) {
        setTheme(isFullScreen ? R.style.LaunchTheme : R.style.AppTheme);
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
        RouterHelper.switchToWebPageWithUrl(this,"https://c1.mufg365.com/app_bridge.html","Test");
    }

    /**
     * 统一切换fragment的方法,不实例化多个Fragment，避免卡顿,Fragment中执行状态切换后的回调onHiddenChanged(boolean hidden)
     *
     * @param targetFragment 跳转目标fragment
     */
    public void switchFragment(Fragment targetFragment, Bundle args) {
        if (targetFragment == null) {
            Timber.d("Argument empty");
            return;
        }
        if (mFragmentManager != null) {
            FragmentTransaction trans = mFragmentManager.beginTransaction();
            // 转场自定义动画
            trans.setCustomAnimations(R.anim.translate_right_to_center,
                    R.anim.translate_center_to_left, R.anim.translate_left_to_center,
                    R.anim.translate_center_to_right);
            if (!targetFragment.isAdded()) {
                // 首次执行curFragment为空，需要判断
                if (curFragment != null) {
                    trans.hide(curFragment);
                }
                trans.add(R.id.flContainer, targetFragment,
                        targetFragment.getClass().getSimpleName());
                trans.addToBackStack(targetFragment.getClass().getSimpleName());
                //                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);    //设置动画效果

            } else {
                //                targetFragment = mFragmentManager.findFragmentByTag(targetFragment.getClass().getSimpleName());
                trans.hide(curFragment).show(targetFragment);
            }
            trans.commitAllowingStateLoss();
            if (curFragment instanceof SplashFragment) {
                trans.remove(curFragment);
            }
            curFragment = targetFragment;
        }
    }

    public void switchFragment(Fragment targetFragment) {
        switchFragment(targetFragment, null);
    }

    public void addFrag(Fragment targetFragment) {
        FragmentUtils.add(getSupportFragmentManager(), targetFragment, R.id.flContainer);
    }

    /**
     *
     */
    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            FragmentTransaction trans = fm.beginTransaction();
            trans.replace(R.id.flContainer, fragment);
            trans.commitAllowingStateLoss();
        }
    }

    public void showHideFragment(Fragment sourceFragment, Fragment destFragment) {
        FragmentUtils.showHide(destFragment, sourceFragment);
    }

    public void switchToLogin() {
        FragmentUtils.add(mFragmentManager, LoginFragment.newInstance(), R.id.flContainer);
    }

    @Override
    public boolean useFragment() {
        return true;
    }

    //    @Override
    //    public void onConfigurationChanged(Configuration newConfig) {
    //        super.onConfigurationChanged(newConfig);
    //        // 检测到暗黑模式启动或关闭，则重建
    //        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
    //        int position = AppConfig.getInstance().getDarkModePosition();
    //        int mode = DarkModeUtils.getMode(position);
    //        switch (currentNightMode) {
    //            // 夜间模式未启用，我们正在使用浅色主题
    //            case Configuration.UI_MODE_NIGHT_NO:
    //                if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
    //                    showMessage("启动白天模式");
    //                    JApplication.avoidSplashRecreate(this, MainActivity.class);
    //                }
    //                break;
    //            // 夜间模式启用，我们使用的是深色主题
    //            case Configuration.UI_MODE_NIGHT_YES:
    //                if (mode == AppCompatDelegate.MODE_NIGHT_NO) {
    //                    showMessage("启动黑暗模式");
    //                    JApplication.avoidSplashRecreate(this, MainActivity.class);
    //                }
    //                break;
    //            default:
    //                break;
    //        }
    //
    //    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Activity 意外销毁时保存状态
        outState.putBoolean(Const.Key.SAVE_INSTANCE_STATE, true);
        super.onSaveInstanceState(outState);
    }

    @Subscriber
    public void onUiModeChanged(Event event) {
        if (event.getEventCode() == Const.EventCode.CHANGE_UI_MODE) {
            Timber.e("onUiModeChanged-MainActivity准备重建");
            JApplication.avoidSplashRecreate(this, MainActivity.class);
        }
    }

    @Subscriber
    public void onTokenExpiredEvent(Event event) {
        if (null != event) {
            if (event.getEventCode() == Const.EventCode.LOGIN_EXPIRED && this.equals(
                    ActivityUtils.getTopActivity()) && !loginShowing) {
                showMessage(getString(R.string.error_login_expired));
                switchFragment(LoginFragment.newInstance());
                loginShowing = true;
            }
        }
    }

    /**
     * 登录成功
     */
    @Subscriber
    public void onLoginSuccess(Event event) {
        if (null != event && (event.getEventCode() == Const.EventCode.LOGIN_SUCCESS || event.getEventCode() == Const.EventCode.LOGIN_RETURN)) {
            loginShowing = false;
        }
    }

}
