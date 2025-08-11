package com.thesubgraph.askstack.features.assistant.data.local.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import com.thesubgraph.askstack.BuildConfig

@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    fun getApiKey(): String {
        return BuildConfig.OPENAI_API_KEY.ifBlank {
            encryptedPrefs.getString(KEY_API_KEY, "") ?: ""
        }
    }
    
    fun hasApiKey(): Boolean {
        return getApiKey().isNotBlank()
    }
    
    fun saveApiKey(apiKey: String) {
        encryptedPrefs.edit {
            putString(KEY_API_KEY, apiKey)
        }
    }
    
    fun clearApiKey() {
        encryptedPrefs.edit {
            remove(KEY_API_KEY)
        }
    }
    
    fun saveDefaultAssistantId(assistantId: String) {
        encryptedPrefs.edit {
            putString(KEY_DEFAULT_ASSISTANT_ID, assistantId)
        }
    }
    
    fun getDefaultAssistantId(): String? {
        return BuildConfig.OPENAI_ASSISTANT_ID.ifBlank {
            encryptedPrefs.getString(KEY_DEFAULT_ASSISTANT_ID, null)
        }
    }
    
    fun hasAssistantIdFromBuildConfig(): Boolean {
        return BuildConfig.OPENAI_ASSISTANT_ID.isNotBlank()
    }
    
    companion object {
        private const val PREFS_NAME = "openai_secure_prefs"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_DEFAULT_ASSISTANT_ID = "default_assistant_id"
    }
}
