package dev.thomasharris.lemon.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.thomasharris.lemon.core.ui.SwipeToNavigate
import dev.thomasharris.lemon.core.ui.rememberSwipeToNavigateState

@Composable
fun SettingsRoute(
    onBackClicked: () -> Unit,
) {
    val swipeToNavigateState = rememberSwipeToNavigateState {
        onBackClicked()
    }

    SwipeToNavigate(
        state = swipeToNavigateState,
        content = {
            SettingsScreen(
                onBackClicked = onBackClicked,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null, // TODO
                        )
                    }
                },
                title = {
                    Text(text = "Settings")
                },
            )
        },
        content = { contentPadding ->
            Column(
                modifier = Modifier.padding(contentPadding),
            ) {
                Text("Settings")
            }
        },
    )
}
