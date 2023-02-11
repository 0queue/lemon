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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.thomasharris.lemon.core.betterhtml.HtmlText
import dev.thomasharris.lemon.core.model.LobstersUser
import dev.thomasharris.lemon.core.theme.LemonForLobstersTheme
import dev.thomasharris.lemon.core.ui.format
import dev.thomasharris.lemon.core.ui.postedAgo
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun UserProfileRoute(
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onUsernameClicked: (username: String) -> Unit,
    onLinkClicked: (url: String) -> Unit,
) {
    val user by viewModel.user.collectAsState()

    UserProfileScreen(
        username = viewModel.username,
        user = user,
        onBackClicked = onBackClicked,
        onUsernameClicked = onUsernameClicked,
        onLinkClicked = onLinkClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    username: String,
    user: LobstersUser?,
    onBackClicked: () -> Unit,
    onUsernameClicked: (username: String) -> Unit,
    onLinkClicked: (url: String) -> Unit,
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
                        onUsernameClicked = onUsernameClicked,
                        onLinkClicked = onLinkClicked,
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
    onUsernameClicked: (username: String) -> Unit,
    onLinkClicked: (url: String) -> Unit,
    now: Instant = Clock.System.now(),
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

        ConstraintLayout(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val (joinedTitle, joinedContent) = createRefs()
            val (privilegesTitle, privilegesContent) = createRefs()
            val (karmaTitle, karmaContent) = createRefs()
            val (githubTitle, githubContent) = createRefs()
            val (twitterTitle, twitterContent) = createRefs()

            val hasPrivileges = true
            val hasGithub = true
            val hasTwitter = true

            Text(
                modifier = Modifier
                    .constrainAs(joinedTitle) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    },
                text = "Joined",
            )

            if (hasPrivileges) Text(
                modifier = Modifier
                    .constrainAs(privilegesTitle) {
                        top.linkTo(joinedContent.bottom)
                        start.linkTo(parent.start)
                    },
                text = "Privileges",
            )

            Text(
                modifier = Modifier
                    .constrainAs(karmaTitle) {
                        top.linkTo(if (hasPrivileges) privilegesContent.bottom else joinedContent.bottom)
                        start.linkTo(parent.start)
                    },
                text = "Karma",
            )

            if (hasGithub) Text(
                modifier = Modifier
                    .constrainAs(githubTitle) {
                        top.linkTo(karmaContent.bottom)
                        start.linkTo(parent.start)
                    },
                text = "Github",
            )

            if (hasTwitter) Text(
                modifier = Modifier
                    .constrainAs(twitterTitle) {
                        top.linkTo(if (hasGithub) githubContent.bottom else karmaContent.bottom)
                        start.linkTo(parent.start)
                    },
                text = "Twitter",
            )

            val barrier = createEndBarrier(
                joinedTitle,
                privilegesTitle,
                karmaTitle,
                githubTitle,
                twitterTitle,
            )

            val ago = user.createdAt.postedAgo(now).format(LocalContext.current.resources)

            val joined = when (val inviter = user.invitedByUser) {
                null -> ago
                else -> """<p>$ago invited by <a>$inviter</a></p>"""
            }

            Box(
                modifier = Modifier
                    .constrainAs(joinedContent) {
                        start.linkTo(barrier)
                        top.linkTo(joinedTitle.top)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                    }
                    .background(Color.Red),
            ) {
                HtmlText(
                    modifier = Modifier.fillMaxWidth(),
                    text = joined,
                    textAlign = TextAlign.End,
                    onLinkClicked = {
                        // doing normal link clicking things is annoying because it only
                        // parses absolute urls right now, and claw gets around it by
                        // building a spanned string itself
                        user
                            .invitedByUser
                            ?.let(onUsernameClicked)
                    },
                )
            }

            if (hasPrivileges) Text(
                modifier = Modifier
                    .constrainAs(privilegesContent) {
                        start.linkTo(barrier)
                        baseline.linkTo(privilegesTitle.baseline)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.preferredWrapContent
                    }
                    .background(Color.Green),
                text = "Uh idk moderator or something?",
                textAlign = TextAlign.End,
            )

            Text(
                modifier = Modifier
                    .constrainAs(karmaContent) {
                        start.linkTo(barrier)
                        baseline.linkTo(karmaTitle.baseline)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                    }
                    .background(Color.Blue),
                text = "1 billion karma",
                textAlign = TextAlign.End,
            )

            if (hasGithub) Text(
                modifier = Modifier
                    .constrainAs(githubContent) {
                        start.linkTo(barrier)
                        baseline.linkTo(githubTitle.baseline)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.preferredWrapContent
                    }
                    .background(Color.Cyan),
                text = "github link",
                textAlign = TextAlign.End,
            )

            if (hasTwitter) Text(
                modifier = Modifier
                    .constrainAs(twitterContent) {
                        start.linkTo(barrier)
                        baseline.linkTo(twitterTitle.baseline)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.preferredWrapContent
                    }
                    .background(Color.Yellow),
                text = "twitter link",
                textAlign = TextAlign.End,
            )
        }
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
        UserProfile(
            user = user,
            onUsernameClicked = {},
            onLinkClicked = {},
        )
    }
}
