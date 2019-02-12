package com.renyu.androidcommonlibrary.impl;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.renyu.commonlibrary.web.impl.IWebApp;

/**
 * Created by renyu on 2017/8/14.
 */

public class WebAppInterface implements Parcelable, IWebApp {

    private Context context;
    private WebView webView;

    public WebAppInterface() {
        super();
    }

    private WebAppInterface(Parcel in) {

    }

    public static final Creator<WebAppInterface> CREATOR = new Creator<WebAppInterface>() {
        @Override
        public WebAppInterface createFromParcel(Parcel in) {
            return new WebAppInterface(in);
        }

        @Override
        public WebAppInterface[] newArray(int size) {
            return new WebAppInterface[size];
        }
    };

    @JavascriptInterface
    public String showToast(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
        return "From App";
    }

    // SDK可以通过反射的方式去获取所有需onActivityResult处理的方法
    public void onActivityResult_111() {
        webView.loadUrl("javascript:showAndroidToast('waawo')");
    }

    @JavascriptInterface
    public void call() {
        Toast.makeText(context, "call", Toast.LENGTH_SHORT).show();
    }

    public void call1(int string) {
        webView.loadUrl("javascript:onData("+string+")");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    @Override
    public void setContext(Context context) {
        this.context=context;
    }

    @Override
    public void setWebView(WebView webView) {
        this.webView=webView;
    }
}
