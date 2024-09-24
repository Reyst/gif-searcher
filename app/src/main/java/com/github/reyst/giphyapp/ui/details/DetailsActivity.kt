package com.github.reyst.giphyapp.ui.details

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.reyst.giphyapp.domain.entities.GiphyGif
import com.github.reyst.giphyapp.domain.entities.ImageData
import com.github.reyst.giphyapp.ui.theme.SearcherTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailsActivity : ComponentActivity() {

    private val vm by viewModel<DetailsVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            val gif =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(PAYLOAD, GiphyGif::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(PAYLOAD) as? GiphyGif
                }
            gif?.also(vm::setData) ?: finish()
        }

        setContent {
            SearcherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DetailsScreen(
                        state = vm.state.collectAsState(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    companion object {

        fun start(activity: Activity, gif: GiphyGif) {
            Intent(activity, DetailsActivity::class.java)
                .putExtra(PAYLOAD, gif)
                .also(activity::startActivity)
        }

        const val PAYLOAD = "data"
    }
}

@Composable
fun DetailsScreen(
    state: State<DetailsState>,
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier) {
        val configuration = LocalConfiguration.current

        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) DetailsLand(state)
        else DetailsPort(state)
    }
}

@Composable
fun DetailsLand(state: State<DetailsState>, modifier: Modifier = Modifier) {

    Row(modifier = modifier.fillMaxSize()) {
        GiphyGifImage(
            url = state.value.url, modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.25F)
        )
        PropertiesList(state.value.options, modifier = Modifier.fillMaxSize())
    }
}


@Composable
fun DetailsPort(state: State<DetailsState>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        GiphyGifImage(
            url = state.value.url, modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25F)
        )
        PropertiesList(state.value.options, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun GiphyGifImage(url: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier
    )
}

@Composable
private fun PropertiesList(options: List<DetailOption>, modifier: Modifier = Modifier) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(options) { item ->
            when (item) {
                is General -> SimpleProperty(item)
                is Image -> ImageProperty(item)
            }
        }
    }
}

@Composable
fun ImageProperty(option: Image) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val imageData = option.value

        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "${imageData.width} x ${imageData.height}")

                Text(text = getSizeString("size", imageData.size))
                UrlText("url", imageData.url)
                Text(text = getSizeString("MP4 size", imageData.mp4Size))
                UrlText("MP4 url", imageData.mp4Url)
                Text(text = getSizeString("Webp size", imageData.webpSize))
                UrlText("Webp url", imageData.webpUrl)
            }
        }
    }
}

@Composable
fun UrlText(title: String, url: String) {
    val uriHandler = LocalUriHandler.current
    Text(
        text = buildAnnotatedString {
            append("$title: ")

            if (url.isBlank()) append(" - ")
            else withLink(
                LinkAnnotation.Url(
                    url = url,
                    styles = TextLinkStyles(
                        SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        )
                    ),
                    linkInteractionListener = { uriHandler.openUri(url) }
                )
            ) { append(url) }
        }
    )
}

private fun getSizeString(title: String, size: Int): String {
    val sizeStr = size.takeIf { it > 0 }
        ?.toString()
        ?: " - "

    return "$title: $sizeStr"
}

@Composable
fun SimpleProperty(options: General) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = "${options.title}:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.wrapContentSize()
            )
            Text(
                text = options.value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    }
}

@Preview
@Composable
fun DetailsScreenPreview() {
    SearcherTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            DetailsScreen(
                state = remember {
                    mutableStateOf(
                        DetailsState(
                            url = "https://media0.giphy.com/media/1X2K7D9txBkDS/giphy.gif",
                            options = listOf(
                                General("Title", "title"),
                                Image(
                                    "original",
                                    ImageData(url = "https://media0.giphy.com/media/1X2K7D9txBkDS/giphy.gif?cid=e46b4c94hfo5whmf6juwq1sp2ygdtcr6ybdlwqycan62vf2b&ep=v1_gifs_search&rid=giphy.gif&ct=g")
                                )
                            )

                        )
                    )
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}