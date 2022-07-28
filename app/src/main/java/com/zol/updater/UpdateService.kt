/**
 * Created by Spencer Searle on 7/25/22.
 */

package com.zol.updater

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import timber.log.Timber
import java.io.IOException
import java.util.*


class UpdateService : Service() {
    protected var handler: Handler = Handler(Looper.myLooper()!!)
    protected var mToast: Toast? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post(Runnable {
            // write your code to post content on server
            var p: Process? = null
            try {
                Timber.d("Starting Commands")
                //p = Runtime.getRuntime().exec("su && pm install -r /storage/sdcard0/Download/app-debug.apk && am start -n com.zol.updater/com.zol.updater.activities.MainActivity")
                /* val outs = DataOutputStream(p.outputStream)
                 val cmd = "pm install -r /storage/sdcard0/Download/app-debug.apk &&" +
                          " am force-stop com.zol.updater"
                 outs.writeBytes(cmd+"\n")*/
                //outs.writeBytes("am start -S com.zol.updater/com.zol.updater.activities.MainActivity"+"\n")
                //outs.writeBytes("am force-stop com.zol.updater"+"\n")

                /*p = Runtime.getRuntime().exec("su pm install -r /storage/sdcard0/Download/app-debug.apk")
                Thread.sleep(15000)*/

                /*p = Runtime.getRuntime().exec("su")
                val dos = DataOutputStream(p.outputStream)
                dos.writeBytes("pm install -r /storage/sdcard0/Download/app-debug.apk && reboot now\n")*/
                //p = Runtime.getRuntime().exec("su am start -S -W com.zol.updater/com.zol.updater.activities.MainActivity\n")

                //val command1 = "pm install -r /storage/sdcard0/Download/app-debug.apk"
                //val command2 = "reboot"
                //val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command1, command2))


                //Runtime.getRuntime().exec("su && pm install -r /storage/sdcard0/Download/app-debug.apk")

                //Timber.d(execCmd("su pm install -r /storage/sdcard0/Download/app-debug.apk"))

                /*val command = "su -c install -r ${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}app-debug.apk"
                val pb = ProcessBuilder(command)
                pb.redirectErrorStream(true);
                //val p = Runtime.getRuntime().exec(command)
                val p = pb.start()
                Timber.d("Waiting")
                val reader = BufferedReader(InputStreamReader(p.inputStream))
                var line: String
                while (reader.readLine().also { line = it } != null) Timber.d("tasklist: $line")
                p.waitFor()
                Timber.d("Done Waiting")
                Timber.d("${p.exitValue()}")

                //Runtime.getRuntime().exec("su && reboot")

                Timber.d("Finished Commands")*/


                //stopService(intent)

                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        Timber.d( "ACTION_SCREEN_OFF")
                        // do something, e.g. send Intent to main app
                    }
                }

                val filter = IntentFilter(Intent.ACTION_MY_PACKAGE_REPLACED)
                registerReceiver(receiver, filter)
            } catch (e: IOException) {
                Timber.e(e.message)
            }
        })
        return START_STICKY
    }

    @Throws(IOException::class)
    fun execCmd(cmd: String?): String? {
        val s = Scanner(Runtime.getRuntime().exec(cmd).inputStream)
        while (s.hasNext()){
            Timber.d(s.next())
        }
        return if (s.hasNext()) s.next() else "NA"
    }

}