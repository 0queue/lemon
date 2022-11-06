package dev.thomasharris.lemon.feature.frontpage

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

internal const val ROUTE = "/"

fun NavController.navigateToFrontPage() {
    navigate(ROUTE)
}

fun NavGraphBuilder.installFrontPageRoute(
    onClick: (String) -> Unit,
) {
    composable(ROUTE) {
        FrontPageRoute(
            onClick = onClick,
        )
    }
}
