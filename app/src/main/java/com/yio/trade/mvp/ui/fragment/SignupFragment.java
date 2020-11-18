package com.yio.trade.mvp.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jess.arms.base.BaseFragment;
import com.yio.mtg.trade.R;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.utils.ArmsUtils;
import com.yio.trade.common.CommonTextWatcher;
import com.yio.trade.common.Const;
import com.yio.trade.model.User;
import com.yio.trade.mvp.contract.SignupContract;
import com.yio.trade.mvp.presenter.SignupPresenter;
import com.yio.trade.utils.UIUtils;

import java.util.Objects;

import com.yio.trade.di.component.DaggerSignupComponent;
import com.yio.trade.event.Event;

import butterknife.BindView;
import pers.zjc.commonlibs.util.StringUtils;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class SignupFragment extends BaseFragment<SignupPresenter> implements SignupContract.View {

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
    @BindView(R.id.etUserName)
    TextInputEditText etUserName;
    @BindView(R.id.tilUser)
    TextInputLayout tilUser;
    @BindView(R.id.etPassword)
    TextInputEditText etPassword;
    @BindView(R.id.tilPassword)
    TextInputLayout tilPassword;
    @BindView(R.id.etConfirm)
    TextInputEditText etConfirm;
    @BindView(R.id.tilConfirm)
    TextInputLayout tilConfirm;
    @BindView(R.id.btSignUp)
    AppCompatButton btSignUp;
    @BindView(R.id.llRoot)
    LinearLayout llRoot;

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerSignupComponent //如找不到该类,请编译一下项目
                              .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        initToolbar();
        setListener();
    }

    private void initToolbar() {
        toolbarLeft.setOnClickListener(v -> killMyself());
        tvTitle.setText(getResources().getString(R.string.bt_signup));
    }

    private void setListener() {
        CommonTextWatcher watcher = new CommonTextWatcher() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void afterTextChanged(Editable s) {
                String originPwd = Objects.requireNonNull(etPassword.getText(), "etPassword is null").toString().trim();
                String confirmPwd = s.toString().trim();
                if (originPwd.length() <= confirmPwd.length() && !StringUtils.equals(originPwd, confirmPwd)) {
                    tilConfirm.setError(getResources().getString(R.string.error_confirm_password));
                    tilConfirm.setErrorEnabled(true);
                } else {
                    tilConfirm.setError("");
                    tilConfirm.setErrorEnabled(false);
                }
            }
        };
        etConfirm.addTextChangedListener(watcher);
        btSignUp.setOnClickListener(v -> signUp());
    }

    private void enableClick(boolean enable) {
        btSignUp.setEnabled(enable);
        int enableColor = UIUtils.getColor(R.color.colorPrimary);
        int unableColor = UIUtils.getColor(R.color.gray);
        btSignUp.setBackgroundColor(enable ? enableColor : unableColor);
    }

    private void signUp() {
        String userName = Objects.requireNonNull(etUserName.getText(), "etUserName is null").toString().trim();
        String originPwd = Objects.requireNonNull(etPassword.getText(), "etPassword is null").toString().trim();
        String rePwd = Objects.requireNonNull(etConfirm.getText(), "etConfirm is null").toString().trim();
        mPresenter.signUp(userName, originPwd, rePwd);
    }

    @Override
    public void setData(@Nullable Object data) {

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
        ToastUtils.showShort(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        Objects.requireNonNull(getFragmentManager()).popBackStackImmediate();
    }

    @Override
    public void showSignUpSuccess(User user) {
        EventBusManager.getInstance().post(new Event<>(Const.EventCode.SIGN_SUCCESS, user));
        killMyself();
    }
}
