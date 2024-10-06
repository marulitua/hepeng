package com.hepeng.note

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService

object CameraFileUtils {

    fun takePicture(
        cameraController: CameraController,
        context: Context,
        executor: ExecutorService,
        onImageCaptured: (Uri) -> Unit,
        onErrorc: (ImageCaptureException) -> Unit
    ) {
        // Create a file to save the photo
        val photoFile = createPhotoFile(context)
        // Prepare the output file options for the ImageCapture use case
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Instruct the cameraController to take a picture
        cameraController.takePicture(
            outputOptions,
            executor,
            object: ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // On successful capture, invoke callback with the uri of the saved file
                    Uri.fromFile(photoFile).let(onImageCaptured)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("HEPENG", "Photo capture failed: ${exception.message}", exception)
                    onErrorc(exception)
                }
            }
        )
    }

    // Helper function to create a file in the external storage directory for the photo
    private fun createPhotoFile(context: Context): File {
        // Obtain the directory for saving the photo
        val outputDirectory = getOutputDirectory(context)
        // Create a new file in the output directory with a unique name
        return File(outputDirectory, photoFileName()).apply {
            // Ensure the file's parent directory exists
            parentFile?.mkdirs()
        }
    }

    // Generates a unique file name for the photo based on the current timestamp
    private fun photoFileName() =
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"

    // Determines the best directory for saving the photo, preferring external but falling back to internal storage
    private fun getOutputDirectory(context: Context): File {
        // Attempt to use the app-specific external storage directory which does not require permissions
        val mediaDir = context.getExternalFilesDir(null)?.let  {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        // Fallback to internal storage if the external directory is not available
        return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
    }
}