package dev.thomasharris.lemon.feature.comments

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val args = CommentsArgs.fromSavedState(savedStateHandle)

    val id: String
        get() = args.storyId

    init {
        Log.i("TEH", "initting a new viewmodel ${args.storyId}")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("TEH", "onCleared ${args.storyId}")
    }
}
