package com.yio.trade.mvp.ui.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.utils.ArmsUtils;
import com.yio.mtg.trade.R;
import com.yio.trade.common.AppConfig;
import com.yio.trade.common.Const;
import com.yio.trade.common.JApplication;
import com.yio.trade.di.component.DaggerSettingsComponent;
import com.yio.trade.mvp.contract.SettingsContract;
import com.yio.trade.mvp.presenter.SettingsPresenter;
import com.yio.trade.mvp.ui.adapter.SimpleListAdapter;
import com.yio.trade.utils.DarkModeUtils;
import com.yio.trade.utils.RvAnimUtils;
import com.yio.trade.utils.WrapContentLinearLayoutManager;

import org.simple.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DragLayout;
import per.goweii.anylayer.Layer;
import com.yio.trade.event.Event;

import pers.zjc.commonlibs.util.AppUtils;
import pers.zjc.commonlibs.util.StringUtils;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;


public class SettingsActivity extends BaseActivity<SettingsPresenter>
        implements SettingsContract.View, View.OnClickListener {

    @BindView(R.id.ll_dark_mode)
    LinearLayout llDarkMode;
    @BindView(R.id.tv_dark_mode)
    TextView tvDarkMode;
    @BindView(R.id.tv_rv_anim)
    TextView tvRvAnim;
    @BindView(R.id.ll_rv_anim)
    LinearLayout llRvAnim;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.toolbar_left)
    RelativeLayout toolbarLeft;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_check_update)
    TextView tvCheckUpdate;
    @BindView(R.id.ll_check_update)
    LinearLayout llCheckUpdate;
    private int mRvAnim;
    private int mDarkMode;
    private Layer animLayer;
    private Layer darkModeLayer;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerSettingsComponent //如找不到该类,请编译一下项目
                                .builder()
                                .appComponent(appComponent)
                                .view(this)
                                .build()
                                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_settings; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                    .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                    .statusBarColor(R.color.colorPrimary)
                    .init();
        mRvAnim = AppConfig.getInstance().getRvAnim();
        initView();
    }

    private void initView() {
        tvTitle.setText("系统设置");
        tvDarkMode.setText(DarkModeUtils.getName(AppConfig.getInstance().getDarkModePosition()));
        tvRvAnim.setText(RvAnimUtils.getName(AppConfig.getInstance().getRvAnim()));
        ivLeft.setOnClickListener(this);
        llRvAnim.setOnClickListener(this);
        llDarkMode.setOnClickListener(this);
        tvCheckUpdate.setText(String.format("%s(%s)", AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));
        llCheckUpdate.setOnClickListener(this);
    }

    private void showAnimPopWindow() {
        animLayer = AnyLayer.dialog(SettingsActivity.this)
                            .contentView(R.layout.layout_popup_list)
                            .cancelableOnTouchOutside(true)
                            .backgroundDimAmount(0.5f)
                            .gravity(Gravity.BOTTOM)
                            .dragDismiss(DragLayout.DragStyle.Bottom)
                            .contentAnimator(new Layer.AnimatorCreator() {
                                @Override
                                public Animator createInAnimator(View target) {
                                    return AnimatorHelper.createBottomInAnim(target);
                                }

                                @Override
                                public Animator createOutAnimator(View target) {
                                    return AnimatorHelper.createBottomOutAnim(target);
                                }
                            })
                            .bindData(layer -> {
                                RecyclerView rv = layer.getView(R.id.mRecyclerView);
                                ArmsUtils.configRecyclerView(rv,
                                        new WrapContentLinearLayoutManager(SettingsActivity.this));
                                List<String> list = Arrays.asList(
                                        RvAnimUtils.getName(RvAnimUtils.RvAnim.NONE),
                                        RvAnimUtils.getName(RvAnimUtils.RvAnim.ALPHAIN),
                                        RvAnimUtils.getName(RvAnimUtils.RvAnim.SCALEIN),
                                        RvAnimUtils.getName(RvAnimUtils.RvAnim.SLIDEIN_BOTTOM),
                                        RvAnimUtils.getName(RvAnimUtils.RvAnim.SLIDEIN_LEFT),
                                        RvAnimUtils.getName(RvAnimUtils.RvAnim.SLIDEIN_RIGHT));
                                SimpleListAdapter adapter = new SimpleListAdapter(list);
                                rv.setAdapter(adapter);
                                adapter.setOnItemClickListener((adapter1, view, position) -> {
                                    tvRvAnim.setText(RvAnimUtils.getName(position));
                                    AppConfig.getInstance().setRvAnim(position);
                                    layer.dismiss();
                                });
                            });
        animLayer.show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLeft:
                killMyself();
                break;
            case R.id.ll_dark_mode:
                setupDarkMode();
                break;
            case R.id.ll_rv_anim:
                showAnimPopWindow();
                break;
            case R.id.ll_check_update:

                break;
            default:
                break;
        }
    }

    private void setupDarkMode() {
        darkModeLayer = AnyLayer.dialog(SettingsActivity.this)
                                .contentView(R.layout.layout_popup_list)
                                .cancelableOnTouchOutside(true)
                                .gravity(Gravity.BOTTOM)
                                .backgroundDimAmount(0.5f)
                                .dragDismiss(DragLayout.DragStyle.Bottom)
                                .contentAnimator(new Layer.AnimatorCreator() {
                                    @Override
                                    public Animator createInAnimator(View target) {
                                        return AnimatorHelper.createBottomInAnim(target);
                                    }

                                    @Override
                                    public Animator createOutAnimator(View target) {
                                        return AnimatorHelper.createBottomOutAnim(target);
                                    }
                                })
                                .bindData(layer -> {
                                    RecyclerView rv = layer.getView(R.id.mRecyclerView);
                                    ArmsUtils.configRecyclerView(rv,
                                            new WrapContentLinearLayoutManager(SettingsActivity.this));
                                    List<String> list = Arrays.asList(DarkModeUtils.getName(0),
                                            DarkModeUtils.getName(1), DarkModeUtils.getName(2),
                                            DarkModeUtils.getName(3));
                                    SimpleListAdapter adapter = new SimpleListAdapter(list);
                                    rv.setAdapter(adapter);
                                    adapter.setOnItemClickListener((adapter1, view, position) -> {
                                        layer.dismiss();
                                        tvDarkMode.setText(DarkModeUtils.getName(position));
                                        String mode = tvDarkMode.getText().toString();
                                        String currentMode = DarkModeUtils.getName(
                                                AppConfig.getInstance().getDarkModePosition());
                                        if (StringUtils.equals(currentMode, mode)) {
                                            return;
                                        }
                                        AppConfig.getInstance().setDarkModePosition(position);
                                        JApplication.loadDarkMode();
                                        Timber.e("SettingsActivity准备重建");
                                        recreate();
                                        JApplication.avoidSplashRecreate(this,
                                                SettingsActivity.class);
                                        EventBus.getDefault()
                                                .post(new Event<>(Const.EventCode.CHANGE_UI_MODE,
                                                        null));
                                    });
                                });
        darkModeLayer.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        postSettingChangedEvent();
    }

    private void postSettingChangedEvent() {
        boolean rvAnimChanged = mRvAnim != AppConfig.getInstance().getRvAnim();
        if (rvAnimChanged) {
            Event<Integer> event = new Event<>(Const.EventCode.CHANGE_RV_ANIM,
                    AppConfig.getInstance().getRvAnim());
            EventBusManager.getInstance().post(event);
        }
    }

}
