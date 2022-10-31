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
    val id = savedStateHandle.get<String>("storyId")!!

    init {
        Log.i("TEH", "initting a new viewmodel $id")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("TEH", "onCleared $id")
    }
}
