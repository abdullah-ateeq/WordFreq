package com.example.wordfreq.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordfreq.data.model.ApostropheMode
import com.example.wordfreq.data.model.HyphenMode
import com.example.wordfreq.data.model.ResultLimit
import com.example.wordfreq.data.model.SortMode
import com.example.wordfreq.data.model.ThemeMode
import com.example.wordfreq.ui.WordFreqViewModel
import com.example.wordfreq.util.HapticFeedbackHelper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: WordFreqViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val view = LocalView.current

    var showAddCustomStopWordDialog by remember { mutableStateOf(false) }
    var newCustomWord by remember { mutableStateOf("") }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                            showResetConfirmDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance
            item {
                SettingsSectionHeader("APPEARANCE")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Theme", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ThemeMode.values().forEach { mode ->
                                FilterChip(
                                    selected = uiState.themeMode == mode,
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        viewModel.setThemeMode(mode)
                                    },
                                    label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }
                                )
                            }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Dynamic Color", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Use Material You system colors (Android 12+)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = uiState.dynamicColor,
                                onCheckedChange = {
                                    HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                    viewModel.setDynamicColor(it)
                                }
                            )
                        }
                    }
                }
            }

            // Analysis Settings
            item {
                SettingsSectionHeader("ANALYSIS SETTINGS")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Case Sensitivity
                        Column {
                            Text("Case Sensitivity", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = !uiState.caseSensitive,
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        viewModel.setCaseSensitive(false)
                                    },
                                    label = { Text("Ignore Case") }
                                )
                                FilterChip(
                                    selected = uiState.caseSensitive,
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        viewModel.setCaseSensitive(true)
                                    },
                                    label = { Text("Respect Case") }
                                )
                            }
                        }

                        HorizontalDivider()

                        // Numbers
                        Column {
                            Text("Count Numbers", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = uiState.includeNumbers,
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        viewModel.setIncludeNumbers(true)
                                    },
                                    label = { Text("Include numbers") }
                                )
                                FilterChip(
                                    selected = !uiState.includeNumbers,
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        viewModel.setIncludeNumbers(false)
                                    },
                                    label = { Text("Exclude numbers") }
                                )
                            }
                        }

                        HorizontalDivider()

                        // Apostrophe Handling
                        Column {
                            Text("Apostrophe Handling", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("e.g. don't, can't, user's", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                ApostropheMode.values().forEach { mode ->
                                    FilterChip(
                                        selected = uiState.apostropheMode == mode,
                                        onClick = {
                                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                            viewModel.setApostropheMode(mode)
                                        },
                                        label = {
                                            val title = when (mode) {
                                                ApostropheMode.PRESERVE -> "Preserve (don't)"
                                                ApostropheMode.SPLIT -> "Split (don, t)"
                                                ApostropheMode.REMOVE -> "Remove (dont)"
                                            }
                                            Text(title)
                                        }
                                    )
                                }
                            }
                        }

                        HorizontalDivider()

                        // Hyphen Handling
                        Column {
                            Text("Hyphen Handling", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("e.g. well-known, state-of-the-art", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                HyphenMode.values().forEach { mode ->
                                    FilterChip(
                                        selected = uiState.hyphenMode == mode,
                                        onClick = {
                                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                            viewModel.setHyphenMode(mode)
                                        },
                                        label = {
                                            val title = when (mode) {
                                                HyphenMode.KEEP -> "Keep as one word"
                                                HyphenMode.SPLIT -> "Split into words"
                                                HyphenMode.REMOVE -> "Remove hyphen"
                                            }
                                            Text(title)
                                        }
                                    )
                                }
                            }
                        }

                        HorizontalDivider()

                        // Stop Words
                        Column {
                            Text("Stop Words (English)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Filter out common words like the, a, is, to", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = !uiState.stopWordsEnabled,
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        viewModel.setStopWordsEnabled(false)
                                    },
                                    label = { Text("Include all words") }
                                )
                                FilterChip(
                                    selected = uiState.stopWordsEnabled,
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        viewModel.setStopWordsEnabled(true)
                                    },
                                    label = { Text("Exclude English stop words") }
                                )
                            }
                        }

                        HorizontalDivider()

                        // Custom Stop Words
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Custom Exclusions", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text("Add words to exclude from analysis", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                OutlinedButton(
                                    onClick = { showAddCustomStopWordDialog = true },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ADD WORD")
                                }
                            }

                            if (uiState.customStopWords.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    uiState.customStopWords.forEach { word ->
                                        InputChip(
                                            selected = false,
                                            onClick = { },
                                            label = { Text(word) },
                                            trailingIcon = {
                                                IconButton(
                                                    onClick = {
                                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                                        viewModel.removeCustomStopWord(word)
                                                    },
                                                    modifier = Modifier.size(18.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = "Remove $word", modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider()

                        // Minimum Word Length
                        Column {
                            Text("Minimum Word Length: ${uiState.minimumWordLength}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(1, 2, 3, 4, 5, 6).forEach { len ->
                                    FilterChip(
                                        selected = uiState.minimumWordLength == len,
                                        onClick = {
                                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                            viewModel.setMinimumWordLength(len)
                                        },
                                        label = { Text("$len") }
                                    )
                                }
                            }
                        }

                        HorizontalDivider()

                        // Maximum Word Length
                        Column {
                            Text("Maximum Word Length: ${uiState.maximumWordLength?.let { "$it chars" } ?: "Unlimited"}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = uiState.maximumWordLength == null,
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        viewModel.setMaximumWordLength(null)
                                    },
                                    label = { Text("Unlimited") }
                                )
                                listOf(15, 20, 25, 30).forEach { maxLen ->
                                    FilterChip(
                                        selected = uiState.maximumWordLength == maxLen,
                                        onClick = {
                                            HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                            viewModel.setMaximumWordLength(maxLen)
                                        },
                                        label = { Text("$maxLen chars") }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Result Settings
            item {
                SettingsSectionHeader("RESULT SETTINGS")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Frequency Chart", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Switch(
                                checked = uiState.showFrequencyChart,
                                onCheckedChange = {
                                    HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                    viewModel.setShowFrequencyChart(it)
                                }
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Frequency Distribution Card", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Switch(
                                checked = uiState.showDistribution,
                                onCheckedChange = {
                                    HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                    viewModel.setShowDistribution(it)
                                }
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Word Length Summary Card", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Switch(
                                checked = uiState.showWordLength,
                                onCheckedChange = {
                                    HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                    viewModel.setShowWordLength(it)
                                }
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Processing Preview Card", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Switch(
                                checked = uiState.showPreview,
                                onCheckedChange = {
                                    HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                    viewModel.setShowPreview(it)
                                }
                            )
                        }

                        HorizontalDivider()

                        Column {
                            Text("Top Words Count in Summary", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = uiState.topWordsCount == 5,
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        viewModel.setTopWordsCount(5)
                                    },
                                    label = { Text("Top 5") }
                                )
                                FilterChip(
                                    selected = uiState.topWordsCount == 10,
                                    onClick = {
                                        HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                        viewModel.setTopWordsCount(10)
                                    },
                                    label = { Text("Top 10") }
                                )
                            }
                        }
                    }
                }
            }

            // Live Analysis & Behavior
            item {
                SettingsSectionHeader("BEHAVIOR")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Live Analysis", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Automatically update results as text changes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = uiState.liveAnalysis,
                                onCheckedChange = {
                                    HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                    viewModel.setLiveAnalysis(it)
                                }
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Haptic Feedback", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Subtle tactile feedback on user actions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = uiState.hapticFeedback,
                                onCheckedChange = {
                                    HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                    viewModel.setHapticFeedback(it)
                                }
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Confirm Delete", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Show confirmation dialogs before deleting text or history", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = uiState.confirmDelete,
                                onCheckedChange = {
                                    HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                    viewModel.setConfirmDelete(it)
                                }
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Restore Last Text", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("Reload previous text when reopening the app", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = uiState.restoreLastText,
                                onCheckedChange = {
                                    HapticFeedbackHelper.performHaptic(view, uiState.hapticFeedback)
                                    viewModel.setRestoreLastText(it)
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Add Custom Stop Word Dialog
    if (showAddCustomStopWordDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddCustomStopWordDialog = false
                newCustomWord = ""
            },
            title = { Text("Add Custom Exclusion") },
            text = {
                Column {
                    Text(
                        text = "Enter a word to exclude from frequency analysis:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCustomWord,
                        onValueChange = { newCustomWord = it },
                        placeholder = { Text("e.g. company, test") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCustomWord.isNotBlank()) {
                            viewModel.addCustomStopWord(newCustomWord.trim())
                        }
                        showAddCustomStopWordDialog = false
                        newCustomWord = ""
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddCustomStopWordDialog = false
                        newCustomWord = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset settings confirmation dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Settings?") },
            text = { Text("This will restore all settings to their default values. Your saved history will not be deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSettings()
                        showResetConfirmDialog = false
                    }
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}
