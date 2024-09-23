package com.github.reyst.giphyapp.ui.search

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.github.reyst.giphyapp.domain.entities.GiphyGif
import com.github.reyst.giphyapp.ui.details.DetailsActivity
import com.github.reyst.giphyapp.ui.theme.SearcherTheme
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

    Column(modifier = modifier.fillMaxSize()) {

        OutlinedTextField(queryState.value, onValueChange = onQueryChange)

        LazyVerticalGrid(
            columns = columnsCount,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(pagingItems.itemCount) { index ->

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
    }
}


/*
    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }


    private val adapter by lazy {
        SearchResultAdapter { DetailsActivity.start(this, it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initSubscriptions()
    }

    private fun initViews() {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        binding.searchResultGrid.layoutManager = GridLayoutManager(this, if (isLandscape) 5 else 3)
        binding.searchResultGrid.adapter = adapter
    }

    private fun initSubscriptions() {

        binding.queryText
            .obtainChangesFlow()
            .onEach { vm.updateQuery(it.toString()) }
            .launchIn(lifecycleScope)


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                vm.gifItems.collect(adapter::submitData)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->

                binding.refreshProgress.isVisible = loadStates.refresh == LoadState.Loading

                binding.updateProgress.isVisible =
                    loadStates.append == LoadState.Loading || loadStates.prepend == LoadState.Loading

                if (loadStates.hasError) {

                    listOf(loadStates.refresh, loadStates.append, loadStates.prepend)
                        .filterIsInstance<LoadState.Error>()
//                        .filter { !it.endOfPaginationReached }
                        .map {

                            Log.wtf("INSPECT", "error: ${it.error}", it.error)

                            when (it.error) {
                                is GiphyApiKeyException -> getString(R.string.key_exception)

                                is GiphyBadSearchRequest,
                                is TooLongQueryException -> getString(R.string.check_query)

                                is GiphyDownstreamSystemException -> getString(R.string.service_down)
                                else -> getString(R.string.general_error)
                            }
                        }
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString(separator = "\n")
                        ?.also {
                            Toast.makeText(this@SearchActivity, it, Toast.LENGTH_LONG).show()
                        }
                }
            }
        }
    }
*/

