package com.yio.trade.mvp.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jess.arms.base.BaseFragment;
import com.yio.mtg.trade.R;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.yio.trade.mvp.contract.SplashContract;
import com.yio.trade.mvp.presenter.SplashPresenter;
import com.yio.trade.mvp.ui.activity.MainActivity;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class SplashFragment extends BaseFragment<SplashPresenter> implements SplashContract.View {

    private MainActivity mainActivity;

    public static SplashFragment newInstance() {
         Bundle args = new Bundle();
         SplashFragment fragment = new SplashFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (mContext instanceof MainActivity) {
            mainActivity = ((MainActivity)mContext);
            mainActivity.setTheme(R.style.LaunchTheme);
        }
//        new Handler().postDelayed(() -> mainActivity.replaceFragment(ContainerFragment.newInstance()), 800);
        new Handler().postDelayed(() -> FragmentUtils.replace(this, ContainerFragment.newInstance(), false), 800);
    }

    @Override
    public void setData(@Nullable Object data) {

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
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
//            killMyself();
            ToastUtils.showShort("闪屏页被隐藏");
//            getFragmentManager().popBackStackImmediate();
        }
    }
}