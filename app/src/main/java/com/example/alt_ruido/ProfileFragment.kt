package com.example.alt_ruido

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.alt_ruido.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        binding.btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        binding.btnLogout.setOnClickListener {
            SessionManager.logout(requireContext())
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        // MODO OSCURO
        val isNightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        binding.switchDarkMode.isChecked = isNightMode

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // VERSION DE LA APP
        try {
            val versionName = requireContext().packageManager
                .getPackageInfo(requireContext().packageName, 0).versionName
            binding.tvAppVersion.text = "Versión de la app: $versionName"
        } catch (e: Exception) {
            binding.tvAppVersion.text = "Versión de la app: N/A"
        }

        // BTN CONTACTO
        binding.tvContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("tu.email@ejemplo.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Reporte de problema en App Alt-Ruido")
            }
            if (activity?.packageManager?.resolveActivity(intent, 0) != null) {
                startActivity(intent)
            }
        }
    }

    private fun updateUI() {
        val isLoggedIn = SessionManager.isLoggedIn(requireContext())
        binding.btnLogout.isVisible = isLoggedIn
        binding.btnGoToLogin.isVisible = !isLoggedIn
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
