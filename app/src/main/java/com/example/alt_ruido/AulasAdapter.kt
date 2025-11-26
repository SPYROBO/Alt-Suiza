package com.example.alt_ruido

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.alt_ruido.databinding.ItemAulaBinding

class AulasAdapter(private var aulas: List<Aula>) : RecyclerView.Adapter<AulasAdapter.AulaViewHolder>() {

    class AulaViewHolder(val binding: ItemAulaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulaViewHolder {
        val binding = ItemAulaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AulaViewHolder(binding)
    }

    override fun getItemCount(): Int = aulas.size

    override fun onBindViewHolder(holder: AulaViewHolder, position: Int) {
        val aula = aulas[position]
        holder.binding.btnAula.text = aula.num_aula.toString()

        holder.binding.btnAula.setOnClickListener {
            val action = FragmentEscuelaDetalleDirections.actionFragmentEscuelaDetalleToAulaDetalleFragment(aula.num_aula)
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun updateData(newAulas: List<Aula>) {
        this.aulas = newAulas
        notifyDataSetChanged()
    }
}
