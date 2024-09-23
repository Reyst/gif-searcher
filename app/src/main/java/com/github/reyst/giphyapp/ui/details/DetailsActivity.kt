package com.github.reyst.giphyapp.ui.details

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
    LazyColumn(modifier = modifier) {
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(text = option.title)
            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            Text(text = "${imageData.width} x ${imageData.height}")

            Text(text = getSizeString("size", imageData.size))
            Text(text = getUrlString("url", imageData.url))
            Text(text = getSizeString("MP4 size", imageData.mp4Size))
            Text(text = getUrlString("MP4 url", imageData.mp4Url))
            Text(text = getSizeString("Webp size", imageData.webpSize))
            Text(text = getUrlString("Webp url", imageData.webpUrl))
        }
    }
}

private fun getUrlString(title: String, url: String) = "$title: ${url.ifBlank { " - " }}"
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
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(text = "${options.title}:", modifier = Modifier.wrapContentSize())
            Text(
                text = options.value, modifier = Modifier
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
                            url = "https://media0.giphy.com/media/1X2K7D9txBkDS/giphy.gif?cid=e46b4c94hfo5whmf6juwq1sp2ygdtcr6ybdlwqycan62vf2b&ep=v1_gifs_search&rid=giphy.gif&ct=g",
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