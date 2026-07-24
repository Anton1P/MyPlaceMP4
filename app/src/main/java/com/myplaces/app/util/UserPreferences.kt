package com.myplaces.app.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

object UserPreferencesKeys {
    val USER_UUID = stringPreferencesKey("user_uuid")
    val USER_NAME = stringPreferencesKey("user_name")
    val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
}

/** Initialise l'UUID et le nom au premier lancement (idempotent). */
suspend fun initUserIfNeeded(dataStore: DataStore<Preferences>) {
    dataStore.edit { prefs ->
        if (prefs[UserPreferencesKeys.USER_UUID] == null) {
            prefs[UserPreferencesKeys.USER_UUID] = UUID.randomUUID().toString()
            prefs[UserPreferencesKeys.USER_NAME] = "Moi"
            prefs[UserPreferencesKeys.BIOMETRIC_ENABLED] = false
        }
    }
}

fun DataStore<Preferences>.getUserUuid(): Flow<String> =
    data.map { it[UserPreferencesKeys.USER_UUID] ?: "" }

fun DataStore<Preferences>.getUserName(): Flow<String> =
    data.map { it[UserPreferencesKeys.USER_NAME] ?: "Moi" }

fun DataStore<Preferences>.getBiometricEnabled(): Flow<Boolean> =
    data.map { it[UserPreferencesKeys.BIOMETRIC_ENABLED] ?: false }

suspend fun DataStore<Preferences>.setBiometricEnabled(enabled: Boolean) {
    edit { it[UserPreferencesKeys.BIOMETRIC_ENABLED] = enabled }
}

suspend fun DataStore<Preferences>.setUserName(name: String) {
    edit { it[UserPreferencesKeys.USER_NAME] = name }
}
