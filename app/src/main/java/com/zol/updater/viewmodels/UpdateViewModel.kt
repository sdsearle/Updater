/**
 * Created by Spencer Searle on 4/13/22.
 */

package com.zol.updater.viewmodels

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import com.zol.updater.BuildConfig
import com.zol.updater.KioskHelper
import java.io.File
import javax.inject.Inject


class UpdateViewModel @Inject constructor(val kioskHelper: KioskHelper) : BaseViewModel() {

    fun onUpdate() {
        kioskHelper.removeAdmin()
    }

    fun installApplication(context: Context, filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            uriFromFile(context, File(filePath)),
            "application/vnd.android.package-archive"
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Log.e("TAG", "Error in opening the file!")
        }
    }

    private fun uriFromFile(context: Context, file: File): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                file
            )
        } else {
            Uri.fromFile(file)
        }
    }

    //TODO: POSSIBLE SOLUTION
    /*//get destination to update file and set Uri
    //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
    //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
    //solution, please inform us in comment
    var destination: String =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/"
    val fileName = "AppName.apk"
    destination += fileName
    val uri: Uri = Uri.parse("file://$destination")

    //Delete update file if exists
    val file = File(destination)
    if (file.exists()) //file.delete() - test this, I think sometimes it doesnt work
        file.delete()

    //get url of app on server
    val url: String = this@Main.getString(R.string.update_app_url)

    //set downloadmanager
    val request = DownloadManager.Request(Uri.parse(url))
    request.setDescription(this@Main.getString(R.string.notification_description))
    request.setTitle(this@Main.getString(R.string.app_name))

    //set destination
    request.setDestinationUri(uri)

    // get download service and enqueue file
    val manager = getSystemService(Context.DOWNLOAD_SERVICE, DownloadManager::class.java) as DownloadManager?
    val downloadId = manager!!.enqueue(request)

    //set BroadcastReceiver to install app when .apk is downloaded
    val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent?) {
            val install = Intent(Intent.ACTION_VIEW)
            install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            install.setDataAndType(
                uri,
                manager!!.getMimeTypeForDownloadedFile(downloadId)
            )
            startActivity(install)
            unregisterReceiver(this)
            finish()
        }
    }
    //register receiver for when .apk download is compete
    registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))*/

}

