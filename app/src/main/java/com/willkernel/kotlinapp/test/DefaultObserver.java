package com.willkernel.kotlinapp.test;


import android.net.ParseException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.JsonParseException;
import com.willkernel.kotlinapp.R;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.json.JSONException;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * Created by willkernel
 * on 2019/3/30.
 */
public abstract class DefaultObserver<T extends Response> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T response) {
        if (response.code() == 200) {
            onSuccess(response);
        } else {
            onFail(response);
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e("Retrofit", e.getMessage());

        if (e instanceof HttpException) {     //   HTTP错误
            onException(ExceptionReason.BAD_NETWORK);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //   连接错误
            onException(ExceptionReason.CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            onException(ExceptionReason.CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {   //  解析错误
            onException(ExceptionReason.PARSE_ERROR);
        } else {
            onException(ExceptionReason.UNKNOWN_ERROR);
        }
    }

    @Override
    public void onComplete() {

    }

    /**
     * 请求数据成功 且响应码为200
     *
     * @param response 服务器返回的数据
     */
    abstract public void onSuccess(T response);

    /**
     * 服务器返回数据，但响应码不为200
     *
     * @param response 服务器返回的数据
     */
    public void onFail(T response) {
        String message = response.message();
        if (TextUtils.isEmpty(message)) {
            ToastUtils.show("response_return_error");
        } else {
            ToastUtils.show(message);
        }
    }

    /**
     * 请求异常
     *
     * @param reason
     */
    public void onException(ExceptionReason reason) {
        switch (reason) {
            case TOKEN_EXPIRED: //  token过期 刷新token
                refreshToken();
                break;
            case CONNECT_ERROR:
                ToastUtils.show("connect_error");
                break;

            case CONNECT_TIMEOUT:
                ToastUtils.show("connect_timeout");
                break;

            case BAD_NETWORK:
                ToastUtils.show("bad_network");
                break;

            case PARSE_ERROR:
                ToastUtils.show("parse_error");
                break;

            case UNKNOWN_ERROR:
            default:
                ToastUtils.show("unknown_error");
                break;
        }
    }

    private void refreshToken() {
    }

    private enum ExceptionReason {
        PARSE_ERROR, BAD_NETWORK, CONNECT_TIMEOUT, CONNECT_ERROR, UNKNOWN_ERROR, TOKEN_EXPIRED
    }
}
