/**
 * Created by Chris Renfrow on 2019-10-16.
 */

package com.zol.updater.inject

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ContextModule {

    @Binds
    @Singleton
    abstract fun provideContext(@ApplicationContext context: Context): Context
}