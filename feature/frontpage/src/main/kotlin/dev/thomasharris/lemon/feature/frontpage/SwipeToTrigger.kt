package dev.thomasharris.lemon.feature.frontpage

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Float.min
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class SwipeToTriggerState(
    private val scope: CoroutineScope,
    private val onTriggered: () -> Unit,
    private val threshold: Float,
    private val visualOffsetMultiplier: Float,
) {
    private val offsetX = Animatable(0f)

    val visualOffset: Float
        get() = offsetX.value.div(visualOffsetMultiplier)

    // TODO I wonder if this can be some fancy derived state of visualOffset, width, and moving left
    var isAboveThreshold by mutableStateOf(false)
        private set

    private var movingLeft = false

    internal var composableWidth: Int? = null

    fun drag(dragAmount: Float) {
        movingLeft = dragAmount < 0

        val w = composableWidth

        scope.launch {
            offsetX.snapTo(min(offsetX.value + dragAmount, 0f))

            isAboveThreshold =
                w != null && visualOffset.absoluteValue > w.times(threshold) && movingLeft
        }
    }

    fun end() {
        if (isAboveThreshold) {
            onTriggered()
        }

        scope.launch {
            movingLeft = false
            offsetX.animateTo(0f)
            isAboveThreshold = false
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun rememberSwipeToTriggerState(
    threshold: Float = .3f,
    visualOffsetMultiplier: Float = 1.5f,
    onTriggered: () -> Unit,
): SwipeToTriggerState {
    val scope = rememberCoroutineScope()

    return remember {
        SwipeToTriggerState(
            scope = scope,
            onTriggered = onTriggered,
            threshold = threshold,
            visualOffsetMultiplier = visualOffsetMultiplier,
        )
    }
}

/**
 * Like SwipeToDismiss without dismissing
 */
@Composable
fun SwipeToTrigger(
    state: SwipeToTriggerState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    background: @Composable RowScope.() -> Unit,
    foreground: @Composable RowScope.() -> Unit,
) {
    Box(
        modifier = modifier
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
            },
    ) {
        Row(
            content = background,
            modifier = Modifier.matchParentSize(),
        )

        Row(
            content = foreground,
            modifier = Modifier.offset { IntOffset(state.visualOffset.roundToInt(), 0) },
        )
    }
}
