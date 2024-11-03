package com.hepeng.note

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import com.hepeng.note.composeable.Greeter
import com.hepeng.note.helloworld.GreeterGrpcKt
import com.hepeng.note.helloworld.helloRequest
import com.hepeng.note.ui.constants.HepengItemBackgroundColor
import io.grpc.ManagedChannelBuilder
//import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.io.Closeable

class GrpcActivity : ComponentActivity() {

    val uri by lazy { Uri.parse(resources.getString(R.string.server_url)) }
    val greeterService by lazy { GreeterRCP(uri) }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            //Surface(color = MaterialTheme.colors.background) {
            Surface(color = HepengItemBackgroundColor) {
                Greeter(greeterService)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        greeterService.close()
    }
}

class GreeterRCP(uri: Uri) : Closeable {
    val responseState = mutableStateOf("")

    private val channel = let {
        println("Connecting to ${uri.host}:${uri.port}")

        val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)
        if (uri.scheme == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }

        builder.executor(Dispatchers.IO.asExecutor()).build()
    }

    private val greeter = GreeterGrpcKt.GreeterCoroutineStub(channel)

    suspend fun sayHello(name: String) {
        try {
            val request = helloRequest { this.name = name }
            val response = greeter.sayHello(request)
            responseState.value = response.message
        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
            e.printStackTrace()
        }
    }

    override fun close() {
        channel.shutdownNow()
    }
}