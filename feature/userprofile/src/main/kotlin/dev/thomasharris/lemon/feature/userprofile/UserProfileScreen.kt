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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Composable
fun UserProfileRoute(
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onUsernameClicked: (username: String) -> Unit,
    onLinkClicked: (url: String) -> Unit,
) {
    val uiState by viewModel.user.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.errorChannel) {
        viewModel
            .errorChannel
            .receiveAsFlow()
            .collect {
                snackbarHostState.showSnackbar("Failed to refresh user")
            }
    }

    UserProfileScreen(
        username = viewModel.username,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClicked = onBackClicked,
        onUsernameClicked = onUsernameClicked,
        onLinkClicked = onLinkClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    username: String,
    uiState: UserProfileUiState?,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClicked: () -> Unit,
    onUsernameClicked: (username: String) -> Unit,
    onLinkClicked: (url: String) -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = Modifier
            .shadow(4.dp),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                // TODO not great, should be either animated, wait for a load/failed load (as state!)
                //      or similar.. Maybe watching the db is not appropriate here
                if (uiState == null)
                    NoProfileFound(username = username)
                else
                    UserProfile(
                        modifier = Modifier
                            .fillMaxWidth()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        user = uiState.user,
                        renderedAbout = uiState.renderedAbout,
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
    renderedAbout: String,
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

        UserInfoTable(
            modifier = Modifier.fillMaxWidth(),
            user = user,
            now = now,
            onUsernameClicked = onUsernameClicked,
            onLinkClicked = onLinkClicked,
        )

        if (user.about.isBlank()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                text = "A mystery...",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
            )
        } else {
            HtmlText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                text = renderedAbout,
                onLinkClicked = {
                    if (it != null)
                        onLinkClicked(it)
                },
            )
        }
    }
}

@Composable
fun UserInfoTable(
    user: LobstersUser,
    now: Instant,
    onUsernameClicked: (username: String) -> Unit,
    onLinkClicked: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ConstraintLayout(
        modifier = modifier,
    ) {
        val (joinedTitle, joinedContent) = createRefs()
        val (privilegesTitle, privilegesContent) = createRefs()
        val (karmaTitle, karmaContent) = createRefs()
        val (githubTitle, githubContent) = createRefs()
        val (twitterTitle, twitterContent) = createRefs()

        Text(
            modifier = Modifier
                .constrainAs(joinedTitle) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
            text = "Joined",
        )

        if (user.isPrivileged) Text(
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
                    top.linkTo(if (user.isPrivileged) privilegesContent.bottom else joinedContent.bottom)
                    start.linkTo(parent.start)
                },
            text = "Karma",
        )

        if (user.githubUsername != null) Text(
            modifier = Modifier
                .constrainAs(githubTitle) {
                    top.linkTo(karmaContent.bottom)
                    start.linkTo(parent.start)
                },
            text = "Github",
        )

        if (user.twitterUsername != null) Text(
            modifier = Modifier
                .constrainAs(twitterTitle) {
                    top.linkTo(if (user.githubUsername != null) githubContent.bottom else karmaContent.bottom)
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
                    // TODO baseline alignment from inner android view???
                    top.linkTo(joinedTitle.top)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
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

        "admin".takeIf { user.isAdmin }

        if (user.isPrivileged) {
            // TODO once the visual logic for tags is extracted, if ever,
            //      convert these to look the same
            val privileges = listOfNotNull(
                "admin".takeIf { user.isAdmin },
                "moderator".takeIf { user.isModerator },
            ).joinToString(separator = ", ")

            Text(
                modifier = Modifier
                    .constrainAs(privilegesContent) {
                        start.linkTo(barrier)
                        baseline.linkTo(privilegesTitle.baseline)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.preferredWrapContent
                    },
                text = privileges,
                textAlign = TextAlign.End,
            )
        }

        Text(
            modifier = Modifier
                .constrainAs(karmaContent) {
                    start.linkTo(barrier)
                    baseline.linkTo(karmaTitle.baseline)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
            text = user.karma.toString(10),
            textAlign = TextAlign.End,
        )

        user.githubUsername?.let { githubUsername ->
            Box(
                modifier = Modifier
                    .constrainAs(githubContent) {
                        start.linkTo(barrier)
                        top.linkTo(githubTitle.top)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.preferredWrapContent
                    },
            ) {
                val text =
                    """<p><a href="https://github.com/$githubUsername">https://github.com/$githubUsername</a></p>"""
                HtmlText(
                    modifier = Modifier.fillMaxWidth(),
                    text = text,
                    textAlign = TextAlign.End,
                    onLinkClicked = {
                        if (it != null)
                            onLinkClicked(it)
                    },
                )
            }
        }

        user.twitterUsername?.let { twitterUsername ->
            Box(
                modifier = Modifier
                    .constrainAs(twitterContent) {
                        start.linkTo(barrier)
                        top.linkTo(twitterTitle.top)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.preferredWrapContent
                    },
            ) {
                val text =
                    """<p><a href="https://twitter.com/$twitterUsername">@$twitterUsername</a></p> """
                HtmlText(
                    modifier = Modifier.fillMaxWidth(),
                    text = text,
                    textAlign = TextAlign.End,
                    onLinkClicked = {
                        if (it != null)
                            onLinkClicked(it)
                    },
                )
            }
        }
    }
}

private val LobstersUser.isPrivileged: Boolean
    get() = isAdmin || isModerator

@Preview
@Composable
fun UserProfilePreview() {
    val instant = Instant.parse("2022-11-20T12:26:06.406253Z")

    val user = LobstersUser(
        username = "0queue",
        createdAt = instant,
        isAdmin = true,
        about = "I do things on Android and other Linux systems",
        isModerator = true,
        karma = 1_000_000,
        avatarUrl = "/avatars/jcs-200.png",
        invitedByUser = null,
        githubUsername = "0queue",
        twitterUsername = "username",
    )

    LemonForLobstersTheme {
        UserProfile(
            user = user,
            renderedAbout = """<p>${user.about}</p>""",
            onUsernameClicked = {},
            onLinkClicked = {},
        )
    }
}
