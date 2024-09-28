package com.hepeng.note

import android.net.Uri
import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.concurrent.Executors
import com.hepeng.note.CameraFileUtils.takePicture
import coil.compose.rememberAsyncImagePainter

@Composable
fun CameraPreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // Executor for background tasks, specifically for taking pictures in this context
    val executor = remember { Executors.newSingleThreadExecutor() }

    val capturedImageUri = remember { mutableStateOf<Uri?>(null) }

    // Camera controller tied to the lifecycle of this composable
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner) // Binds the camera to the lifecycle of the lifecycleOwner
        }
    }


    Box {

        // PreviewView for the camera feed. Configured to fill the
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_START
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    controller = cameraController
                }
            },
            onRelease = {
                cameraController.unbind()
            }
        )
        Button(
            onClick = {
                Log.d("clicked:", "capture button")
                // Calls a utility function to take a picture, handling success
                takePicture(cameraController, context, executor, { uri ->
                    capturedImageUri.value =
                        uri // Update state with the URI of the captured image on success
                }
                    /*
                , { exception ->
                // Error handling logic for image capture failures
            }*/
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Capture")
        }

        // Displays the captured image if available
        capturedImageUri.value?.let { uri ->
            Image(
                // Asynchronously loads an displays the image from the URI
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .align(Alignment.BottomEnd),
                contentScale = ContentScale.Crop
            )
        }
    }
}