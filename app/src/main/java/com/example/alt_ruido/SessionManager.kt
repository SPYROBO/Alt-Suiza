package com.example.alt_ruido

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    private const val PREFS_NAME = "alt_ruido_prefs"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_USER_ID = "userId"
    private const val KEY_FAVORITE_ESCUELAS = "favoriteEscuelas"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // --- User Session ---
    fun isLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun login(context: Context, userId: Int) {
        getPreferences(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putInt(KEY_USER_ID, userId)
            apply()
        }
    }

    fun logout(context: Context) {
        getPreferences(context).edit().apply {
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_USER_ID)
            remove(KEY_FAVORITE_ESCUELAS) // Importante: Limpiar favoritos al cerrar sesión
            apply()
        }
    }

    fun getUserId(context: Context): Int {
        return getPreferences(context).getInt(KEY_USER_ID, -1)
    }

    // --- Favorites ---

    /**
     * Reemplaza la lista local de favoritos con la lista del servidor.
     */
    fun setFavorites(context: Context, favoriteIds: Set<String>) {
        getPreferences(context).edit().apply {
            putStringSet(KEY_FAVORITE_ESCUELAS, favoriteIds)
            apply()
        }
    }

    fun getFavoriteIds(context: Context): Set<Int> {
        val favorites = getPreferences(context).getStringSet(KEY_FAVORITE_ESCUELAS, emptySet()) ?: emptySet()
        return favorites.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun isFavorite(context: Context, escuelaId: Int): Boolean {
        val favorites = getFavoriteIds(context)
        return favorites.contains(escuelaId)
    }

    fun addFavorite(context: Context, escuelaId: Int) {
        val favorites = getFavoriteIds(context).map { it.toString() }.toMutableSet()
        favorites.add(escuelaId.toString())
        setFavorites(context, favorites)
    }

    fun removeFavorite(context: Context, escuelaId: Int) {
        val favorites = getFavoriteIds(context).map { it.toString() }.toMutableSet()
        favorites.remove(escuelaId.toString())
        setFavorites(context, favorites)
    }
}
