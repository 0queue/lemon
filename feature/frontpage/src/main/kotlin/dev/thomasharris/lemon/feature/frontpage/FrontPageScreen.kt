package dev.thomasharris.lemon.feature.frontpage

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun FrontPageRoute(
    onClick: (String) -> Unit,
) {
    FrontPageScreen(
        onClick = onClick,
    )
}

@Composable
fun FrontPageScreen(
    onClick: (String) -> Unit,
) {
    Column {
        Text("welcome to the frontpage")
        Button(
            onClick = { onClick("firstStory") },
        ) {
            Text("First")
        }

        Button(
            onClick = { onClick("secondStory") },
        ) {
            Text("Second")
        }
    }
}
