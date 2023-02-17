package dev.thomasharris.lemon.feature.settings

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomasharris.lemon.core.datastore.DefaultSettings
import dev.thomasharris.lemon.core.datastore.Settings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    datastore: DataStore<Settings>,
) : ViewModel() {
    val settings = datastore
        .data
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DefaultSettings,
        )
}
