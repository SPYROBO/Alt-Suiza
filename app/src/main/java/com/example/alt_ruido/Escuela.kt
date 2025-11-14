package com.example.alt_ruido

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
// SE DEFINE LA ESTRUCTURA DE UN OBJETO ESCUELA
data class Escuela(
    val id: Int,
    val nombre: String,
    val cue: Int,
    val point_x: Double,
    val point_y: Double,
    val num_calle: Int,
    val calle: String,
    val jornada: String,
    val sector: String,
    val barrio: String,
    val comuna: Int,
    val clave_rama: String,
    val mail: String
): Parcelable
    