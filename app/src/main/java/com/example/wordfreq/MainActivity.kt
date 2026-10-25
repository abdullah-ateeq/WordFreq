package com.example.wordfreq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.wordfreq.navigation.WordFreqNavHost
import com.example.wordfreq.ui.WordFreqViewModel
import com.example.wordfreq.ui.theme.WordFreqTheme

class MainActivity : ComponentActivity() {

    private val viewModel: WordFreqViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            WordFreqTheme(
                themeMode = uiState.themeMode,
                dynamicColor = uiState.dynamicColor
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WordFreqNavHost(viewModel = viewModel)
                }
            }
        }
    }
}
