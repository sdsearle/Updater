/**
 * Created by Chris Renfrow on 2019-12-13.
 */

package com.zol.updater.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context

class UpdaterDeviceAdminReceiver : DeviceAdminReceiver() {

    companion object {
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context.applicationContext, UpdaterDeviceAdminReceiver::class.java)
        }
    }
}