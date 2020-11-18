package com.yio.trade.mvp.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.UriUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jess.arms.base.BaseFragment;
import com.yio.mtg.trade.R;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.PermissionUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yio.trade.common.AppConfig;
import com.yio.trade.common.Const;
import com.yio.trade.common.CookiesManager;
import com.yio.trade.common.ScrollTopListener;
import com.yio.trade.model.Coin;
import com.yio.trade.mvp.contract.ContainerContract;
import com.yio.trade.mvp.presenter.ContainerPresenter;
import com.yio.trade.mvp.ui.activity.MainActivity;
import com.yio.trade.mvp.ui.activity.SearchActivity;
import com.yio.trade.mvp.ui.activity.SettingsActivity;
import com.yio.trade.mvp.ui.activity.WebActivity;
import com.yio.trade.utils.RxPhotoTool;
import com.yio.trade.utils.UIUtils;
import com.yio.trade.utils.rx.RxScheduler;
import com.yio.trade.widgets.ChooseImageDialog;

import de.hdodenhof.circleimageview.CircleImageView;

import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import com.yio.trade.di.component.DaggerContainerComponent;
import com.yio.trade.event.Event;

import pers.zjc.commonlibs.constant.PermissionConstants;
import pers.zjc.commonlibs.ui.BasePagerAdapter;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * @author ZJC
 */
public class ContainerFragment extends BaseFragment<ContainerPresenter>
        implements ContainerContract.View {

    private static final long INTERVAL_DOUBLE_CLICK = 1000L;
    private static final int TYPE_LOAD_LOCAL_IMAGE = 1;
    private static final int TYPE_ACCESS_IMAGE = 2;

    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.toolbar_left)
    RelativeLayout toolbarBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vpContainer)
    ViewPager viewPager;
    @BindView(R.id.bottomNav)
    BottomNavigationView bottomNav;
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    private TextView tvPoem;

    @Inject
    AppConfig appConfig;
    @Inject
    CookiesManager cookiesManager;

    private ImageView ivRank;
    private TextView tvLevel;
    private TextView tvIntegral;
    private TextView tvUserName;
    private TextView tvRank;
    private CircleImageView ivAvatar;
    private int mStartType = 0;
    private String[] mTitles = { "Home", "News"};
    private BasePagerAdapter<String, Fragment> fragmentPagerAdapter;

    private long firstClick = 0L;
    private MainActivity mActivity;
    private int coinCount;
    private TextView tvCoinCount;
    private Coin myCoin;
    private View headerView;
    private View rlNavHeader;

    public static ContainerFragment newInstance() {
        ContainerFragment fragment = new ContainerFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mActivity = (MainActivity)context;
        }
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerContainerComponent //如找不到该类,请编译一下项目
                                 .builder()
                                 .appComponent(appComponent)
                                 .view(this)
                                 .build()
                                 .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //        UIUtils.setSameColorBar(true, mActivity);

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(Const.Key.SAVE_INSTANCE_STATE)) {

            }
        }
        initView();
        loadMyCoin();
    }

    private void loadMyCoin() {
        tvLevel.setText(String.format("Level:%s", "--"));
        tvIntegral.setText(String.format("id:%s", "--"));
        tvRank.setText(String.format("Ranking:%s", "--"));
        if (isLogin()) {
            mPresenter.loadCoin();
        }
        else {
            tvUserName.setText(getResources().getString(R.string.hint_user_name));
        }
    }

    private void initView() {
        initNavigationView();
        initToolbar();
        initFloatingButton();
        initBottomBav();
        initViewPager();
    }

    private void initNavigationView() {
        headerView = navigationView.getHeaderView(0);
        rlNavHeader = headerView.findViewById(R.id.rlNavHeader);
        ivRank = headerView.findViewById(R.id.icRanking);
        tvLevel = headerView.findViewById(R.id.tvLevel);
        tvIntegral = headerView.findViewById(R.id.tvIntegral);
        tvUserName = headerView.findViewById(R.id.tvUserName);
        tvRank = headerView.findViewById(R.id.tvRank);
        ivAvatar = headerView.findViewById(R.id.ivAvatar);
        tvPoem = headerView.findViewById(R.id.tvPoem);
        // 隐藏掉 转到刷新header
        tvPoem.setVisibility(View.GONE);
        ivAvatar.setBorderColor(ContextCompat.getColor(mContext, R.color.base_bg_color));
        ivAvatar.setBorderWidth(5);
        ivAvatar.setOnClickListener(v -> openGallery());
        tvCoinCount = (TextView)navigationView.getMenu().getItem(0).getActionView();
        tvCoinCount.setGravity(Gravity.CENTER_VERTICAL);
        //设置图片为本身的颜色
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_integral:
                    Bundle bundle = new Bundle();
                    bundle.putInt(Const.Key.KEY_COIN_COUNT, coinCount);
                    Fragment fragment = MyCoinFragment.newInstance();
                    fragment.setArguments(bundle);
                    mActivity.switchFragment(fragment);
                    break;
                case R.id.nav_share:

                    break;
                case R.id.nav_collection:
                    mActivity.switchFragment(CollectionFragment.newInstance());
                    break;
                case R.id.nav_todo:
                    mActivity.switchFragment(TodoTabFragment.newInstance());
                    //                    launchActivity(new Intent(mContext, TodoTabFragment.class));
                    break;
                case R.id.nav_settings:
                    launchActivity(new Intent(mContext, SettingsActivity.class));
                    break;
                case R.id.nav_about_us:
                    Intent intent = new Intent(mActivity, WebActivity.class);
                    intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_URL);
                    intent.putExtra(Const.Key.KEY_WEB_PAGE_URL, Const.Url.ABOUT_US);
                    intent.putExtra(Const.Key.KEY_WEB_PAGE_TITLE,
                            getResources().getString(R.string.drawer_about_us));
                    launchActivity(intent);
                    break;
                case R.id.nav_logout:
                    UIUtils.createConfirmDialog(mContext, "Are you sure to logout？", true,
                            (dialog, which) -> mPresenter.logout(), null).show();

                    break;
                default:
                    break;
            }
            return true;
        });
        //        setDrawerToggle();
        ivRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToRankPage();
            }
        });
        tvUserName.setOnClickListener(v -> {
            if (!isLogin()) {
                mActivity.switchFragment(LoginFragment.newInstance());
            }
        });
        configLogoutButton();
        if (StringUtils.isEmpty(appConfig.getAvatar())) {
            return;
        }
        // 加载头像
        if (PermissionUtils.isGranted(PermissionConstants.STORAGE)) {
            loadLocalAvatar();
        }
        else {
            requestStoragePermissions(TYPE_LOAD_LOCAL_IMAGE);
        }
    }

    /**
     * 初始化本地储存的头像并高斯模糊
     */
    private void loadLocalAvatar() {
        File file = new File(appConfig.getAvatar());
        if (StringUtils.isEmpty(appConfig.getAvatar()) || !file.exists()) {
            //            Glide.with(ivAvatar.getContext()).load(R.drawable.ic_avatar).into(ivAvatar);
            ivAvatar.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_avatar));
        }
        else {
            Glide.with(ivAvatar.getContext()).load(appConfig.getAvatar()).into(ivAvatar);
            blurBackground(file);
        }
    }

    private void openGallery() {
        if (!appConfig.isLogin()) {
            mActivity.switchFragment(LoginFragment.newInstance());
            return;
        }
        // 申请权限
        requestStoragePermissions(TYPE_ACCESS_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RxPhotoTool.GET_IMAGE_BY_CAMERA:
                    if (data == null) {
                        // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        // 指定了存储的路径的uri，那么返回的data就为null
                        Observable.create(new ObservableOnSubscribe<Uri>() {
                            @Override
                            public void subscribe(ObservableEmitter<Uri> emitter) throws Exception {
                                Uri uri = RxPhotoTool.getUri(mContext, appConfig.getUserName(),true);
                                emitter.onNext(uri);
                            }
                        }).compose(RxScheduler.Obs_io_main()).subscribe(new Observer<Uri>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Uri uri) {
                                handlePicByCamera(uri);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e);
                                showMessage("图片处理出错");
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                    }
                    break;
                case RxPhotoTool.GET_IMAGE_FROM_PHONE:
                    Uri uri = data.getData();
                    handlePicByAlbum(uri);
                    break;
                default:
                    break;
            }
        }
    }

    private void handlePicByAlbum(Uri uri) {
        if (uri != null) {
            File file = UriUtils.uri2File(uri);
            if (file == null) {
                return;
            }
            String path = file.getAbsolutePath();
            Timber.e(path);
            if (!ImageUtils.isImage(path)) {
                showMessage("请选择图片文件");
                return;
            }
            Glide.with(ivAvatar.getContext()).load(uri).into(ivAvatar);
            appConfig.setAvatar(file.getAbsolutePath());
            blurBackground(file);
        }
    }

    /**
     * 处理相机拍照返回图片
     */
    private void handlePicByCamera(Uri uri) {
        if (uri == null) {
            showMessage("uri is null");
            return;
        }
        Timber.e("uri:%s", uri.toString());
        File file = UriUtils.uri2File(uri);
        //        File file = UriUtils.uri2File(uri);
        if (file == null || !file.exists()) {
            showMessage("图片丢失，请重新拍摄");
            return;
        }
        String path = file.getAbsolutePath();
        Timber.e("图片路径%s", path);
        if (!ImageUtils.isImage(file)) {
            showMessage("请选择图片文件");
            return;
        }
        Glide.with(ivAvatar.getContext()).load(path).into(ivAvatar);
        appConfig.setAvatar(path);
        blurBackground(file);
    }

    /**
     * 注意：要针对Android 10(Q)储存权限适配：取消对READ_EXTERNAL_STORAGE 和 WRITE_EXTERNAL_STORAGE两个权限的申请。
     * 并替换为新的媒体特定权限。
     */
    private void requestStoragePermissions(int type) {
        PermissionUtil.RequestPermission requestPermission = new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                switch (type) {
                    case TYPE_LOAD_LOCAL_IMAGE:
                        loadLocalAvatar();
                        break;
                    case TYPE_ACCESS_IMAGE:
                        ChooseImageDialog.create(ContainerFragment.this).show();
                        // 打开本地图库
                        //                        RxPhotoTool.openLocalImage(ContainerFragment.this);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                showMessage("拒绝存储权限将无法正常使用app,可能导致崩溃");
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                showMessage("您已拒绝存储权限，将无法正常使用app,请前往系统设置打开存储权限");
            }
        };
        RxErrorHandler handler = RxErrorHandler.builder()
                                               .with(mContext)
                                               .responseErrorListener(
                                                       (context, t) -> Timber.e(t.getMessage()))
                                               .build();
        PermissionUtil.requestPermission(requestPermission, new RxPermissions(this), handler,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void blurBackground(File file) {
        if (file == null || !file.exists()) {
            Timber.e("文件不存在");
            return;
        }
        // 被观察者
        Observable<Bitmap> observable = Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                Bitmap source = ImageUtils.getBitmap(file);
                if (source == null) {
                    emitter.onError(new Throwable("Bitmap转换失败"));
                    return;
                }
                // 先按照1/8进行缩放，然后再进行模糊
                Bitmap blurBm = UIUtils.rsBlur(mContext, source, 20, 1f / 8f);
                emitter.onNext(blurBm);
            }
        });
        // 观察者
        Observer<Bitmap> observer = new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Bitmap blurredBm) {
                if (rlNavHeader == null) {
                    return;
                }
                rlNavHeader.setBackground(new BitmapDrawable(getResources(), blurredBm));
            }

            @Override
            public void onError(Throwable e) {
                showMessage(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
        // 切换线程并订阅
        observable.compose(RxScheduler.Obs_io_main()).subscribe(observer);
    }

    private void configLogoutButton() {
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(isLogin());
    }

    private void switchToRankPage() {
        Fragment fragment = RankFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Const.Key.KEY_MY_COIN, myCoin);
        fragment.setArguments(bundle);
        mActivity.switchFragment(fragment);
    }

    private void setDrawerToggle() {
        //通过actionbardrawertoggle将toolbar与drawablelayout关联起来
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,
                R.string.hint_user_name, R.string.hint_user_name) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //可以重新侧滑方法,该方法实现侧滑动画,整个布局移动效果
                //获取mDrawerLayout中的第一个子布局，也就是布局中的RelativeLayout
                //获取抽屉的view
                View mContent = drawerLayout.getChildAt(0);
                float scale = 1 - slideOffset;
                float endScale = 0.8f + scale * 0.2f;
                float startScale = 1 - 0.3f * scale;

                //设置左边菜单滑动后的占据屏幕大小
                drawerView.setScaleX(startScale);
                drawerView.setScaleY(startScale);
                //设置菜单透明度
                drawerView.setAlpha(0.6f + 0.4f * (1 - scale));

                //设置内容界面水平和垂直方向偏转量
                //在滑动时内容界面的宽度为 屏幕宽度减去菜单界面所占宽度
                mContent.setTranslationX(drawerView.getMeasuredWidth() * (1 - scale));
                //设置内容界面操作无效（比如有button就会点击无效）
                mContent.invalidate();
                //设置右边菜单滑动后的占据屏幕大小
                mContent.setScaleX(endScale);
                mContent.setScaleY(endScale);
            }
        };
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
    }

    private void initToolbar() {
        Glide.with(mContext).load(R.drawable.ic_menu).into(ivLeft);
        Glide.with(mContext).load(R.drawable.ic_search).into(ivRight);
        setToolbar(bottomNav.getMenu().getItem(0).getTitle().toString());
        ivLeft.setOnClickListener(v -> toggleDrawer());
        ivRight.setOnClickListener(v -> switchToSearchPage());
        //        toggleDrawer();
    }

    private void toggleDrawer() {
        drawerLayout.openDrawer(navigationView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(mActivity, drawerLayout,
                R.string.drawer_open, R.string.drawer_close);
        toggle.onDrawerOpened(drawerLayout);
        toggle.syncState();
    }

    private void switchToSearchPage() {
        launchActivity(new Intent(mContext, SearchActivity.class));
    }

    private void initBottomBav() {
        // 防止其他用到该颜色值的控件，都变成透明：使用mutate()方法使该控件状态不定，这样不定状态的控件就不会共享自己的状态了。
        //        bottomNav.getBackground().mutate().setAlpha(5);
        bottomNav.setOnNavigationItemSelectedListener(menuItem -> {
            setToolbar(menuItem.getTitle().toString());
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.nav_wenda:
                    viewPager.setCurrentItem(1);
                    break;
//                case R.id.nav_public:
//                    viewPager.setCurrentItem(2);
//                    break;
                default:
                    break;
            }
            return false;
        });
        bottomNav.setOnNavigationItemReselectedListener(menuItem -> slideToTop(true));
    }

    private void initFloatingButton() {
        fabTop.setOnClickListener(v -> slideToTop(false));
    }

    private void slideToTop(boolean refresh) {
        int pos = viewPager.getCurrentItem();
        Fragment fragment = fragmentPagerAdapter.getFragment(pos);
        if (fragment instanceof ScrollTopListener) {
            if (refresh) {
                ((ScrollTopListener)fragment).scrollToTopRefresh();
            }
            ((ScrollTopListener)fragment).scrollToTop();
        }
    }

    private void initViewPager() {
        // 按需设置viewPager预加载fragment数量，此处有5个界面，设置预加载4个，结合Fragment的懒加载，只预加载视图，不加载数据
        viewPager.setOffscreenPageLimit(2);
        fragmentPagerAdapter = new BasePagerAdapter<>(getFragmentManager(),
                new BasePagerAdapter.PagerFragCreator<String, Fragment>() {
                    @Override
                    public Fragment createFragment(String data, int position) {
                        return createMainFragments(position);
                    }

                    @Override
                    public String createTitle(String data) {
                        return data;
                    }
                });
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                MenuItem menuItem = bottomNav.getMenu().getItem(i);
                tvTitle.setText(menuItem.getTitle());
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        // 绑定数据
        fragmentPagerAdapter.setData(Arrays.asList(mTitles));
    }

    private Fragment createMainFragments(int position) {
        switch (position) {
            case 0:
            default:
                return HomeFragment.newInstance();
            case 1:
                return QAFragment.newInstance();
//            case 2:
//                return WeixinFragment.newInstance();
        }
    }

    public void setToolbar(String title) {
        checkNotNull(title);
        tvTitle.setText(title);
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
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        long cur = TimeUtils.getNowMills();
        if (cur - firstClick > INTERVAL_DOUBLE_CLICK) {
            showMessage(getString(R.string.back_tips));
        }
        else {
            killMyself();
        }
        firstClick = cur;
        ArmsUtils.exitApp();
    }

    /**
     * Token过期,重新登录
     */
    //    @Subscriber
    //    public void onTokenExpiredEvent(Event event) {
    //        if (null != event) {
    //            if (event.getEventCode() == Const.EventCode.LOGIN_EXPIRED && mContext.equals(
    //                    ActivityUtils.getTopActivity())) {
    //                showMessage(getString(R.string.error_login_expired));
    //                if (mContext instanceof MainActivity) {
    //                    mActivity.switchFragment(LoginFragment.newInstance());
    //                }
    //            }
    //        }
    //    }

    /**
     * 登录成功
     */
    @Subscriber
    public void onLoginSuccess(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOGIN_SUCCESS) {
            showMessage("登录成功");
            configLogoutButton();
            loadLocalAvatar();
            mPresenter.loadCoin();
        }
    }

    @Override
    public void showCoin(Coin data) {
        this.myCoin = data;
        tvUserName.setText(appConfig.getUserName());
        tvLevel.setText(data.getLevelStr());
        tvIntegral.setText(data.getIdStr());
        tvRank.setText(data.getFormatRank());
        this.coinCount = data.getCoinCount();
        tvCoinCount.setText(String.valueOf(coinCount));
    }

    @Override
    public void showLogoutSuccess() {
        tvLevel.setText(String.format("Level:%s", "--"));
        tvIntegral.setText(String.format("id:%s", "--"));
        tvRank.setText(String.format("Ranking:%s", "--"));
        tvUserName.setText(getResources().getString(R.string.hint_user_name));
        tvCoinCount.setText("");
        rlNavHeader.setBackground(ContextCompat.getDrawable(mContext, R.color.colorPrimary));
        appConfig.clear();
        loadLocalAvatar();
        configLogoutButton();
        EventBusManager.getInstance().post(new Event<>(Const.EventCode.LOG_OUT, null));
    }

    @Override
    public void showPoem(String content) {
        tvPoem.setText(content);
    }

    private boolean isLogin() {
        return appConfig.isLogin();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // 意外销毁时（屏幕方向切换、颜色模式改变等）保存状态
        outState.putBoolean(Const.Key.SAVE_INSTANCE_STATE, true);
        super.onSaveInstanceState(outState);
    }

    public void switchToHome() {
        viewPager.setCurrentItem(1);
    }

}
