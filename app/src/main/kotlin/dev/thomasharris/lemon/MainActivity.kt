package dev.thomasharris.lemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.thomasharris.lemon.feature.comments.installCommentsRoute
import dev.thomasharris.lemon.feature.frontpage.installFrontPageRoute
import dev.thomasharris.lemon.lobstersapi.LobstersService
import dev.thomasharris.lemon.ui.theme.LemonForLobstersTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lobstersService = LobstersService()

        setContent {
            LemonForLobstersTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "frontpage",
                    ) {
                        installFrontPageRoute(
                            onClick = {
                                navController.navigate("comments/$it")
                            },
                        )

                        installCommentsRoute(
                            onClick = {
                                navController.navigate("comments/$it")
                            },
                        )
                    }

//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Top,
//                    ) {
//                        val throwawayViewModel = viewModel<ThrowawayViewModel>()
//
//                        val counterState by throwawayViewModel.counterState.collectAsState()
//
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(8.dp),
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.SpaceEvenly,
//                        ) {
//                            Text("Counter: $counterState")
//
//                            Button(onClick = throwawayViewModel::increment) {
//                                Text("Increment")
//                            }
//
// //                            Button(
// //                                onClick = throwawayViewModel::insertStories,
// //                            ) {
// //                                Text("Load")
// //                            }
//                        }
//
//                        val listState = rememberLazyListState()
//
//                        val stories = throwawayViewModel.pages.collectAsLazyPagingItems()
//
//                        LazyColumn(
//                            modifier = Modifier.fillMaxWidth(),
//                            state = listState,
//                        ) {
//                            items(
//                                items = stories,
//                                key = Story::shortId,
//                            ) { story ->
//
//                                if (story == null) {
//                                    Text(
//                                        modifier = Modifier.padding(8.dp),
//                                        text = "why is this null",
//                                    )
//                                } else {
//                                    Column(
//                                        modifier = Modifier.padding(8.dp),
//                                    ) {
//                                        Text(text = story.title)
//                                        Text(text = story.createdAt.toString())
//                                    }
//                                }
//                            }
//                        }
//                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LemonForLobstersTheme {
        Greeting("Android")
    }
}
