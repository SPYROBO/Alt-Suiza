package com.example.alt_ruido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.alt_ruido.databinding.FragmentLoginBinding
import org.json.JSONObject

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (SessionManager.isLoggedIn(requireContext())) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            return
        }

        binding.btnLogin.setOnClickListener {
            val correo = binding.etEmailLogin.text.toString().trim()
            val password = binding.etPasswordLogin.text.toString().trim()

            if (correo.isNotEmpty() && password.isNotEmpty()) {
                loginUsuario(correo, password)
            } else {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun loginUsuario(correo: String, pass: String) {
        val url = "${ApiConfig.BASE_URL}/login.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.optBoolean("success", false)

                    if (success) {
                        val usuarioObject = jsonObject.getJSONObject("usuario")
                        val userId = usuarioObject.optInt("id", -1)

                        if (userId != -1) {
                            SessionManager.login(requireContext(), userId)
                            Toast.makeText(requireContext(), "¡Inicio de sesión exitoso!", Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        } else {
                            Toast.makeText(requireContext(), "Login exitoso, pero no se recibió el ID de usuario desde el servidor.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val message = jsonObject.optString("message", "Correo o contraseña incorrectos.")
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Respuesta inválida del servidor: $response", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val errorMessage = error.message ?: "Error de red desconocido"
                Toast.makeText(requireContext(), "Error de conexión: $errorMessage", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["correo"] = correo
                params["password"] = pass
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
