package com.github.reyst.giphyapp.ui.details

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.github.reyst.giphyapp.R
import com.github.reyst.giphyapp.databinding.ActivityDetailsBinding
import com.github.reyst.giphyapp.domain.entities.GiphyGif
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailsActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetailsBinding.inflate(layoutInflater) }

    private val vm by viewModel<DetailsVM>()
    private val adapter by lazy { DetailsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        vm.state
            .onEach {
                binding.image.load(it.url)
                adapter.setItems(it.options)
            }
            .launchIn(lifecycleScope)


        binding.rvDetails.adapter = adapter
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
