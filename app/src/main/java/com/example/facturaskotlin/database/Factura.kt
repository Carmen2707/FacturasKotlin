package com.example.facturaskotlin.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_facturas")
class Factura(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fecha: String,
    val estado: String,
    val importe: Double
) {
}