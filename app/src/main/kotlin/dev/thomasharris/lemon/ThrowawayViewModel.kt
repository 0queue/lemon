package dev.thomasharris.lemon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.unwrap
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import dev.thomasharris.lemon.lobstersapi.LobstersService
import dev.thomasharris.lemon.model.LobstersStory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
object ThrowawayModule {
    @Provides
    fun provideLobstersService() = LobstersService()
}

@HiltViewModel
class ThrowawayViewModel @Inject constructor(
    private val lobstersService: LobstersService,
) : ViewModel() {

    val counterState = MutableStateFlow(0)

    suspend fun getPage(): List<LobstersStory> {
        return lobstersService.getPage(1).unwrap()
    }

    fun increment() {
        viewModelScope.launch {
            delay(1000)
            counterState.value += 1
        }
    }
}
