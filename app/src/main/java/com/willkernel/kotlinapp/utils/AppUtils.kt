package com.willkernel.kotlinapp.utils

import android.content.Context
import android.os.Build
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.willkernel.kotlinapp.MyApplication


class AppUtils private constructor() {
    companion object {
        fun getVerCode(context: Context): Long {
            val pkgName = context.packageName
            return context.packageManager.getPackageInfo(pkgName, 0).longVersionCode
        }

        fun getVerName(context: Context): String? {
            val pkgName = context.packageName
            return context.packageManager.getPackageInfo(pkgName, 0).versionName
        }

        /**
         * 打卡软键盘
         */
        fun openKeyBord(mEditText: EditText) {
            val imm = MyApplication.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        /**
         * 关闭软键盘
         */
        fun closeKeyBord(mEditText: EditText) {
            val imm = MyApplication.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
        }

        fun getModel(): String? {
            return Build.MODEL?.trim {
                it <= ' '
            } ?: ""
        }

        fun isOverBuildVersion(version: Int): Boolean {
            return Build.VERSION.SDK_INT >= version
        }

    }
}