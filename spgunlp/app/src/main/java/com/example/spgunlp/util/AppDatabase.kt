package com.example.spgunlp.util

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spgunlp.daos.PoligonoDao
import com.example.spgunlp.model.Poligono

@Database(entities = [Poligono::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun poligonoDao(): PoligonoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}