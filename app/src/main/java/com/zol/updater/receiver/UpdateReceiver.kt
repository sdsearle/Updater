/**
 * Created by Spencer Searle on 7/19/22.
 */

package com.zol.updater.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import timber.log.Timber

class UpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Timber.d(log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
            /*val i: Intent? = context.packageManager
                .getLaunchIntentForPackage(context.packageName)
            i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)*/
            //Runtime.getRuntime().exec("su && reboot")
        }
    }
}