package com.example.facturaskotlin.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FacturaDAO {
    @Query("SELECT * FROM tabla_facturas")
    fun getAll(): LiveData<List<Factura>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(factura: Factura)

    @Query("DELETE FROM tabla_facturas")
    fun deleteAll()
}