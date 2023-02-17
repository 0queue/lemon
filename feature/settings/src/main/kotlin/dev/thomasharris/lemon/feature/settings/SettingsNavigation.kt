package dev.thomasharris.lemon.feature.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

fun NavController.navigateToSettings() {
    navigate("settings")
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.installSettingsRoute(
    onBackClicked: () -> Unit,
) {
    composable(
        route = "settings",
        // TODO I think most of these transitions can be default
        enterTransition = {
            slideInHorizontally(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = IntOffset.VisibilityThreshold,
                ),
                initialOffsetX = { it },
            )
        },
        exitTransition = null,
        popEnterTransition = null,
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it })
        },
        content = {
            SettingsRoute(
                onBackClicked = onBackClicked,
            )
        },
    )
}
