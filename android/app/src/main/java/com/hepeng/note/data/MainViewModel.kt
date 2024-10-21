package com.hepeng.note.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ImageRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val images = repository.allImages

    fun addImage(path: String) =
        viewModelScope.launch(ioDispatcher) { repository.insert(Image(path = path, isUpload = false))  }

    fun toggleUpload(imageItem: Image) =
        viewModelScope.launch(ioDispatcher) { repository.insert(imageItem.copy(isUpload = !imageItem.isUpload)) }

    fun removeImage(imageItem: Image) =
        viewModelScope.launch(ioDispatcher) { repository.delete(imageItem) }
}