package dev.thomasharris.lemon.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.thomasharris.lemon.core.datastore.Settings
import dev.thomasharris.lemon.core.datastore.ThemeBrightness
import dev.thomasharris.lemon.core.theme.LemonForLobstersTheme
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
            SettingsColumn(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(horizontal = 8.dp),
                settings = settings,
                onBrightnessChanged = onBrightnessChanged,
                onDynamicChanged = onDynamicChanged,
            )
        },
    )
}

@Composable
fun SettingsColumn(
    settings: Settings,
    onBrightnessChanged: (ThemeBrightness) -> Unit,
    onDynamicChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth(),
        ),
    ) {
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = "Theme",
            style = MaterialTheme.typography.titleMedium,
        )

        // TODO segmented button when those are ready
        ThemeBrightness.values().forEach { themeBrightness ->
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .selectable(
                        selected = settings.themeBrightness == themeBrightness,
                        onClick = { onBrightnessChanged(themeBrightness) },
                        role = Role.RadioButton,
                    )
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = settings.themeBrightness == themeBrightness,
                    onClick = null,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = when (themeBrightness) {
                        ThemeBrightness.SYSTEM -> "System"
                        ThemeBrightness.DAY -> "Day"
                        ThemeBrightness.NIGHT -> "Night"
                    },
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Dynamic",
            )

            Spacer(modifier = Modifier.weight(1f))

            Switch(
                checked = settings.themeDynamic,
                onCheckedChange = onDynamicChanged,
            )
        }
    }
}

@Composable
@Preview
fun SettingsColumnPreview() {
    LemonForLobstersTheme {
        Surface {
            SettingsColumn(
                settings = Settings(
                    themeBrightness = ThemeBrightness.SYSTEM,
                    themeDynamic = true,
                ),
                onBrightnessChanged = {},
                onDynamicChanged = {},
            )
        }
    }
}
