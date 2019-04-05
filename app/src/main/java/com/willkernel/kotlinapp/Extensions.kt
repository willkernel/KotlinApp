package com.willkernel.kotlinapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.willkernel.kotlinapp.mvp.model.HomeBean
import com.willkernel.kotlinapp.ui.activity.VideoDetailActivity
import com.willkernel.kotlinapp.utils.AppUtils


/**
 * Created by xuhao on 2017/11/14.
 */

fun Fragment.showToast(content: String): Toast {
    val toast = Toast.makeText(this.activity?.applicationContext, content, Toast.LENGTH_SHORT)
    toast.show()
    return toast
}

fun Context.showToast(content: String): Toast {
    val toast = Toast.makeText(MyApplication.context, content, Toast.LENGTH_SHORT)
    toast.show()
    return toast
}


fun View.dip2px(dipValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

fun View.px2dip(pxValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

fun durationFormat(duration: Long?): String {
    val minute = duration!! / 60
    val second = duration % 60
    return if (minute <= 9) {
        if (second <= 9) {
            "0$minute' 0$second''"
        } else {
            "0$minute' $second''"
        }
    } else {
        if (second <= 9) {
            "$minute' 0$second''"
        } else {
            "$minute' $second''"
        }
    }
}

/**
 * 数据流量格式化
 */
fun Context.dataFormat(total: Long): String {
    var result: String
    var speedReal: Int = (total / (1024)).toInt()
    result = if (speedReal < 512) {
        speedReal.toString() + " KB"
    } else {
        val mSpeed = speedReal / 1024.0
        (Math.round(mSpeed * 100) / 100.0).toString() + " MB"
    }
    return result
}

/**
 * 跳转到视频详情页面播放
 *
 * @param activity
 * @param view
 */
fun goToVideoPlayer(activity: Activity, view: View, itemData: HomeBean.Issue.Item) {
    val intent = Intent(activity, VideoDetailActivity::class.java)
    intent.putExtra(Constants.BUNDLE_VIDEO_DATA, itemData)
    intent.putExtra(VideoDetailActivity.TRANSITION, true)
    if (AppUtils.isOverBuildVersion(Build.VERSION_CODES.LOLLIPOP)) {
        val pair = androidx.core.util.Pair(view, VideoDetailActivity.IMG_TRANSITION)
        val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity, pair
        )
        ActivityCompat.startActivity(activity, intent, activityOptions.toBundle())

    } else {
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
    }
}