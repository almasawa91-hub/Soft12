package com.aalmoghalis.muhasibsoft.di
import android.content.Context
import androidx.room.Room
import com.aalmoghalis.muhasibsoft.data.local.AppDatabase
import com.aalmoghalis.muhasibsoft.data.local.ItemDao
import com.aalmoghalis.muhasibsoft.data.local.SaleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "muhasib_soft.db").build()
    @Provides fun provideItemDao(database: AppDatabase): ItemDao = database.itemDao()
    @Provides fun provideSaleDao(database: AppDatabase): SaleDao = database.saleDao()
}
