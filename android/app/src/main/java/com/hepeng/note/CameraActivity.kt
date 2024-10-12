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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CameraActivity : ComponentActivity() {
    private val _isCameraPermissionGranted = MutableStateFlow(false)
    val isCameraPermissionGranted: StateFlow<Boolean> = _isCameraPermissionGranted

    // Declare a laucher for the camera permission request
    private val cameraPermissionRequestLaucher: ActivityResultLauncher<String> =
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

    fun handleCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                _isCameraPermissionGranted.value = true
            }
            else -> {
                cameraPermissionRequestLaucher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val permissionGranted = isCameraPermissionGranted.collectAsState().value

            Box(modifier = Modifier.fillMaxSize()) {
                if (permissionGranted) {
                    CameraPreview()
                } else {
                    handleCameraPermission()
                }
            }
        }
    }
}