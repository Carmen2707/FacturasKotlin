package com.example.facturaskotlin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Factura::class], version = 1, exportSchema = false)
abstract class FacturaDB : RoomDatabase() {
    abstract fun getAppDao(): FacturaDAO

    companion object {
        private var DB_INSTANCE: FacturaDB? = null

        fun getAppDBInstance(context: Context): FacturaDB {
            if (DB_INSTANCE == null) {
                DB_INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    FacturaDB::class.java,
                    "invoice_database"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return DB_INSTANCE!!
        }
    }
}