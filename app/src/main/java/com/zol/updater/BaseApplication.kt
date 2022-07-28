package com.zol.updater

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import timber.log.Timber

/**
 * Created by Chris Renfrow on 2019-10-16.
 */
abstract class BaseApplication : Application(),
    Application.ActivityLifecycleCallbacks,
    LifecycleObserver {

    companion object {
        lateinit var instance: BaseApplication private set
        var currentActivity: Activity? = null
    }

    lateinit var application: Application

    val activity: Activity? get() = currentActivity
    override fun onCreate() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        instance = this
        application = this

        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onBackground() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onForeground() {

    }

    override fun onActivityPaused(activity: Activity) {
        Timber.i("${activity::class.simpleName} Paused")

        if (currentActivity == activity) {
            currentActivity = null
        }
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.i("${activity::class.simpleName} Started")
        currentActivity = activity

    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.i("${activity::class.simpleName} Destroyed")
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
        Timber.i("${activity::class.simpleName} SaveInstanceState")
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.i("${activity::class.simpleName} Stopped")
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        Timber.i("${activity::class.simpleName} Created")
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.i("${activity::class.simpleName} Resumed")
        currentActivity = activity

    }

    /* I suspect that at some point multidex will be required.
     * When that time comes, we need the code below along with the
     * api dependecy on androidx.multidex:multidex:<version> to
     * allow multidex

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }*/
}
