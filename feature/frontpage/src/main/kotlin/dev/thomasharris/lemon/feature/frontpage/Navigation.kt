package dev.thomasharris.lemon.feature.frontpage

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.installFrontPageRoute(
    onClick: (String) -> Unit,
) {
    composable("frontpage") {
        FrontPageRoute(
            onClick = onClick,
        )
    }
}
