/**
 * Created by Spencer Searle on 4/8/22.
 */

package com.zol.updater.coordinator

import androidx.navigation.NavController
import com.zol.updater.navigator.Navigator
import timber.log.Timber
import javax.inject.Inject

class FragmentCoordinator @Inject constructor(): Navigator {
    override lateinit var navController: NavController

    fun isNavControllerInitialized() = this::navController.isInitialized

    fun isNavInitialized(): Boolean {
        return this::navController.isInitialized
    }

    fun start(navController: NavController, onFinished: (() -> Unit)? = null) {
        Timber.d("ZOL Nav Controller Started")
        this.navController = navController
        finished = onFinished
    }

    var finished: (() -> Unit)? = null
}