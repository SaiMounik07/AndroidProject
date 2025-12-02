package com.example.androidlearning.di

import android.content.Context
import android.content.SharedPreferences
import com.example.androidlearning.base.constants.Constants.PREFS_FILE_LOGIN
import com.example.androidlearning.base.utils.SharedPreferenceUtility
import com.example.androidlearning.data.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AndroidLearningModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_FILE_LOGIN, Context.MODE_PRIVATE)
    }
    @Provides
    fun providesSharedPreferenceUtility(sharedPreferences: SharedPreferences): SharedPreferenceUtility {
        return SharedPreferenceUtility(sharedPreferences)
    }


}