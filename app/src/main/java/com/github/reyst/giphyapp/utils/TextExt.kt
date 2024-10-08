package com.github.reyst.giphyapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart

fun TextView.obtainChangesFlow(): Flow<CharSequence> = callbackFlow {
    val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            trySend(s ?: "")
        }
    }
    addTextChangedListener(listener)
    awaitClose { removeTextChangedListener(listener) }
}.onStart { emit(text ?: "") }

@FlowPreview
fun TextView.obtainDebouncedChangesFlow(debounceDelay: Long = 300L) = obtainChangesFlow()
    .debounce(debounceDelay)
