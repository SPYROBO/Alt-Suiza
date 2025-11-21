package com.example.alt_ruido

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.alt_ruido.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // INICIA CUANDO LA APP SE ABRE, PUNTO DE ENTRADA PRINCIPAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //INICIO DEL CÓDIGO CRUCIAL

        // 1. Encontrar el NavController desde el NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        // 2. Conectar la BottomNavigationView con el NavController
        // Esta línea hace que los botones de la barra inferior funcionen.
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
