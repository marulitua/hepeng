package com.hepeng.note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.hepeng.note.composeable.ImageItemsContainer
import com.hepeng.note.data.AppDatabase
import com.hepeng.note.data.ImageRepository
import com.hepeng.note.data.MainViewModel
import kotlinx.coroutines.Dispatchers

class ImageActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getInstance(applicationContext)
        val mainViewModel = MainViewModel(ImageRepository(db.userDao()), ioDispatcher = Dispatchers.IO)
        setContent{
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                ImageItemsContainer(
                    imageItemsFlow = mainViewModel.images,
                    onItemUpload = mainViewModel::toggleUpload,
                    //overlappingElementsHeight = OverlappingHeight
                )
            }
        }
    }


}