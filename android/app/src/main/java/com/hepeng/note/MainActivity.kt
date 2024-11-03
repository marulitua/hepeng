package com.hepeng.note

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hepeng.note.ui.theme.HepengTheme
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyContent()
        }
    }
}

@Composable
fun MyContent() {
    val mContext = LocalContext.current

    HepengTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column {
                Greeting(
                    name = "Android",
                    modifier = Modifier.padding(innerPadding)
                )

                HorizontalDivider()

                Button(onClick = {
                    mContext.startActivity(
                        Intent(mContext, CameraActivity::class.java)
                    )
                    Toast.makeText(mContext, "Clicked!", Toast.LENGTH_SHORT).show()
                }) { Text("Add") }

                Button(onClick = {
                    Log.d(Constants.TAG, "list button")
                    mContext.startActivity(
                        Intent(mContext, ImageActivity::class.java)
                    )
                }) { Text("List") }

                Button(onClick = {
                    Log.d(Constants.TAG, "activate GRPC")
                    mContext.startActivity(
                        Intent(mContext, GrpcActivity::class.java)
                    )
                }) { Text("Grpc") }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HepengTheme {
        Greeting("Android")
    }
}