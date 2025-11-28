package com.example.alt_ruido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.alt_ruido.databinding.FragmentRegisterBinding
import org.json.JSONObject

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val apellido = binding.etApellido.text.toString().trim()
            val correo = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && correo.isNotEmpty() && password.isNotEmpty()) {
                registrarUsuario(nombre, apellido, correo, password)
            } else {
                Toast.makeText(requireContext(), R.string.register_missing_fields, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registrarUsuario(nombre: String, apellido: String, correo: String, pass: String) {
        val url = "${ApiConfig.BASE_URL}/registrar.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.optBoolean("success", false)
                    val message = jsonObject.optString("message", getString(R.string.register_no_server_message))

                    if (success) {
                        Toast.makeText(requireContext(), R.string.register_success, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.register_error, message), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), R.string.register_invalid_response, Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val errorMessage = error.message ?: getString(R.string.register_unknown_network_error)
                Toast.makeText(requireContext(), getString(R.string.register_connection_error, errorMessage), Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["correo"] = correo
                params["password"] = pass
                params["nombre"] = nombre
                params["apellido"] = apellido
                params["rol_id"] = "1"
                return params
            }
        }

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
