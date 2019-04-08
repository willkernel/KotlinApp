package com.willkernel.kotlinapp.base

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.willkernel.kotlinapp.MyApplication
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by willkernel
 * on 2019/4/2.
 */

abstract class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    val TAG = BaseActivity::class.java.simpleName
    var mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initData()
        initView()
        initListener()
    }

    /**只有一个参数可以标注为 vararg。如果 vararg 参数不是列表中的最后一个参数， 可以使用命名参数语法传递其后的参数的值，
     * 或者，如果参数具有函数类型，则通过在括号外部传一个 lambda。当我们调用 vararg-函数时，我们可以一个接一个地传参，
     * 例如 asList(1, 2, 3)，或者，如果我们已经有一个数组并希望将其内容传给该函数，我们使用伸展（spread）操作符（在数组前面加 *）*/
    override fun onResume() {
        super.onResume()
        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,Manifest.permission.VIBRATE)
        EasyPermissions.requestPermissions(this, "申请权限", 0, *permissions)
    }

    override fun onPause() {
        super.onPause()
        mCompositeDisposable.clear()
    }

    fun addSubscription(d: Disposable) {
        mCompositeDisposable.add(d)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        val sb = StringBuilder()
        for (perm in perms) {
            sb.append(perm)
            sb.append("\n")
        }
        Log.d(TAG, "sb $sb")

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                .setRationale("need permission $sb")
                .setPositiveButton("ok")
                .setNegativeButton("no")
                .build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.i(TAG, "获取成功的权限$perms")
    }

    abstract fun initData()
    abstract fun initView()
    abstract fun initListener()
    abstract fun getLayoutId(): Int

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.getRefWatcher(this)?.watch(this)
    }

}