package dev.thomasharris.lemon

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
class MainActivityViewModel @Inject constructor(
    settingsDatastore: DataStore<Settings>,
) : ViewModel() {

    val settings = settingsDatastore
        .data
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DefaultSettings,
        )
}
