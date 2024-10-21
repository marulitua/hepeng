package com.hepeng.note

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.hepeng.note.data.AppDatabase
import com.hepeng.note.data.ImageRepository
import com.hepeng.note.data.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CameraActivity : ComponentActivity() {
    private val _isCameraPermissionGranted = MutableStateFlow(false)
    private val isCameraPermissionGranted: StateFlow<Boolean> = _isCameraPermissionGranted

    // Declare a launcher for the camera permission request
    private val cameraPermissionRequestLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
            if (isGranted) {
                _isCameraPermissionGranted.value = true
            } else {
                Toast.makeText(
                    this,
                    "NEED camera permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun handleCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                _isCameraPermissionGranted.value = true
            }
            else -> {
                cameraPermissionRequestLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val mainViewModel = MainViewModel(ImageRepository(db.userDao()), ioDispatcher = Dispatchers.IO)

        setContent {
            val permissionGranted = isCameraPermissionGranted.collectAsState().value

            Box(modifier = Modifier.fillMaxSize()) {
                if (permissionGranted) {
                    CameraPreview(mainViewModel)
                } else {
                    handleCameraPermission()
                }
            }
        }
    }
}