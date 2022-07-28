/**
 * Created by Chris Renfrow on 6/8/20.
 */

package com.zol.updater

import android.annotation.SuppressLint
import android.app.Application
import android.app.admin.DevicePolicyManager
import android.app.admin.SystemUpdatePolicy
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.UserManager
import android.provider.Settings
import android.view.View
import android.view.Window
import com.zol.updater.activities.MainActivity
import com.zol.updater.receiver.UpdaterDeviceAdminReceiver
import timber.log.Timber
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KioskHelper @Inject constructor(
    val application: Application,
    val context: Context
) {

    private var decorView: View? = null
    val LOCK_TASK_KEY: String = "com.alloy.fusion.LOCK_TASK"

    val STATUS_BAR_DISABLE_NONE = 0x00000000
    val STATUS_BAR_DISABLE_HOME = 0x00200000
    val STATUS_BAR_DISABLE_BACK = 0x00400000
    val STATUS_BAR_DISABLE_CLOCK = 0x00800000
    val STATUS_BAR_DISABLE_RECENT = 0x01000000
    val STATUS_BAR_DISABLE_SEARCH = 0x02000000
    val STATUS_BAR_DISABLE_EXPAND = 0x00010000
    val STATUS_BAR_DISABLE_NOTIFICATION_ICONS = 0x00020000
    val STATUS_BAR_DISABLE_NOTIFICATION_ALERTS = 0x00040000
    val STATUS_BAR_DISABLE_SYSTEM_INFO = 0x00100000

    private val devicePolicyManager: DevicePolicyManager by lazy {
        application.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }

    private val adminComponentName by lazy { UpdaterDeviceAdminReceiver.getComponentName(application) }

    fun lockTask(lock: Boolean? = null) {
        val shouldLock = if (lock == null) {
            //sharedPrefs.getBoolean(LOCK_TASK_KEY, true)
        } else {
            //sharedPrefs.setBoolean(LOCK_TASK_KEY, lock)
            lock
        }

        decorView?.let {
            setSystemUIVisibility(it)
        }
        if (devicePolicyManager.isDeviceOwnerApp(application.packageName)) {
            //setKioskProperties(enable = shouldLock, isAdmin = shouldLock)
        }
    }

    @Suppress("SameParameterValue")
    private fun setKioskProperties(enable: Boolean, isAdmin: Boolean) {
        if (isAdmin) {
            setRestrictions(enable)
            //enableStayOnWhilePluggedIn(enable)
            setUpdatePolicy(enable)
            setAsHomeApp(enable)
            setKeyGuardDisabled(enable)
        }
        setLockTask(enable, isAdmin)
    }

    // region restrictions
    private fun setRestrictions(disallow: Boolean) {
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, disallow)
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, disallow)
        setUserRestriction(UserManager.DISALLOW_ADD_USER, disallow)
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, disallow)
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, disallow)
    }

    private fun setUserRestriction(restriction: String, disallow: Boolean) = if (disallow) {
        devicePolicyManager.addUserRestriction(adminComponentName, restriction)
    } else {
        devicePolicyManager.clearUserRestriction(adminComponentName, restriction)
    }
    // endregion

    private fun enableStayOnWhilePluggedIn(active: Boolean) = if (active) {
        devicePolicyManager.setGlobalSetting(
            adminComponentName,
            Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
            (BatteryManager.BATTERY_PLUGGED_AC
                    or BatteryManager.BATTERY_PLUGGED_USB
                    or BatteryManager.BATTERY_PLUGGED_WIRELESS).toString()
        )
    } else {
        devicePolicyManager.setGlobalSetting(
            adminComponentName,
            Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
            "0"
        )
    }

    fun updateApp(uri: Uri, context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                devicePolicyManager.installSystemUpdate(adminComponentName, uri, context.mainExecutor, object: DevicePolicyManager.InstallSystemUpdateCallback(){
                    override fun onInstallUpdateError(errorCode: Int, errorMessage: String) {
                        Timber.e("ERROR $errorCode: $errorMessage")
                        super.onInstallUpdateError(errorCode, errorMessage)
                    }
                })
            }
        }
    }

    private fun setLockTask(start: Boolean, isAdmin: Boolean) {

        if (isAdmin) {
            Timber.d("CWR - SetLockTask is admin, start = $start")
            devicePolicyManager.setLockTaskPackages(
                adminComponentName,
                if (start) arrayOf(
                    application.packageName,
                    "com.zipato.android.sip.server",
                    "com.zipato.android.webcam",
                    "com.zipato.android.zipabox",
                    "com.zipato.android.zipatile",
                    "com.zipato.zipatile",
                    "org.fdroid.zipatile2"
                ) else arrayOf()
            )
        }
        if (start) {
            Timber.d("Starting lock task")
            (application as App).activity?.startLockTask()
        } else {
            Timber.d("Stopping lock task")
            (application as App).activity?.stopLockTask()
        }
    }

    private fun setUpdatePolicy(enable: Boolean) {
        if (enable) {
            devicePolicyManager.setSystemUpdatePolicy(
                adminComponentName,
                SystemUpdatePolicy.createWindowedInstallPolicy(60, 120)
            )
        } else {
            devicePolicyManager.setSystemUpdatePolicy(adminComponentName, null)
        }
    }

    private fun setAsHomeApp(enable: Boolean) {
        if (enable) {
            val intentFilter = IntentFilter(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
            }
            devicePolicyManager.addPersistentPreferredActivity(
                adminComponentName,
                intentFilter,
                ComponentName(application.packageName, MainActivity::class.java.name)
            )
        } else {
            devicePolicyManager.clearPackagePersistentPreferredActivities(
                adminComponentName, application.packageName
            )
        }
    }

    private fun setKeyGuardDisabled(disable: Boolean) {
        devicePolicyManager.setKeyguardDisabled(adminComponentName, disable)
    }

    private fun setStatusBarFlags(flags: Int) {
        @SuppressLint("WrongConstant")
        val statusBarService: Any =
            application.getSystemService("statusbar")

        // Use reflection to trigger a method from 'StatusBarManager'
        var statusBarManager: Class<*>? = null


        try {
            statusBarManager = Class.forName("android.app.StatusBarManager")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        var disable: Method? = null

        try {
            disable = statusBarManager?.getMethod("disable", Int::class.javaPrimitiveType)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }

        disable?.isAccessible = true

        try {
            disable?.invoke(statusBarService, flags)

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    fun initFullscreen(window: Window) {
        // Code below handles hiding the system status and nav bars
        decorView = window.decorView
        decorView?.let {
            setSystemUIVisibility(it)
            it
                .setOnSystemUiVisibilityChangeListener { visibility ->
                    if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                        setSystemUIVisibility(it)

                    }
                }
        }
    }

    private fun setSystemUIVisibility(decorView: View) {
        val immersiveFlags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        val nonimmersiveFlags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        /*if (sharedPrefs.getBoolean(LOCK_TASK_KEY, true)) {
            decorView.systemUiVisibility = immersiveFlags
            setStatusBarFlags(
                STATUS_BAR_DISABLE_HOME
                        or STATUS_BAR_DISABLE_RECENT
                        or STATUS_BAR_DISABLE_BACK
                        or STATUS_BAR_DISABLE_EXPAND
                        or STATUS_BAR_DISABLE_NOTIFICATION_ICONS
                        or STATUS_BAR_DISABLE_NOTIFICATION_ALERTS
                        or STATUS_BAR_DISABLE_CLOCK
                        or STATUS_BAR_DISABLE_SYSTEM_INFO
                        or STATUS_BAR_DISABLE_SEARCH
            )
        } else {
            decorView.systemUiVisibility = nonimmersiveFlags
            setStatusBarFlags(STATUS_BAR_DISABLE_NONE)
        }*/
    }

    fun removeAdmin(){
            val devAdminReceiver = ComponentName(context, UpdaterDeviceAdminReceiver::class.java)
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                dpm.removeActiveAdmin(devAdminReceiver)
    }
}