package com.willkernel.kotlinapp.ui.activity

import android.content.Intent
import android.graphics.Typeface
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.willkernel.kotlinapp.MyApplication
import com.willkernel.kotlinapp.base.BaseActivity
import com.willkernel.kotlinapp.utils.AppUtils
import kotlinx.android.synthetic.main.activity_splash.*


/**
 * Created by willkernel
 * on 2019/4/2.
 */
class SplashActivity : BaseActivity() {
    private var textTypeface: Typeface? = null

    private var descTypeFace: Typeface? = null

    private var alphaAnimation: AlphaAnimation? = null

    init {
        textTypeface = Typeface.createFromAsset(MyApplication.context.assets, "fonts/Lobster-1.4.otf")
        descTypeFace = Typeface.createFromAsset(MyApplication.context.assets, "fonts/FZLanTingHeiS-L-GB-Regular.TTF")
    }

    override fun initListener() {
    }

    override fun initView() {
        tv_app_name.typeface = textTypeface
        tv_splash_desc.typeface = descTypeFace
        tv_version_name.text = "v${AppUtils.getVerName(MyApplication.context)}"

        alphaAnimation = AlphaAnimation(0.5f, 1f)
        alphaAnimation?.duration = 2000
        alphaAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                redirectTo()
            }
        })
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        super.onPermissionsGranted(requestCode, perms)
        iv_web_icon.startAnimation(alphaAnimation)
    }

    private fun redirectTo() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun initData() {
    }

    override fun getLayoutId() = com.willkernel.kotlinapp.R.layout.activity_splash
}