/**
 * Created by Chris Renfrow on 2019-10-29.
 */

package com.smartrent.common.ui.extension

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

fun Context.dpToPx(dp: Float) = dp * resources.displayMetrics.density

/**
 * in SDK 30, the ability to query device for packages to handle implicit intents was reduced
 * e.g. intent.resolveActivity(packageManager) could return null even if there is actually and app available to handle the intent
 * Therefore, attempt to launch the intent, but catch the exception if one occurs
 */
fun Context.startActivitySafely(intent: Intent, noMatchingActivity: () -> Unit) {
    try {
        startActivity(intent)
    }catch (e: ActivityNotFoundException) {
        noMatchingActivity()
    }
}