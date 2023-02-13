package dev.thomasharris.lemon.core.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.roundToInt

// I guess this basically swipe to dismiss,
// but upon triggering the swipe action
// I want navigation to finish the animation
// for consistency. Also it is fun

class SwipeToNavigateState(
    private val scope: CoroutineScope,
    private val threshold: Float,
    private val visualOffsetMultiplier: Float,
    private val onNavigate: () -> Unit,
) {
    private val offsetX = Animatable(0f)

    val visualOffset: Float
        get() = offsetX.value.div(visualOffsetMultiplier)

    private var movingRight by mutableStateOf(false)

    internal var composableWidth: Int? by mutableStateOf(null)

    val isAboveThreshold: Boolean by derivedStateOf {
        val w = composableWidth

        w != null && visualOffset.absoluteValue > w.times(threshold) && movingRight
    }

    fun drag(dragAmount: Float) {
        movingRight = dragAmount > 0

        scope.launch {
            offsetX.snapTo(max(0f, offsetX.value + dragAmount))
        }
    }

    fun end() {
        if (isAboveThreshold) {
            onNavigate()
        } else scope.launch {
            movingRight = false
            offsetX.animateTo(0f)
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun rememberSwipeToNavigateState(
    threshold: Float = .3f,
    visualOffsetMultiplier: Float = 1.5f,
    onNavigate: () -> Unit,
): SwipeToNavigateState {
    val scope = rememberCoroutineScope()

    return remember {
        SwipeToNavigateState(
            scope = scope,
            threshold = threshold,
            visualOffsetMultiplier = visualOffsetMultiplier,
            onNavigate = onNavigate,
        )
    }
}

@Composable
fun SwipeToNavigate(
    state: SwipeToNavigateState,
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.then(
            Modifier
                .onSizeChanged {
                    state.composableWidth = it.width
                }
                .pointerInput(enabled) {
                    if (!enabled)
                        return@pointerInput

                    detectHorizontalDragGestures(
                        onDragEnd = {
                            state.end()
                        },
                        onDragCancel = {
                            state.end()
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            state.drag(dragAmount)
                        },
                    )
                }
                .offset {
                    IntOffset(state.visualOffset.roundToInt(), 0)
                }
                .shadow(elevation),
        ),
        content = content,
    )
}
