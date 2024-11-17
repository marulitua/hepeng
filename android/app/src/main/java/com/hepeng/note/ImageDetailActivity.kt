package com.hepeng.note

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


class ImageDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filename = getIntent().getStringExtra("filename")
        setContent {
            filename?.let { filename ->
                Box {
                    Text(text = filename)
                    Image(
                        // Asynchronously loads an displays the image from the URI
                        painter = rememberAsyncImagePainter(filename),
                        contentDescription = null,
                        modifier = Modifier
                            .width(800.dp)
                            .align(Alignment.BottomEnd),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}