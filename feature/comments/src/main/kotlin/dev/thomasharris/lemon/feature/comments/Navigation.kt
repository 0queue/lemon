package dev.thomasharris.lemon.feature.comments

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.installCommentsRoute(
    onClick: (String) -> Unit,
) {
    composable(
        route = "comments/{storyId}",
        arguments = listOf(
            navArgument("storyId") { type = NavType.StringType },
        ),
    ) {
        CommentsRoute(
            onClick = onClick,
        )
    }
}
