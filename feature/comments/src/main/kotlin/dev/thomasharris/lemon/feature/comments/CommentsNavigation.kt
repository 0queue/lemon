package dev.thomasharris.lemon.feature.comments

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

internal const val storyIdArg = "storyId"

internal class CommentsArgs(val storyId: String) {
    companion object {
        fun fromSavedState(
            savedStateHandle: SavedStateHandle,
        ) = CommentsArgs(savedStateHandle[storyIdArg]!!)
    }
}

fun NavController.navigateToComments(storyId: String) {
    navigate("s/$storyId")
}

fun NavGraphBuilder.installCommentsRoute(
    onBackClick: () -> Unit,
) {
    composable(
        route = "s/{$storyIdArg}",
        arguments = listOf(
            navArgument(storyIdArg) { type = NavType.StringType },
        ),
    ) {
        CommentsRoute(
            onBackClick = onBackClick,
        )
    }
}
