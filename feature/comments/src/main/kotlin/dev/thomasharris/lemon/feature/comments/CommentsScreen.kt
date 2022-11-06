package dev.thomasharris.lemon.feature.comments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CommentsRoute(
    viewModel: CommentsViewModel = hiltViewModel(),
    onClick: (String) -> Unit,
) {
    CommentsScreen(
        storyId = viewModel.id,
        onClick = onClick,
    )
}

@Composable
fun CommentsScreen(
    storyId: String,
    onClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("story: $storyId")
        Button(
            onClick = {
                onClick(storyId + "Nested")
            },
        ) {
            Text("go deeper")
        }
    }
}
