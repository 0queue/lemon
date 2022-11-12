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
    onBackClick: () -> Unit,
) {
    CommentsScreen(
        storyId = viewModel.id,
        onBackClick = onBackClick,
    )
}

@Composable
fun CommentsScreen(
    storyId: String,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("story: $storyId")
        Button(
            onClick = onBackClick,
        ) {
            Text("go back")
        }
    }
}
