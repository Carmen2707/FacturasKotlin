package com.example.facturaskotlin.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * FacturaDAO es una interfaz Data Access Object (DAO) de Room.
 */
@Dao
interface FacturaDAO {

    /**
     * Recupera todas las facturas de la base de datos.
     */
    @Query("SELECT * FROM tabla_facturas")
    fun getAll(): LiveData<List<Factura>>

    /**
     * Inserta una factura en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(factura: Factura)

    /**
     * Elimina todas las facturas de la tabla.
     */
    @Query("DELETE FROM tabla_facturas")
    fun deleteAll()
}