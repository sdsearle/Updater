/**
 * Created by Chris Renfrow on 6/18/20.
 */

package com.zol.updater.navigator

import android.net.Uri
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions

interface Navigator {
    val navController: NavController

    fun navigateUp(@IdRes navID: Int? = null) {
        navID?.let {
            navController.popBackStack(it, false)

        } ?: navController.navigateUp()
    }

    fun navigate(@IdRes navID: Int) {
        navController.navigate(navID)
    }

    fun navigate(direction: NavDirections) {
        navController.navigate(direction)
    }

    fun navigate(uri: Uri) {
        navController.navigate(uri)
    }

    fun navigate(@IdRes navID: Int, bundle: Bundle) {
        navController.navigate(navID, bundle)
    }

    fun navigate(@IdRes navID: Int, bundle: Bundle, navOptions: NavOptions?) {
        navController.navigate(navID, bundle, navOptions)
    }
}