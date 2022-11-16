package com.tylerb.dragonvalesandbox.android.di

import android.content.Context
import com.tylerb.dragonvalesandbox.SharedRepository
import com.tylerb.dragonvalesandbox.storage.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideCommonPreferences(@ApplicationContext context: Context): Preferences =
        Preferences(context)

    @Provides
    fun provideSharedRepo(preferences: Preferences): SharedRepository =
        SharedRepository(preferences)

}