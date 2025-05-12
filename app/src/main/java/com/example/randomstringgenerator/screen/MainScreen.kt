package com.example.randomstringgenerator.screen

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.randomstringgenerator.model.RandomText
import com.example.randomstringgenerator.viewmodel.MainViewModel
import com.example.randomstringgenerator.viewmodel.MainViewModelFactory
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun MainScreen() {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(context))

    val randomTexts by viewModel.randomTexts.observeAsState(emptyList())


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Button(onClick = { viewModel.generateRandomText(10..20) }) {
            Text("Generate Random Text")
        }

        Spacer(modifier = Modifier.height(30.dp))

        LazyColumn {
            items(randomTexts) { randomText: RandomText ->
                RandomTextItem(randomText)
            }
        }
    }
}

@Composable
fun RandomTextItem(randomText: RandomText) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text("Timestamp: ${randomText.timestamp}")
        Text("Length: ${randomText.length}")
        Text("Text: ${randomText.text}")
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen()
}
