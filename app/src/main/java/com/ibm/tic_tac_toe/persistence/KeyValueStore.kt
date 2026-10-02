package com.ibm.tic_tac_toe.persistence

/**
 * The small subset of key-value storage [GameStateRepository] needs, kept separate
 * from `android.content.SharedPreferences` so the repository's encode/decode logic
 * can be unit tested on the JVM against a fake implementation.
 */
interface KeyValueStore {
    fun getString(key: String, default: String): String
    fun putString(key: String, value: String)
    fun getInt(key: String, default: Int): Int
    fun putInt(key: String, value: Int)
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
}
