package com.github.reyst.giphyapp.ui.search

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.github.reyst.giphyapp.R
import com.github.reyst.giphyapp.databinding.ActivitySearchBinding
import com.github.reyst.giphyapp.domain.exceptions.GiphyApiKeyException
import com.github.reyst.giphyapp.domain.exceptions.GiphyBadSearchRequest
import com.github.reyst.giphyapp.domain.exceptions.GiphyDownstreamSystemException
import com.github.reyst.giphyapp.domain.exceptions.TooLongQueryException
import com.github.reyst.giphyapp.ui.details.DetailsActivity
import com.github.reyst.giphyapp.utils.obtainChangesFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySearchBinding.inflate(layoutInflater) }

    private val vm by viewModel<SearchVM>()

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
}

