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
            clear()
            apply()
        }
    }

    fun getUserId(context: Context): Int {
        return getPreferences(context).getInt(KEY_USER_ID, -1)
    }

    // --- Favorites ---
    fun getFavoriteIds(context: Context): Set<Int> {
        val favorites = getPreferences(context).getStringSet(KEY_FAVORITE_ESCUELAS, emptySet()) ?: emptySet()
        return favorites.map { it.toInt() }.toSet()
    }

    fun isFavorite(context: Context, escuelaId: Int): Boolean {
        val favorites = getPreferences(context).getStringSet(KEY_FAVORITE_ESCUELAS, emptySet()) ?: emptySet()
        return favorites.contains(escuelaId.toString())
    }

    fun addFavorite(context: Context, escuelaId: Int) {
        val favorites = getPreferences(context).getStringSet(KEY_FAVORITE_ESCUELAS, emptySet())?.toMutableSet() ?: mutableSetOf()
        favorites.add(escuelaId.toString())
        getPreferences(context).edit().putStringSet(KEY_FAVORITE_ESCUELAS, favorites).apply()
    }

    fun removeFavorite(context: Context, escuelaId: Int) {
        val favorites = getPreferences(context).getStringSet(KEY_FAVORITE_ESCUELAS, emptySet())?.toMutableSet() ?: mutableSetOf()
        favorites.remove(escuelaId.toString())
        getPreferences(context).edit().putStringSet(KEY_FAVORITE_ESCUELAS, favorites).apply()
    }
}
