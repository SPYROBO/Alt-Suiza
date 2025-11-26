package com.example.alt_ruido

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.alt_ruido.databinding.ItemEscuelaCardBinding
import com.google.android.material.card.MaterialCardView
import org.json.JSONObject
import kotlin.random.Random

class EscuelasAdapter(private var escuelas: List<Escuela>) :
    RecyclerView.Adapter<EscuelasAdapter.EscuelaViewHolder>() {

    class EscuelaViewHolder(val binding: ItemEscuelaCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EscuelaViewHolder {
        val binding = ItemEscuelaCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EscuelaViewHolder(binding)
    }

    override fun getItemCount() = escuelas.size

    override fun onBindViewHolder(holder: EscuelaViewHolder, position: Int) {
        val escuela = escuelas[position]
        val context = holder.itemView.context

        holder.binding.tvNombreEscuela.text = escuela.nombre

        val decibelios = Random.nextInt(70, 101)
        holder.binding.tvDecibelios.text = "+${decibelios}db"

        val (cardColorHex, textColorHex) = when {
            decibelios > 90 -> Pair("#F49494", "#9D0000")
            decibelios > 80 -> Pair("#FFC177", "#A65200")
            decibelios > 75 -> Pair("#FFEB85", "#7A5F00")
            else -> Pair("#A9E2A2", "#2E7D32")
        }

        (holder.itemView as MaterialCardView).setCardBackgroundColor(Color.parseColor(cardColorHex))
        holder.binding.tvNombreEscuela.setTextColor(Color.parseColor(textColorHex))
        holder.binding.tvDecibelios.setTextColor(Color.parseColor(textColorHex))

        holder.binding.root.setOnClickListener {
            val action = EscuelasListFragmentDirections.actionEscuelasListFragmentToFragmentEscuelaDetalle(escuela)
            holder.itemView.findNavController().navigate(action)
        }

        // --- Lógica de Favoritos ---
        if (SessionManager.isLoggedIn(context)) {
            holder.binding.ivFavoriteStar.isVisible = true

            fun updateStarIcon() {
                if (SessionManager.isFavorite(context, escuela.id)) {
                    holder.binding.ivFavoriteStar.setImageResource(R.drawable.ic_star_filled)
                } else {
                    holder.binding.ivFavoriteStar.setImageResource(R.drawable.ic_star_border)
                }
            }

            updateStarIcon()

            holder.binding.ivFavoriteStar.setOnClickListener {
                toggleFavoriteStatus(context, escuela.id, holder.adapterPosition)
            }
        } else {
            holder.binding.ivFavoriteStar.isVisible = false
        }
    }

    private fun toggleFavoriteStatus(context: Context, escuelaId: Int, position: Int) {
        val userId = SessionManager.getUserId(context)
        val isCurrentlyFavorite = SessionManager.isFavorite(context, escuelaId)

        val url = "https://gangliar-chet-promptly.ngrok-free.dev/api/toggle_fav.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.optBoolean("success") // Usamos optBoolean por seguridad
                    
                    if (success) {
                        if (isCurrentlyFavorite) {
                            SessionManager.removeFavorite(context, escuelaId)
                        } else {
                            SessionManager.addFavorite(context, escuelaId)
                        }
                        notifyItemChanged(position) // Actualiza solo este item
                    } else {
                        // Muestra el mensaje de error del servidor, o uno genérico si no viene
                        val errorMessage = jsonObject.optString("message", "Error al actualizar favorito.")
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error procesando la respuesta: $response", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(context, "Error de red: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "usuario_id" to userId.toString(),
                    "escuela_id" to escuelaId.toString()
                )
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest)
    }

    fun updateData(newEscuelas: List<Escuela>) {
        this.escuelas = newEscuelas
        notifyDataSetChanged()
    }
}
