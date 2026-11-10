package org.aurral.firetv.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Stores the account/app password in an encrypted preferences file. */
@Singleton
class SecurePasswordStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    fun save(password: String) {
        prefs.edit().putString(KEY_PASSWORD, password).apply()
    }

    fun load(): String? = prefs.getString(KEY_PASSWORD, null)

    fun clear() {
        prefs.edit().remove(KEY_PASSWORD).apply()
    }

    private companion object {
        const val FILE_NAME = "aurral_secure"
        const val KEY_PASSWORD = "password"
    }
}
