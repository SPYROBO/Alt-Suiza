package com.example.alt_ruido

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.alt_ruido.databinding.ItemEscuelaCardBinding
import com.google.android.material.card.MaterialCardView
import kotlin.random.Random

// Hereda de RecyclerView.Adapter. Le decimos que va a manejar una lista de 'Escuela'
// y que cada item visual sera de tipo 'Escuelaviewholder'
class EscuelasAdapter(private var escuelas: List<Escuela>) :
    RecyclerView.Adapter<EscuelasAdapter.EscuelaViewHolder>() {

        // CONTIENE LAS REFERENCIAS A UNA SOLA TARJETA
    class EscuelaViewHolder(val binding: ItemEscuelaCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EscuelaViewHolder {
        val binding = ItemEscuelaCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EscuelaViewHolder(binding)
    }

    override fun getItemCount() = escuelas.size

    // Rellena una tarjeta específica CON LA LOGICA DE COLORES
    override fun onBindViewHolder(holder: EscuelaViewHolder, position: Int) {
        val escuela = escuelas[position]

        // Asignamos el nombre de la escuela
        holder.binding.tvNombreEscuela.text = escuela.nombre

        // Simula los decibelios con un número aleatorio por ahora
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

        // Aplicamos los colores a la tarjeta
        (holder.itemView as MaterialCardView).setCardBackgroundColor(cardColor)
        holder.binding.tvNombreEscuela.setTextColor(textColor)
        holder.binding.tvDecibelios.setTextColor(textColor)

        // Lógica para navegar al hacer clic
        holder.itemView.setOnClickListener {
            val action = EscuelasListFragmentDirections.actionEscuelasListFragmentToFragmentEscuelaDetalle(escuela)
            holder.itemView.findNavController().navigate(action)
        }
    }

    // ACTUALIZA LA VISTA AL MOMENTO DE USAR EL BUSCADOT
    fun updateData(newEscuelas: List<Escuela>) {
        this.escuelas = newEscuelas
        notifyDataSetChanged() // Esto actualiza el RecyclerView"
    }
}
