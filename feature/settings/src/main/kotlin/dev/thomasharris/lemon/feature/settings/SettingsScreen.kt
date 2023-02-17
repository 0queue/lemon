package dev.thomasharris.lemon.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.thomasharris.lemon.core.datastore.Settings
import dev.thomasharris.lemon.core.datastore.ThemeBrightness
import dev.thomasharris.lemon.core.ui.SwipeToNavigate
import dev.thomasharris.lemon.core.ui.rememberSwipeToNavigateState

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
) {
    val settings by viewModel.settings.collectAsState()

    val swipeToNavigateState = rememberSwipeToNavigateState {
        onBackClicked()
    }

    SwipeToNavigate(
        state = swipeToNavigateState,
        content = {
            SettingsScreen(
                settings = settings,
                onBackClicked = onBackClicked,
                onBrightnessChanged = viewModel::onBrightnessSelected,
                onDynamicChanged = viewModel::onDynamicSelected,
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: Settings,
    onBackClicked: () -> Unit,
    onBrightnessChanged: (ThemeBrightness) -> Unit,
    onDynamicChanged: (Boolean) -> Unit,
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
                Text("theme brightness: ${settings.themeBrightness}")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(
                        onClick = { onBrightnessChanged(ThemeBrightness.SYSTEM) },
                        content = {
                            Text(
                                text = "System",
                                fontWeight = if (settings.themeBrightness == ThemeBrightness.SYSTEM) FontWeight.Bold else null,
                            )
                        },
                    )

                    Button(
                        onClick = { onBrightnessChanged(ThemeBrightness.DAY) },
                        content = {
                            Text(
                                text = "Day",
                                fontWeight = if (settings.themeBrightness == ThemeBrightness.DAY) FontWeight.Bold else null,
                            )
                        },
                    )

                    Button(
                        onClick = { onBrightnessChanged(ThemeBrightness.NIGHT) },
                        content = {
                            Text(
                                text = "Night",
                                fontWeight = if (settings.themeBrightness == ThemeBrightness.NIGHT) FontWeight.Bold else null,
                            )
                        },
                    )
                }

                Divider(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp,
                    ),
                    thickness = 1.dp,
                )

                Text("theme dynamic: ${settings.themeDynamic}")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(
                        onClick = { onDynamicChanged(true) },
                        content = {
                            Text(
                                text = "Dynamic",
                                fontWeight = if (settings.themeDynamic) FontWeight.Bold else null,
                            )
                        },
                    )

                    Button(
                        onClick = { onDynamicChanged(false) },
                        content = {
                            Text(
                                text = "Not Dynamic",
                                fontWeight = if (!settings.themeDynamic) FontWeight.Bold else null,
                            )
                        },
                    )
                }
            }
        },
    )
}
