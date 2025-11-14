package com.example.alt_ruido

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.alt_ruido.databinding.ItemEscuelaCardBinding
import com.google.android.material.card.MaterialCardView
import kotlin.random.Random

// TOMA LA LISTA DE ESCUELAS PASADA POR ESCUELASLISTFRAGMENT
// y el layout de una tarjeta
// crea, rellena y recicla c/u de las tarjetas
class EscuelasAdapter(private var escuelas: List<Escuela>) :
    RecyclerView.Adapter<EscuelasAdapter.EscuelaViewHolder>() {

    // Clase interna que representa la vista de una tarjeta y usa ViewBinding
    class EscuelaViewHolder(val binding: ItemEscuelaCardBinding) : RecyclerView.ViewHolder(binding.root)

    // crea una nueva lista de tarjetas a partir del xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EscuelaViewHolder {
        val binding = ItemEscuelaCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EscuelaViewHolder(binding)
    }

    // le dice al recyclerview cuantos elementos hay en total
    override fun getItemCount() = escuelas.size

    // Rellena una tarjeta específica
    override fun onBindViewHolder(holder: EscuelaViewHolder, position: Int) {
        val escuela = escuelas[position]
        val context = holder.itemView.context

        // Asignamos los datos a los TextViews usando el binding
        holder.binding.tvNombreEscuela.text = escuela.nombre

        // Simula los decibelios con un número aleatorio para el ejemplo
        val decibelios = Random.nextInt(70, 101)
        holder.binding.tvDecibelios.text = "+${decibelios}db"

        // --- Lógica para cambiar el color de la tarjeta según los decibelios ---
        val (cardColorHex, textColorHex) = when {
            decibelios > 90 -> Pair("#F49494", "#9D0000") // Rojo
            decibelios > 80 -> Pair("#FFC177", "#A65200") // Naranja
            decibelios > 75 -> Pair("#FFEB85", "#7A5F00") // Amarillo
            else -> Pair("#A9E2A2", "#2E7D32")            // Verde
        }

        val cardColor = Color.parseColor(cardColorHex)
        val textColor = Color.parseColor(textColorHex)

        // Aplicamos los colores
        (holder.itemView as MaterialCardView).setCardBackgroundColor(cardColor)
        holder.binding.tvNombreEscuela.setTextColor(textColor)
        holder.binding.tvDecibelios.setTextColor(textColor)
        // El color de "Mas info..." se mantiene negro por defecto.

        holder.itemView.setOnClickListener {
            // --- ESTA ES LA LÍNEA CRÍTICA CORREGIDA ---
            // 1. Usamos la clase del fragmento de ORIGEN (EscuelasListFragment) seguida de "Directions".
            val action = EscuelasListFragmentDirections.actionEscuelasListFragmentToFragmentEscuelaDetalle(escuela)

            // 2. Ejecutamos la navegación
            holder.itemView.findNavController().navigate(action)
        }
    }
    // Función para actualizar los datos del adaptador
    fun updateData(newEscuelas: List<Escuela>) {
        this.escuelas = newEscuelas
        notifyDataSetChanged()
    }
}
