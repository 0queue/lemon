package dev.thomasharris.lemon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.thomasharris.lemon.core.datastore.ThemeBrightness
import dev.thomasharris.lemon.core.theme.LemonForLobstersTheme
import dev.thomasharris.lemon.feature.comments.R
import dev.thomasharris.lemon.feature.comments.installCommentsRoute
import dev.thomasharris.lemon.feature.comments.navigateToComments
import dev.thomasharris.lemon.feature.frontpage.installFrontPageRoute
import dev.thomasharris.lemon.feature.settings.installSettingsRoute
import dev.thomasharris.lemon.feature.settings.navigateToSettings
import dev.thomasharris.lemon.feature.userprofile.installUserProfileRoute
import dev.thomasharris.lemon.feature.userprofile.navigateToUserProfile

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val settings by viewModel.settings.collectAsState()

            LemonForLobstersTheme(
                useDarkTheme = when (settings.themeBrightness) {
                    ThemeBrightness.SYSTEM -> isSystemInDarkTheme()
                    ThemeBrightness.DAY -> false
                    ThemeBrightness.NIGHT -> true
                },
                dynamicColor = settings.themeDynamic,
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberAnimatedNavController()

                    val context = LocalContext.current
                    val closeButtonIcon = remember(context) {
                        // TODO technically now have two sources of truth on that back icon...
                        context.getDrawable(R.drawable.baseline_arrow_back_24)!!.toBitmap()
                    }
                    val colorSurface = MaterialTheme.colorScheme.surface

                    val openUrl = { url: String? ->
                        if (url != null) context.launchUrl(
                            url = url,
                            closeButtonIcon = closeButtonIcon,
                            toolbarColor = colorSurface.toArgb(),
                        )
                    }

                    AnimatedNavHost(
                        navController = navController,
                        startDestination = "/",
                        popEnterTransition = { fadeIn() },
                        popExitTransition = { ExitTransition.None },
                    ) {
                        installFrontPageRoute(
                            onClick = navController::navigateToComments,
                            onLongClick = navController::navigateToUserProfile,
                            onUrlSwiped = openUrl,
                            onSettingsClicked = navController::navigateToSettings,
                        )

                        installCommentsRoute(
                            onBackClick = navController::popBackStack,
                            onUrlClicked = openUrl,
                            onViewUserProfile = navController::navigateToUserProfile,
                        )

                        installUserProfileRoute(
                            onBackClicked = navController::popBackStack,
                            onUsernameClicked = navController::navigateToUserProfile,
                            onLinkClicked = openUrl,
                        )

                        installSettingsRoute(
                            onBackClicked = navController::popBackStack,
                        )
                    }
                }
            }
        }
    }
}

// TODO Uri.parse is very throwy, handle it by showing
//      an error toast or snackbar
fun Context.launchUrl(
    url: String,
    closeButtonIcon: Bitmap,
    @ColorInt
    toolbarColor: Int,
) {
    val defaultColors = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor)
        .build()

    CustomTabsIntent.Builder()
        .setStartAnimations(this, R.anim.slide_in_from_right, R.anim.nothing)
        // Not currently working...
        .setExitAnimations(this, R.anim.nothing, R.anim.slide_out_to_right)
        .setCloseButtonIcon(closeButtonIcon)
        .setDefaultColorSchemeParams(defaultColors)
        .build()
        .launchUrl(this, Uri.parse(url))
}

fun Drawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(
        intrinsicWidth,
        intrinsicHeight,
        Bitmap.Config.ARGB_8888,
    )

    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LemonForLobstersTheme {
        Greeting("Android")
    }
}
