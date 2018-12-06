package com.renyu.commonlibrary.baseact;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.util.AdaptScreenUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.renyu.commonlibrary.commonutils.BarUtils;
import com.renyu.commonlibrary.params.InitParams;
import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

/**
 * Created by Administrator on 2018/7/8.
 */
public abstract class BaseDataBindingActivity<T> extends AppCompatActivity {

    public abstract void initParams();
    public abstract int initViews();
    public abstract void loadData();
    public abstract int setStatusBarColor();
    public abstract int setStatusBarTranslucent();

    public T viewDataBinding;

    // 判断是否执行onCreate以下部分
    public boolean isNeedOnCreate=true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (ScreenUtils.isPortrait()) {
//            AdaptScreenUtils.adaptWidth(InitParams.designWidthInPx);
//        } else {
//            AdaptScreenUtils.adaptHeight(InitParams.designHeightInPx);
//        }

        if (!isNeedOnCreate) {
            return;
        }

        if (initViews()!=0) {
            viewDataBinding = (T) (DataBindingUtil.setContentView(this, initViews()));
        }

        // 设置沉浸式，二选一
        if (setStatusBarColor()!=0) {
            BarUtils.setColor(this, setStatusBarColor());
            // 此为全屏模式下设置沉浸式颜色
            // 此方法会导致键盘无法将EditText弹起
//                BarUtils.setColorForSwipeBack(this, setStatusBarColor(), 0);
        }
        if (setStatusBarTranslucent()!=0) {
            BarUtils.setTranslucent(this);
        }

        initParams();
        loadData();
    }

    public void openLog(String path) {
        String[] permissionsSD={Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!PermissionUtils.isGranted(permissionsSD)) {
            // 初始化xlog
            Xlog.open(true, Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, "", path, InitParams.LOG_NAME, "");
            Xlog.setConsoleLogOpen(true);
            Log.setLogImp(new Xlog());
        }
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();

        openLog(InitParams.LOG_PATH);
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();

        // 关闭xlog，生成日志
        Log.appenderClose();
    }
}
