package com.ejemplo.kilometro_a_kilometro.utils

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "user_session",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
    }

    /**
     * Guarda la sesión del usuario activo
     */
    fun saveSession(userId: Int, username: String) {
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    /**
     * Indica si hay un usuario logueado
     */
    fun isLoggedIn(): Boolean {
        return prefs.contains(KEY_USER_ID)
    }

    /**
     * Devuelve el ID del usuario activo
     */
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    /**
     * Devuelve el username del usuario activo
     */
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    /**
     * Borra la sesión (logout)
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
