package dev.thomasharris.lemon.feature.comments

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable

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

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.installCommentsRoute(
    onBackClick: () -> Unit,
) {
    composable(
        route = "s/{$storyIdArg}",
        arguments = listOf(
            navArgument(storyIdArg) { type = NavType.StringType },
        ),
        enterTransition = {
            slideInHorizontally(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = IntOffset.VisibilityThreshold,
                ),
                initialOffsetX = { it },
            )
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { it })
        },
    ) {
        CommentsRoute(
            onBackClick = onBackClick,
        )
    }
}
