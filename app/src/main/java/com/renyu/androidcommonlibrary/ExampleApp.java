package com.renyu.androidcommonlibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.multidex.MultiDexApplication;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.renyu.androidcommonlibrary.di.component.AppComponent;
import com.renyu.androidcommonlibrary.di.component.DaggerAppComponent;
import com.renyu.androidcommonlibrary.di.module.ApiModule;
import com.renyu.androidcommonlibrary.di.module.AppModule;
import com.renyu.androidcommonlibrary.service.X5IntentService;
import com.renyu.commonlibrary.commonutils.ImagePipelineConfigUtils;
import com.renyu.commonlibrary.network.Retrofit2Utils;
import com.renyu.commonlibrary.params.InitParams;
import com.renyu.commonlibrary.web.sonic.SonicRuntimeImpl;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mmkv.MMKV;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by renyu on 2016/12/26.
 */

public class ExampleApp extends MultiDexApplication {

    public AppComponent appComponent;

    public ArrayList<String> openClassNames;

    @Override
    public void onCreate() {
        super.onCreate();

        openClassNames = new ArrayList<>();

        String processName = ProcessUtils.getCurrentProcessName();
        if (processName != null && processName.equals(getPackageName())) {
            // dagger2注入
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .apiModule(new ApiModule())
                    .build();

            // 初始化工具库
            Utils.init(this);

            // 宽高字体缩放适配设置
            InitParams.designWidthInPx = 360;
            InitParams.designHeightInPx = 640;

            // 设置网络请求成功码
            Retrofit2Utils.sucessedCode = 1;

            // 初始化MMKV
            MMKV.initialize(this);

            // 初始化相关配置参数
            // 项目根目录
            // 请注意修改xml文件夹下filepaths.xml中的external-path节点，此值需与ROOT_PATH值相同，作为fileprovider使用
            InitParams.ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "example";
            // 项目图片目录
            InitParams.IMAGE_PATH = InitParams.ROOT_PATH + File.separator + "image";
            // 项目热修复目录
            InitParams.HOTFIX_PATH = InitParams.ROOT_PATH + File.separator + "hotfix";
            // 项目日志目录
            InitParams.LOG_PATH = InitParams.ROOT_PATH + File.separator + "log";
            InitParams.LOG_NAME = "example_log";
            // 缓存目录
            InitParams.CACHE_PATH = InitParams.ROOT_PATH + File.separator + "cache";
            // fresco缓存目录
            InitParams.FRESCO_CACHE_NAME = "fresco_cache";
            // app更新功能目录
            com.renyu.commonlibrary.update.params.InitParams.FILE_PATH = InitParams.ROOT_PATH + File.separator + "file";

            // 初始化fresco
            Fresco.initialize(this, ImagePipelineConfigUtils.getDefaultImagePipelineConfig(this));

            // 初始化Sonic
            if (!SonicEngine.isGetInstanceAllowed()) {
                SonicEngine.createInstance(new SonicRuntimeImpl(this), new SonicConfig.Builder().build());
            }

            // x5内核初始化接口
            startService(new Intent(this, X5IntentService.class));

            // 注册统计Activity生命周期所用的LifeCycle
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {
                    openClassNames.add(activity.getLocalClassName());
                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {
                    openClassNames.remove(activity.getLocalClassName());
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });

            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
        }
    }
}
