package com.willkernel.kotlinapp.test;

import android.content.Context;

import java.util.logging.LogRecord;

/**
 * Created by willkernel
 * on 2019/3/31.
 */
class CustomProgressDialog {
    public boolean isShowing() {
        return false;
    }

    public void show() {

    }

    public void dismiss() {

    }

    public static class Builder {
        public Builder(Context context) {
        }

        public Builder setTheme(int progressDialogStyle) {
            return null;
        }

        public CustomProgressDialog build(){
            return new CustomProgressDialog();
        }

        public Builder setMessage(String msg) {
            return null;
        }
    }
}
