package org.aurral.firetv.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** Process-wide DataStore for non-secret session settings. */
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "aurral_settings")
