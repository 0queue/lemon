package dev.thomasharris.lemon.feature.frontpage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import dev.thomasharris.lemon.core.betterhtml.HtmlText
import dev.thomasharris.lemon.core.model.LobstersStory
import dev.thomasharris.lemon.core.ui.Story
import dev.thomasharris.lemon.core.ui.requireNotPlaceholder

@Composable
fun FrontPageRoute(
    viewModel: FrontPageViewModel = hiltViewModel(),
    onClick: (String) -> Unit,
) {
    val pages = viewModel.pages.collectAsLazyPagingItems()

    FrontPageScreen(
        onClick = onClick,
        pages = pages,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
fun FrontPageScreen(
    onClick: (String) -> Unit,
    pages: LazyPagingItems<LobstersStory>,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Lemon for Lobsters")
                },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    HtmlText(
                        text = """
                            <blockquote>
                            <p>Maybe this is a hot take, but I suspect that unless we start using radically different physical hardware, UNIX is going to stay a pretty good API and I’m fine with it looking pretty similar in the year 2100.</p>
                            <blockquote>
                            <p>This is a nested blockquote which is double indented</p>
                            </blockquote
                            </blockquote>
                            <p>The hardware has changed quite a bit from the systems where UNIX was developed:</p>
                            <ul>
                                <li>Multicore is the norm, increasingly with asymmetric multiprocessing (big.LITTLE and so on).</li>
                                <li>There are multiple heterogeneous compute units (GPUs, NPUs, and so on).</li>
                                <li>There is almost always at least one fast network.</li>
                                <li>Local storage latency is very low and seek times are also very low.</li>
                                <li>Remote storage capacity is effectively unbounded.</li>
                            </ul>
                            <p>Some changes were present in the ’90s:</p>
                            <ul>
                                <li>There’s usually a display capable of graphics.</li>
                                <li>There’s usually a pointing device.</li>
                                <li><code>RAM</code> is a lot slower than the <code>CPU</code>(s), you can do a lot of compute per memory access.</li>
                            </ul>
                            <p>At the same time, user needs have changed a lot:</p>
                            <ul>
                                <li>Most computers have a single user.</li>
                                <li>Most users have multiple computers and need to synchronise data between them.</li>
                                <li>Software comes from untrusted sources.</li>
                                <li>Security models need to protect the user’s data from malicious or compromised applications, not from other users.</li>
                                <li>Users perform a very large number of different tasks on a computer.</li>
                            </ul>
                            <p>I think UNIX can adapt to the changes to the hardware.  I’m far less confident that it will adapt well to the changes to uses.  In particular, the UNIX security model is a very poor fit for modern computers (though things like Capsicum can paper over some of this).  Fuchsia provides an more Mach-like abstraction without a single global namespace (as did Plan 9), which makes it easier to run applications in isolated environments.</p>
                        """.trimIndent().trim(),
                    )
                }
//                    var onDraw: DrawScope.() -> Unit by remember { mutableStateOf({}) }
//                    val textMeasurer = rememberTextMeasurer()
//                    val style = LocalTextStyle.current
//
//                    // TODO can calculate for list and take max?
//
//                    val numberedIndentWidth = with(LocalDensity.current) {
//                        textMeasurer.measure(
//                            text = AnnotatedString(0.toString().plus(".")),
//                            style = style,
//                        ).size.width.toFloat()
//                    }
//
//                    val numberedIndentWidthSp = with(LocalDensity.current) {
//                        println("numberedIndentWidth ${numberedIndentWidth.toSp()}")
//                        16.sp.toDp().plus(numberedIndentWidth.toDp()).toSp()
//                    }
//
//                    val text = buildAnnotatedString {
//                        val paragraphStyle = ParagraphStyle(
//                            textIndent = TextIndent(
//                                firstLine = 16.sp,
//                                restLine = 16.sp,
//                            ),
//                        )
//
//                        val deeperParagraph = ParagraphStyle(
//                            textIndent = TextIndent(
//                                firstLine = numberedIndentWidthSp,
//                                restLine = numberedIndentWidthSp,
//                            ),
//                        )
//
//                        withAnnotation("blockquote", annotation = "what") {
//                            withStyle(paragraphStyle) {
//                                append("things and stuff. as well as the other things. but not including the non sequiturs")
//                            }
//
//                            withAnnotation("blockquote", annotation = "idk") {
//                                withStyle(deeperParagraph) {
//                                    append("0. this is a second paragraph which may be a quote block")
//                                }
//                            }
//
//                            withStyle(paragraphStyle) {
//                                append("idk a third paragraph I guess")
//                            }
//                        }
//
//                    }
//
//
//
//                    Text(
//                        modifier = Modifier.drawBehind { onDraw() },
//                        text = text,
//                        onTextLayout = { textLayoutResult ->
// //                            val paragraph = text.paragraphStyles.first()
//                            val annotation = text
//                                .getStringAnnotations("blockquote", 0, text.length)
//                                .drop(1).first()
//
//                            val textBounds = textLayoutResult
//                                .getBoundingBoxes(annotation.start, annotation.end)
//
//                            onDraw = {
//                                val colors = listOf(
//                                    Color.Cyan,
//                                    Color.Green,
//                                    Color.Red,
//                                )
//                                var i = 0
//                                for (bound in textBounds) {
//                                    drawRect(
//                                        color = colors[i % colors.size],
//                                        topLeft = bound.topLeft, //.minus(Offset(4.dp.toPx(), 0f)),
//                                        size = bound.size, //.copy(width = 2.dp.toPx()),
//                                    )
//
//
//                                    drawText(
//                                        textMeasurer = textMeasurer,
//                                        text = i.toString().plus("."),
//                                        topLeft = bound.topLeft.minus(
//                                            Offset(
//                                                numberedIndentWidth,
//                                                0f,
//                                            ),
//                                        ),
//                                        style = style,
//                                    )
//
//                                    i += 1
//                                }
//                            }
//                        },
//                    )
//
//                }

                items(
                    items = pages,
                    key = LobstersStory::shortId,
                ) { story ->
                    requireNotPlaceholder(story)

                    Story(
                        story = story,
                        onClick = onClick,
                    )
                }
            }
        },
    )
}
