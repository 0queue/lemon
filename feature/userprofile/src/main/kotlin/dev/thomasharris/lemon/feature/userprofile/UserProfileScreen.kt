package dev.thomasharris.lemon.feature.userprofile

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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp

@Composable
fun UserProfileRoute(
    onBackClicked: () -> Unit,
) {
    UserProfileScreen(
        username = "EXAMPLE USERNAME",
        onBackClicked = onBackClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    username: String,
    onBackClicked: () -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = Modifier
            .shadow(4.dp),
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
                    Text(text = "User")
                },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            ) {
                Text(text = username)
            }
        },
    )
}
