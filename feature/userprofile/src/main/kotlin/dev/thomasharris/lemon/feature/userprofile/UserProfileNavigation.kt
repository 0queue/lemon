package dev.thomasharris.lemon.feature.userprofile

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
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

internal const val usernameArg = "usernameArg"

internal class UserProfileArgs(
    val username: String,
) {
    companion object {
        fun fromSavedState(
            savedStateHandle: SavedStateHandle,
        ) = UserProfileArgs(savedStateHandle[usernameArg]!!)
    }
}

fun NavController.navigateToUserProfile(username: String) {
    navigate("u/$username")
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.installUserProfileRoute(
    onBackClicked: () -> Unit,
    onUsernameClicked: (username: String) -> Unit,
    onLinkClicked: (url: String) -> Unit,
) {
    composable(
        route = "u/{$usernameArg}",
        arguments = listOf(
            navArgument(usernameArg) { type = NavType.StringType },
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
        exitTransition = null,
        popEnterTransition = null,
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it })
        },
        content = {
            UserProfileRoute(
                onBackClicked = onBackClicked,
                onUsernameClicked = onUsernameClicked,
                onLinkClicked = onLinkClicked,
            )
        },
    )
}
