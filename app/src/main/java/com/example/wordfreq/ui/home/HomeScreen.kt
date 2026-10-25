package com.example.wordfreq.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordfreq.data.model.WordFreqUiState
import com.example.wordfreq.ui.WordFreqViewModel
import com.example.wordfreq.util.HapticFeedbackHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WordFreqViewModel,
    uiState: WordFreqUiState,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showClearConfirmDialog by remember { mutableStateOf(false) }

    // Show error or success messages as snackbars
    LaunchedEffect(uiState.errorMessage, uiState.successMessage, uiState.infoMessage) {
        val msg = uiState.errorMessage ?: uiState.successMessage ?: uiState.infoMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "WordFreq",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Count how frequently each word appears in text.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                            onNavigateToHistory()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Saved history",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                            onNavigateToSettings()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                            onNavigateToAbout()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About WordFreq",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Text Input Area
            item(key = "text_input") {
                TextInputSection(
                    text = uiState.text,
                    onTextChanged = { viewModel.onTextChanged(it) },
                    onClearClicked = {
                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                        if (uiState.confirmDelete && uiState.text.isNotEmpty()) {
                            showClearConfirmDialog = true
                        } else {
                            viewModel.clearAllText()
                        }
                    },
                    liveCharCount = uiState.liveCharCount,
                    liveWordCount = uiState.liveWordCount,
                    liveLineCount = uiState.liveLineCount
                )
            }

            // Analyze Button
            item(key = "analyze_action") {
                AnalyzeActionBar(
                    isAnalyzing = uiState.isAnalyzing,
                    liveAnalysis = uiState.liveAnalysis,
                    onAnalyzeClicked = {
                        HapticFeedbackHelper.performConfirmHaptic(view, uiState.hapticFeedback)
                        viewModel.onAnalyzeClicked()
                    }
                )
            }

            // Filters Summary
            item(key = "filters_summary") {
                FiltersSummaryCard(uiState = uiState)
            }

            // Analysis Results Section (only if analyzed results exist)
            if (uiState.validWords > 0) {
                // Section Header
                item(key = "results_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WORD FREQUENCY",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${uiState.validWords} WORDS • ${uiState.uniqueWords} UNIQUE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // Summary Statistics Cards
                item(key = "results_summary") {
                    ResultSummaryCards(uiState = uiState)
                }

                // Optional Top Words Frequency Chart
                if (uiState.showFrequencyChart) {
                    item(key = "frequency_chart") {
                        TopWordsChartSection(
                            allResults = uiState.allResults,
                            topCount = uiState.topWordsCount
                        )
                    }
                }

                // Optional Frequency Distribution Card
                if (uiState.showDistribution) {
                    item(key = "frequency_distribution") {
                        ExpandableFrequencyDistributionCard(
                            distribution = uiState.frequencyDistribution,
                            initiallyExpanded = true
                        )
                    }
                }

                // Optional Word Length Card
                if (uiState.showWordLength) {
                    item(key = "word_length_summary") {
                        ExpandableWordLengthCard(
                            shortestWord = uiState.shortestWord,
                            longestWord = uiState.longestWord,
                            averageWordLength = uiState.averageWordLength,
                            initiallyExpanded = true
                        )
                    }
                }

                // Optional Processing Preview Card
                if (uiState.showPreview) {
                    item(key = "processing_preview") {
                        ExpandableProcessingPreviewCard(
                            originalSnippet = uiState.text,
                            normalizedTokens = uiState.normalizedPreview,
                            initiallyExpanded = true
                        )
                    }
                }

                // Action Buttons: Copy Results, Save, Share, Export CSV
                item(key = "action_buttons") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("WordFreq Results", viewModel.getCopyText(all = false))
                                        clipboard.setPrimaryClip(clip)
                                        scope.launch { snackbarHostState.showSnackbar("Results copied to clipboard.") }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy")
                                }

                                Button(
                                    onClick = {
                                        HapticFeedbackHelper.performConfirmHaptic(view, uiState.hapticFeedback)
                                        viewModel.saveCurrentAnalysis()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save")
                                }

                                OutlinedButton(
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, "WordFreq Analysis")
                                            putExtra(Intent.EXTRA_TEXT, viewModel.getShareText())
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Word Frequency"))
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.IosShare, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share")
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("WordFreq All Results", viewModel.getCopyText(all = true))
                                        clipboard.setPrimaryClip(clip)
                                        scope.launch { snackbarHostState.showSnackbar("All ${uiState.uniqueWords} words copied.") }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Copy All", style = MaterialTheme.typography.labelMedium)
                                }

                                OutlinedButton(
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        val csv = viewModel.getCsvContent()
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/csv"
                                            putExtra(Intent.EXTRA_SUBJECT, "WordFreq_Results.csv")
                                            putExtra(Intent.EXTRA_TEXT, csv)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Export CSV"))
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Export CSV", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }

                // Search & Filter Bar
                item(key = "search_and_filter") {
                    SearchAndFilterControls(
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        sortMode = uiState.sortMode,
                        onSortModeChanged = {
                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                            viewModel.onSortModeChanged(it)
                        },
                        sortDescending = uiState.sortDescending,
                        onToggleSortDirection = {
                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                            viewModel.onSortDirectionToggled()
                        },
                        resultLimit = uiState.resultLimit,
                        onResultLimitChanged = {
                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                            viewModel.onResultLimitChanged(it)
                        }
                    )
                }

                // Check if search returned 0 items
                if (uiState.results.isEmpty() && uiState.searchQuery.isNotBlank()) {
                    item(key = "no_search_results") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No words match \"${uiState.searchQuery}\".",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Result list items
                val maxFrequency = uiState.allResults.maxOfOrNull { it.frequency } ?: 1
                items(
                    items = uiState.results,
                    key = { "${it.word}_${it.rank}" }
                ) { wordFreq ->
                    ResultItemRow(
                        item = wordFreq,
                        maxFrequency = maxFrequency,
                        onCopyWord = { word ->
                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Word", word)
                            clipboard.setPrimaryClip(clip)
                            scope.launch { snackbarHostState.showSnackbar("Copied \"$word\"") }
                        }
                    )
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Clear confirmation dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear all text?") },
            text = { Text("This will clear your input text and reset the current frequency results.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearConfirmDialog = false
                        viewModel.clearAllText()
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
