/**
 * Created by Chris Renfrow on 7/6/18.
 */

package com.smartrent.common.ui.extension

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

fun AppCompatActivity.setStatusBarColor(color: Int) {
    window.statusBarColor = color

    //setStatusBarIsLight(color.isLight())

}

fun AppCompatActivity.setStatusBarToDarkerColor(color: Int) {
    val hsv = FloatArray(3)
    Color.colorToHSV(color, hsv)
    hsv[2] *= 0.8f
    setStatusBarColor(Color.HSVToColor(hsv))

}

fun AppCompatActivity.setStatusBarIsLight(isStatusBarLight: Boolean) {
    if (isStatusBarLight) {
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility and (window.decorView.systemUiVisibility.inv())
    }
}

fun AppCompatActivity.setNavigationBarIsLight(isNavigationBarLight: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (isNavigationBarLight) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility and (window.decorView.systemUiVisibility.inv())
        }
    }
}

fun AppCompatActivity.setScreenshotEnabled(enabled: Boolean) {
    if (enabled) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    } else {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

}

inline fun <reified T : ViewModel> AppCompatActivity.getViewModel(): T {
    return ViewModelProvider(this).get(T::class.java)
}

inline fun <reified T : ViewModel> AppCompatActivity.getViewModel(factory: ViewModelProvider.Factory): T {
    return getViewModel(this, factory)
}

inline fun <reified T : ViewModel> AppCompatActivity.getViewModel(
    owner: ViewModelStoreOwner,
    factory: ViewModelProvider.Factory
): T {
    return ViewModelProvider(owner, factory).get(T::class.java)
}
