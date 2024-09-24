package com.github.reyst.giphyapp.ui.search

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.github.reyst.giphyapp.R
import com.github.reyst.giphyapp.domain.entities.GiphyGif
import com.github.reyst.giphyapp.domain.entities.ImageVariants
import com.github.reyst.giphyapp.domain.exceptions.GiphyApiKeyException
import com.github.reyst.giphyapp.domain.exceptions.GiphyBadSearchRequest
import com.github.reyst.giphyapp.domain.exceptions.GiphyDownstreamSystemException
import com.github.reyst.giphyapp.domain.exceptions.TooLongQueryException
import com.github.reyst.giphyapp.ui.details.DetailsActivity
import com.github.reyst.giphyapp.ui.theme.SearcherTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : ComponentActivity() {

    private val vm by viewModel<SearchVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SearcherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SearchScreen(
                        queryState = vm.query.collectAsState(),
                        onQueryChange = vm::updateQuery,
                        pagingItems = vm.gifItems.collectAsLazyPagingItems(),
                        modifier = Modifier.padding(innerPadding),
                        onItemClick = ::showDetails
                    )
                }
            }
        }
    }

    private fun showDetails(gif: GiphyGif) = DetailsActivity.start(this, gif)
}

@Composable
fun SearchScreen(
    queryState: State<String>,
    onQueryChange: (String) -> Unit,
    pagingItems: LazyPagingItems<GiphyGif>,
    modifier: Modifier = Modifier,
    onItemClick: (GiphyGif) -> Unit = {},
) {

    val orientation = LocalConfiguration.current.orientation

    val columnsCount = remember(orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) GridCells.Fixed(5)
        else GridCells.Fixed(3)

    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {

        OutlinedTextField(
            queryState.value,
            onValueChange = onQueryChange,
            label = { Text(stringResource(R.string.query_caption)) },
            trailingIcon = {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(imageVector = Icons.Default.Clear, null)
                }


            },
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {

            LazyVerticalGrid(
                columns = columnsCount,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),

                modifier = Modifier.fillMaxSize(),
            ) {

                items(
                    pagingItems.itemCount,
                    key = { pagingItems[it]?.id.orEmpty() },
                ) { index ->

                    pagingItems[index]
                        ?.let {
                            AsyncImage(
                                model = it.images.original.url,
                                contentDescription = it.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clickable { onItemClick(it) }
                            )
                        }
                        ?: Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                }
            }

            val loadState = pagingItems.loadState
            if (loadState.refresh == LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .size(128.dp)
                        .align(alignment = Alignment.TopCenter)
                )
            }

            if (loadState.append == LoadState.Loading || loadState.prepend == LoadState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            val ctx = LocalContext.current

            if (loadState.hasError) {
                LaunchedEffect(loadState) {
                    listOf(loadState.refresh, loadState.append, loadState.prepend)
                        .filterIsInstance<LoadState.Error>()
                        .map {
                            when (it.error) {
                                is GiphyApiKeyException -> ctx.getString(R.string.key_exception)

                                is GiphyBadSearchRequest,
                                is TooLongQueryException -> ctx.getString(R.string.check_query)

                                is GiphyDownstreamSystemException -> ctx.getString(R.string.service_down)
                                else -> ctx.getString(R.string.general_error)
                            }
                        }
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(separator = "\n")
                        ?.also { Toast.makeText(ctx, it, Toast.LENGTH_LONG).show() }
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {

    SearcherTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            val qState = remember { mutableStateOf("Car") }

            val flow = remember {
                val fakeData = listOf(
                    GiphyGif(
                        id = "id1",
                        url = "url",
                        images = ImageVariants()
                    ),
                    GiphyGif(
                        id = "id2",
                        url = "url",
                        images = ImageVariants()
                    ),
                )

                val pagingData = PagingData.from(fakeData)
                MutableStateFlow(pagingData)
            }

            SearchScreen(
                queryState = qState,
                onQueryChange = { qState.value = it },
                pagingItems = flow.collectAsLazyPagingItems(),
                modifier = Modifier.padding(innerPadding),
                onItemClick = { },
            )
        }
    }

}