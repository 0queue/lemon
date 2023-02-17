package dev.thomasharris.lemon.feature.userprofile

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.core.DataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.michaelbull.result.onFailure
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import dev.thomasharris.lemon.core.data.UserRepository
import dev.thomasharris.lemon.core.datastore.Settings
import dev.thomasharris.lemon.core.model.LobstersUser
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import materialcolorutilities.quantize.QuantizerCelebi
import materialcolorutilities.scheme.Scheme
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
    private val imageLoader: ImageLoader,
    private val settingsDataStore: DataStore<Settings>,
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

                UiState(
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

    // Not super happy with this solution either tbh
    fun scheme(context: Context): Flow<ThemeInfo?> {
        // why does loading the image need another context I already gave the ImageLoader one ;__;
        return user.combine(settingsDataStore.data) { uiState, settings ->

            if (uiState == null)
                return@combine null

            val res = ImageRequest.Builder(context)
                .data(uiState.user.fullAvatarUrl)
                .crossfade(true)
                .allowHardware(false)
                .build()
                .let { imageLoader.execute(it) }

            if (res !is SuccessResult)
                return@combine null

            val bitmap = res.drawable.toBitmap()
            val pixels = IntArray(bitmap.width * bitmap.height)

            bitmap.getPixels(
                pixels,
                0,
                bitmap.width,
                0,
                0,
                bitmap.width,
                bitmap.height,
            )

            // TODO idk probably should quantize and score rather than maxColors=1
            val colors = QuantizerCelebi.quantize(pixels, 1)

            val argb = colors.entries.first().key

            ThemeInfo(
                keyColor = Color(argb),
                lightScheme = Scheme.light(argb),
                darkScheme = Scheme.dark(argb),
                settings = settings,
            )
        }
    }

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

    data class UiState(
        val user: LobstersUser,
        val renderedAbout: String,
    )

    data class ThemeInfo(
        val keyColor: Color,
        val lightScheme: Scheme,
        val darkScheme: Scheme,
        val settings: Settings,
    )
}

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
