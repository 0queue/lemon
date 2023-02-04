package dev.thomasharris.lemon.feature.frontpage

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

internal const val ROUTE = "/"

fun NavController.navigateToFrontPage() {
    navigate(ROUTE)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.installFrontPageRoute(
    onClick: (String) -> Unit,
    onUrlSwiped: (String?) -> Unit,
) {
    composable(
        route = ROUTE,
        popEnterTransition = {
            fadeIn()
        },
    ) {
        FrontPageRoute(
            onClick = onClick,
            onUrlSwiped = onUrlSwiped,
        )
    }
}
