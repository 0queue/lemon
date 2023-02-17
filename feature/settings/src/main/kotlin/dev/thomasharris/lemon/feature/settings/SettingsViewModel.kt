package dev.thomasharris.lemon.feature.settings

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomasharris.lemon.core.datastore.DefaultSettings
import dev.thomasharris.lemon.core.datastore.Settings
import dev.thomasharris.lemon.core.datastore.ThemeBrightness
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val datastore: DataStore<Settings>,
) : ViewModel() {
    val settings = datastore
        .data
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DefaultSettings,
        )

    fun onBrightnessSelected(newBrightness: ThemeBrightness) {
        viewModelScope.launch {
            datastore.updateData { settings ->
                settings.copy(themeBrightness = newBrightness)
            }
        }
    }

    fun onDynamicSelected(newDynamic: Boolean) {
        viewModelScope.launch {
            datastore.updateData { settings ->
                settings.copy(themeDynamic = newDynamic)
            }
        }
    }
}
