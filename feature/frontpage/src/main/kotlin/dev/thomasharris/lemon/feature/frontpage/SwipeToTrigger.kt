package dev.thomasharris.lemon.feature.frontpage

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
) {
    internal val offsetX = Animatable(0f)

    internal var movingLeft = false

    internal var composableWidth: Int? = null

    fun drag(dragAmount: Float) {
        movingLeft = dragAmount < 0

        scope.launch {
            offsetX.snapTo(min(offsetX.value + dragAmount, 0f))
        }
    }

    fun end() {
        val w = composableWidth

        if (w != null && offsetX.value.div(1.5f).absoluteValue > w * .3f && movingLeft) {
            onTriggered()
        }

        scope.launch {
            movingLeft = false
            offsetX.animateTo(0f)
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun rememberSwipeToTriggerState(
    onTriggered: () -> Unit,
): SwipeToTriggerState {
    val scope = rememberCoroutineScope()

    return remember {
        SwipeToTriggerState(
            scope = scope,
            onTriggered = onTriggered,
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
                        // TODO try not consuming if the drag amount is positive and the offsetX >= 0
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

        val scaledOffsetX = state.offsetX.value.div(1.5).roundToInt()

        Row(
            content = foreground,
            modifier = Modifier.offset { IntOffset(scaledOffsetX, 0) },
        )
    }
}
