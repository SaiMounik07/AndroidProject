package com.example.androidlearning.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.androidlearning.base.constants.Constants.PREFS_FILE_LOGIN
import com.example.androidlearning.base.utils.SharedPreferenceUtility
import com.example.androidlearning.data.db.AppDatabase
import com.example.androidlearning.data.db.dao.ProductDao
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
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
            .build()
    }

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

}