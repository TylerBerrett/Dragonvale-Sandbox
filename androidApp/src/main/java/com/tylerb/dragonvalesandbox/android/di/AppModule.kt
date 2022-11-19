package com.tylerb.dragonvalesandbox.android.di

import android.content.Context
import com.squareup.sqldelight.db.SqlDriver
import com.tylerb.dragonvalesandbox.SharedRepository
import com.tylerb.dragonvalesandbox.database.DatabaseDriverFactory
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
    fun provideSqlDriver(@ApplicationContext context: Context): SqlDriver =
        DatabaseDriverFactory(context).createDriver()

    @Provides
    fun provideSharedRepo(preferences: Preferences, sqlDriver: SqlDriver): SharedRepository =
        SharedRepository(preferences, sqlDriver)

}