package com.zol.updater.activities

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageInstaller
import android.content.pm.PackageInstaller.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.fragment.NavHostFragment
import com.smartrent.common.ui.extension.replace
import com.zol.updater.BuildConfig
import com.zol.updater.KioskHelper
import com.zol.updater.R
import com.zol.updater.UpdateService
import com.zol.updater.coordinator.FragmentCoordinator
import com.zol.updater.databinding.ActivityMainBinding
import com.zol.updater.fragments.UpdateFragment
import com.zol.updater.viewmodels.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.*
import javax.inject.Inject


private const val NAME = "mostly-unused"
private const val PI_INSTALL = 3439

@AndroidEntryPoint
class MainActivity : MVVMActivity<ActivityMainBinding, BaseViewModel>() {

    @Inject
    lateinit var fragmentCoordinator: FragmentCoordinator

    override val layoutID: Int = R.layout.activity_main

    val context = this

    @Inject
    lateinit var kioskHelper: KioskHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            Runtime.getRuntime()
                .exec("dpm set-device-owner com.example.deviceowner/.MyDeviceAdminReceiver")
            Timber.d("Device is now Admin")
        } catch (e: java.lang.Exception) {
            Timber.e("ZOL device owner not set")
            Timber.e(e.toString())
            e.printStackTrace()
        }

        val a = getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        a.edit().putString("value","Saved").apply()
        val mString = a.getString("value","Not Saved")
        Timber.d("ZOL isSaved: $mString")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(
                        Uri.parse(String.format("package:%s", getPackageName()))
                    ), 1234
                );
            } else {
                startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(
                        Uri.parse(String.format("package:%s", getPackageName()))
                    ), 1234
                )
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
        }

        Timber.d("ZOL Command ran")

        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
    }

    fun installAPK(uri: Uri) {
        val PATH: String? = uri.path
        val file = File(PATH)
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                uriFromFile(applicationContext, File(PATH)),
                "application/vnd.android.package-archive"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                applicationContext.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Log.e("TAG", "Error in opening the file!")
            }
        } else {
            Toast.makeText(applicationContext, "installing", Toast.LENGTH_LONG).show()
        }
    }

    fun uriFromFile(context: Context?, file: File?): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context!!, BuildConfig.APPLICATION_ID + ".provider",
                file!!
            )
        } else {
            Uri.fromFile(file)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_CANCELED && data != null) {
            if (requestCode == 111 && resultCode == RESULT_OK) {
                val mime = MimeTypeMap.getSingleton()
                val selectedFile = data?.data //The uri with the location of the file
                selectedFile?.let {
                    mime.getExtensionFromMimeType(contentResolver.getType(it))
                        ?.let { type ->
                            Log.d("ZOL", type)

                            Timber.d("ZOL: apk: su -c adb install -r ${selectedFile.path}")
                            Timber.d("/sdcard/Download/app-debug.apk")
                            //kioskHelper.updateApp(selectedFile, this)

                            //TODO: This works for 8+
                            /*val f = File("/storage/sdcard0/Download/app-debug.apk")
                            Timber.d("File exists: ${f.exists()}")
                            contentResolver.openInputStream(Uri.fromFile(File("/storage/sdcard0/Download/app-debug.apk")))
                                ?.let { file -> installPackage(this, "123",
                                                               "com.zol.updater", file) }*/

                            /*val mStartActivity = Intent(context, this::class.java)
                            val mPendingIntentId = 123456
                            val mPendingIntent = PendingIntent.getActivity(
                                context,
                                mPendingIntentId,
                                mStartActivity,
                                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                            val mgr = context.getSystemService(ALARM_SERVICE) as AlarmManager
                            mgr[AlarmManager.RTC, System.currentTimeMillis() + 100] =
                                mPendingIntent*/

                            //TODO: This works for android 5
                            var p: Process? = null
                            try {
                                p = Runtime.getRuntime().exec("su && pm install -r /storage/sdcard0/Download/app-debug.apk")
                            } catch (e: IOException) {
                                Timber.e(e.message)
                            }

                            //startService(Intent(this, UpdateService::class.java))

                            //selectedFile.path?.let { file -> InstallAPK(file) }

                            /******ANOTHER PROCESS*****/
                            /*var p: Process? = null
                            try {
                                p = Runtime.getRuntime().exec("su")
                                val outs = DataOutputStream(p.outputStream)
                                outs.writeBytes("mount -o rw,remount /system &&" +
                                                        " pm install -r /storage/sdcard0/Download/app-debug.apk &&" +
                                                        " cp /storage/sdcard0/Download/app-debug.apk /system/app/app-debug.apk &&" +
                                                        " svc power reboot")

                            } catch (e: IOException) {
                                Timber.e(e.message)
                            }*/


                        }
                }
            }
        }
    }

    private fun runCmd(outs: DataOutputStream, cmd: String){
        outs.writeBytes(cmd+"\n")
    }

    private val PACKAGE_INSTALLED_ACTION =
        "com.example.android.apis.content.SESSION_API_PACKAGE_INSTALLED"

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val extras: Bundle? = intent?.extras
        if (PACKAGE_INSTALLED_ACTION.equals(intent?.action)) {
            val status = extras?.getInt(PackageInstaller.EXTRA_STATUS)
            val message = extras?.getString(PackageInstaller.EXTRA_STATUS_MESSAGE)
            when (status) {
                STATUS_PENDING_USER_ACTION              -> {
                    // Ask user to confirm the installation
                    val confirmIntent = extras.get(Intent.EXTRA_INTENT) as Intent
                    startActivity(confirmIntent)
                }
                STATUS_SUCCESS                          -> {
                }
                PackageInstaller.STATUS_FAILURE, PackageInstaller.STATUS_FAILURE_ABORTED,
                PackageInstaller.STATUS_FAILURE_BLOCKED, PackageInstaller.STATUS_FAILURE_CONFLICT,
                PackageInstaller.STATUS_FAILURE_INCOMPATIBLE, PackageInstaller.STATUS_FAILURE_INVALID,
                PackageInstaller.STATUS_FAILURE_STORAGE -> {
                }
            }
        }
    }

    private fun createIntentSender(context: Context, sessionId: Int): IntentSender? {
        val ACTION_INSTALL_COMPLETE = "cm.android.intent.action.INSTALL_COMPLETE"
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId,
            Intent(ACTION_INSTALL_COMPLETE),
            PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent.intentSender
    }

    override fun onResume() {
        super.onResume()
        Timber.d("ZOL Current Version: ${BuildConfig.VERSION_NAME}")

        val fragment = NavHostFragment.create(R.navigation.navigation_fragment)

        Timber.d("ZOL Fragment replacing")
        supportFragmentManager.replace(
            R.id.content,
            false,
            UpdateFragment.tag,
            R.anim.anim_enter_rtl,
            R.anim.anim_exit_rtl,
            R.anim.anim_enter_ltr,
            R.anim.anim_exit_ltr
        ) {
            fragment
        }
    }

    fun installPackage(
        context: Context, installSessionId: String?,
        packageName: String?,
        apkStream: InputStream
    ) {
        val packageManger = context.packageManager
        val packageInstaller = packageManger.packageInstaller
        val params = SessionParams(
            SessionParams.MODE_FULL_INSTALL
        )
        params.setAppPackageName(packageName)
        var session: Session? = null
        try {
            val sessionId = packageInstaller.createSession(params)
            session = packageInstaller.openSession(sessionId)
            val out = session.openWrite(installSessionId!!, 0, -1)
            val buffer = ByteArray(1024)
            var length: Int
            var count = 0
            while (apkStream.read(buffer).also { length = it } != -1) {
                out.write(buffer, 0, length)
                count += length
            }
            session.fsync(out)
            out.close()
            val intent = Intent(Intent.ACTION_PACKAGE_ADDED)
            session.commit(
                PendingIntent.getBroadcast(
                    context, sessionId,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                ).intentSender
            )
        } catch (e: Error){
            Timber.e(e.message)
        }
        finally {
            session?.close()
        }
    }

}