package dev.thomasharris.lemon.feature.userprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import dev.thomasharris.lemon.core.data.UserRepository
import dev.thomasharris.lemon.core.model.LobstersUser
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    markdownParser: Parser,
    htmlRenderer: HtmlRenderer,
) : ViewModel() {
    private val args = UserProfileArgs.fromSavedState(savedStateHandle)

    val username = args.username

    val user = userRepository
        .userFlow(args.username)
        .map { user ->
            if (user != null) {
                val renderedAbout = user.about
                    .let(markdownParser::parse)
                    .let(htmlRenderer::render)

                UserProfileUiState(
                    user = user,
                    renderedAbout = renderedAbout,
                )
            } else null
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    val errorChannel = Channel<Throwable>()

    init {
        refreshUser()
    }

    private fun refreshUser() {
        viewModelScope.launch {
            userRepository
                .refreshUser(username)
                .onFailure { t ->
                    errorChannel.send(t)
                }
        }
    }
}

data class UserProfileUiState(
    val user: LobstersUser,
    val renderedAbout: String,
)

@Module
@InstallIn(SingletonComponent::class)
object MarkdownModule {
    @Provides
    @Singleton
    fun provideMarkdownParser(): Parser = Parser.builder().build()

    @Provides
    @Singleton
    fun provideHtmlRenderer(): HtmlRenderer = HtmlRenderer.builder().build()
}
