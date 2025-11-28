package com.example.alt_ruido

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONException

class EscuelasViewModel : ViewModel() {

    private val _escuelas = MutableLiveData<List<Escuela>>()
    val escuelas: LiveData<List<Escuela>> get() = _escuelas

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val url = "${ApiConfig.BASE_URL}/get_escuelas.php"

    fun cargarEscuelas(context: Context) {
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val listaMutable = mutableListOf<Escuela>()
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val escuela = Escuela(
                            id = jsonObject.optInt("id", 0),
                            nombre = jsonObject.optString("nombre", "Nombre no disponible"),
                            cue = jsonObject.optInt("cue", 0),
                            point_x = jsonObject.optDouble("point_x", 0.0),
                            point_y = jsonObject.optDouble("point_y", 0.0),
                            calle = jsonObject.optString("calle", "Calle no disponible"),
                            num_calle = jsonObject.optInt("num_calle", 0),
                            jornada = jsonObject.optString("jornada", "Jornada no especificada"),
                            sector = jsonObject.optString("sector", "Sector no especificado"),
                            barrio = jsonObject.optString("barrio", "Barrio no especificado"),
                            comuna = jsonObject.optInt("comuna", 0),
                            clave_rama = jsonObject.optString("clave_rama", "N/A"),
                            mail = jsonObject.optString("mail", "Email no disponible")
                        )
                        listaMutable.add(escuela)
                    }
                    _escuelas.value = listaMutable
                } catch (e: JSONException) {
                    _error.value = "Error al procesar la respuesta del servidor: ${e.message}"
                }
            },
            { error ->
                _error.value = "Fallo de conexión con ngrok: ${error.message}"
            }
        )

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }
}
