package com.hepeng.note.composeable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.hepeng.note.GreeterRCP
import com.hepeng.note.R
import kotlinx.coroutines.launch

@Composable
fun Greeter(greeterRCP: GreeterRCP) {

    val scope = rememberCoroutineScope()

    val nameState = remember { mutableStateOf(TextFieldValue()) }

    Column(Modifier.fillMaxWidth().fillMaxHeight(), Arrangement.Top, Alignment.CenterHorizontally) {
        Text(stringResource(R.string.name_hint), modifier = Modifier.padding(top = 10.dp))
        OutlinedTextField(nameState.value, { nameState.value = it })

        Button({ scope.launch { greeterRCP.sayHello(nameState.value.text) } }, Modifier.padding(10.dp)) {
            Text(stringResource(R.string.send_request))
        }

        if (greeterRCP.responseState.value.isNotEmpty()) {
            Text(stringResource(R.string.server_response), modifier = Modifier.padding(top = 10.dp))
            Text(greeterRCP.responseState.value)
        }
    }
}
