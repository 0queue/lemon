package dev.thomasharris.lemon.feature.userprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.thomasharris.lemon.core.model.LobstersUser
import dev.thomasharris.lemon.core.theme.LemonForLobstersTheme
import kotlinx.datetime.Instant

@Composable
fun UserProfileRoute(
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
) {
    val user by viewModel.user.collectAsState()

    UserProfileScreen(
        username = viewModel.username,
        user = user,
        onBackClicked = onBackClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    username: String,
    user: LobstersUser?,
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
            Box(
                modifier = Modifier.padding(contentPadding),
            ) {
                if (user == null)
                    NoProfileFound(username = username)
                else
                    UserProfile(
                        modifier = Modifier
                            .fillMaxWidth()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        user = user,
                    )
            }
        },
    )
}

@Composable
fun NoProfileFound(username: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("User '$username' not found")
    }
}

@Composable
fun UserProfile(
    user: LobstersUser,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                )
                .background(MaterialTheme.colorScheme.background)
                .align(Alignment.CenterHorizontally),

        ) {
            AsyncImage(
                modifier = Modifier
                    .size(128.dp)
                    .padding(2.dp)
                    .clip(CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.fullAvatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp),
            text = user.username,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Preview
@Composable
fun UserProfilePreview() {
    val instant = Instant.parse("2022-11-20T12:26:06.406253Z")

    val user = LobstersUser(
        username = "0queue",
        createdAt = instant,
        isAdmin = false,
        about = "I do things on Android and other Linux systems",
        isModerator = false,
        karma = 1_000_000,
        avatarUrl = "/avatars/jcs-200.png",
        invitedByUser = null,
        githubUsername = "0queue",
        twitterUsername = null,
    )

    LemonForLobstersTheme {
        UserProfile(user = user)
    }
}
