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
import dev.thomasharris.lemon.core.theme.LemonForLobstersTheme
import dev.thomasharris.lemon.feature.comments.installCommentsRoute
import dev.thomasharris.lemon.feature.comments.navigateToComments
import dev.thomasharris.lemon.feature.frontpage.installFrontPageRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                        startDestination = "/",
                    ) {
                        installFrontPageRoute(
                            onClick = navController::navigateToComments,
                        )

                        installCommentsRoute(
                            onBackClick = navController::popBackStack,
                        )
                    }
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
