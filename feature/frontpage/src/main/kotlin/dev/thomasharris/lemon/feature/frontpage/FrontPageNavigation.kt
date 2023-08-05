package dev.thomasharris.lemon.feature.frontpage

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

internal const val ROUTE = "/"

fun NavController.navigateToFrontPage() {
    navigate(ROUTE)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.installFrontPageRoute(
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    onUrlSwiped: (String?) -> Unit,
    onSettingsClicked: () -> Unit,
) {
    composable(
        route = ROUTE,
        popEnterTransition = {
            fadeIn()
        },
    ) {
        FrontPageRoute(
            onClick = onClick,
            onLongClick = onLongClick,
            onUrlSwiped = onUrlSwiped,
            onSettingsClicked = onSettingsClicked,
        )
    }
}
