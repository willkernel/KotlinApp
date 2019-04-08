package com.willkernel.kotlinapp.utils

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import com.willkernel.kotlinapp.ui.activity.WatchHistoryActivity


/**
通知
 */
class NotificationUtils {
    companion object {
        const val CHANNEL_ID = "kotlinApp"
        @TargetApi(Build.VERSION_CODES.O)
        fun showTextNotification(activity: Activity) {
            var nm = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //android 8.0以上需要特殊处理，也就是targetSDKVersion为26以上
            var notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)//闪光灯
            notificationChannel.enableVibration(true)//是否允许震动
            notificationChannel.canShowBadge()//桌面launcher的消息角标
            notificationChannel.canBypassDnd()//是否绕过请勿打扰模式
            notificationChannel.setBypassDnd(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET//锁屏显示通知
            notificationChannel.lightColor = Color.RED
//            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300)
//            notificationChannel.shouldShowLights()
//            notificationChannel.shouldVibrate()
//            notificationChannel.setSound(
//                android.provider.Settings.System.DEFAULT_RINGTONE_URI,
//                Notification.AUDIO_ATTRIBUTES_DEFAULT
//            )
            notificationChannel.setSound(
                Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "2"),
                Notification.AUDIO_ATTRIBUTES_DEFAULT
            )


            nm.createNotificationChannel(notificationChannel)


            var mBuilder = Notification.Builder(activity, CHANNEL_ID)
            mBuilder.setAutoCancel(false)
                .setContentText("content")
                .setContentTitle("title")
                .setSmallIcon(com.willkernel.kotlinapp.R.mipmap.ic_action_collection)
                .setOngoing(true)
//                .setBadgeIconType(Notification.BADGE_ICON_SMALL)
//                .setTicker("ticker")
//                .setWhen()


            var intent = Intent(activity, WatchHistoryActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("data", 2)
            val stackBuilder = TaskStackBuilder.create(activity)
// Adds the back stack
            stackBuilder.addParentStack(WatchHistoryActivity::class.java)
// Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(intent)


//            val pendingIntent = PendingIntent.getActivity(activity, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            mBuilder.setContentIntent(pendingIntent)
            nm.notify(1, mBuilder.build())
//            nm.cancel(10)
        }

        fun openChannelSetting(channelId: String, activity: Activity) {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId)

            if (activity.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null)
                activity.startActivity(intent)
        }

        fun openNotificationSetting(activity: Activity) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
            if (activity.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null)
                activity.startActivity(intent)
        }
    }
}