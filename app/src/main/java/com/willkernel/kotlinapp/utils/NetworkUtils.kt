package com.willkernel.kotlinapp.utils

import android.content.Context
import android.net.ConnectivityManager
import com.willkernel.kotlinapp.MyApplication

class NetworkUtils {
    companion object {
        @JvmStatic
        fun isNetworkAvailable(): Boolean {
            val manager = MyApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return info != null && info.isAvailable
        }

        fun isWifi(): Boolean {
            val manager = MyApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return info != null && info.type==ConnectivityManager.TYPE_WIFI
        }
    }
}