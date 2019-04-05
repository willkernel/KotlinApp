package com.willkernel.kotlinapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.willkernel.kotlinapp.utils.DisplayManager
import kotlin.properties.Delegates

class MyApplication : Application() {
    private var refWatcher: RefWatcher? = null

    /**类加载初始化*/
    companion object {
        private val TAG = "MyApplication"
        // 委托 notNull 适用于那些无法在初始化阶段就确定属性值
        var context: Context by Delegates.notNull()
            private set

        fun getRefWatcher(context: Context): RefWatcher? {
            val myApp = context.applicationContext as MyApplication
            return myApp.refWatcher
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        configLogger()
        configCanary()
        DisplayManager.init(this)
        registerActivityLifecycleCallbacks(mActivityLifeCallback)
    }

    private fun configCanary() {
        refWatcher = if (LeakCanary.isInAnalyzerProcess(this)) RefWatcher.DISABLED
        else LeakCanary.install(this)
    }

    /**
     * 初始化配置
     */
    private fun configLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // 隐藏线程信息 默认：显示
            .methodCount(0)         // 决定打印多少行（每一行代表一个方法）默认：2
            .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
            .tag("KotlinApp")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    /**对象表达式,使用时直接初始化*/
    private val mActivityLifeCallback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity?) {
        }

        override fun onActivityResumed(activity: Activity?) {
            Log.d(TAG, "onCreate " + activity?.componentName?.className)
        }

        override fun onActivityStarted(activity: Activity?) {
        }

        override fun onActivityDestroyed(activity: Activity?) {
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            Log.d(TAG, "onCreate " + activity?.componentName?.className)
        }

    }
}