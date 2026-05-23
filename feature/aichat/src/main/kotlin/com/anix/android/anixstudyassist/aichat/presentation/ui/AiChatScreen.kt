package com.anix.android.anixstudyassist.aichat.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anix.android.anixstudyassist.aichat.presentation.state.AiChatUiState
import com.anix.android.anixstudyassist.aichat.presentation.state.ChatMessage
import com.anix.android.anixstudyassist.aichat.presentation.state.TextChatCapability
import com.anix.android.anixstudyassist.aichat.presentation.state.TextChatCapabilityOption
import com.anix.android.anixstudyassist.aichat.presentation.viewmodel.AiChatViewModel
import com.anix.android.anixstudyassist.aikit.domain.model.RewriteTone
import com.anix.android.anixstudyassist.ui.theme.AnixColors

private const val TAG = "ANIX_AiChatUI"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d(TAG, "AiChatScreen initialized")
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onMicClicked()
        }
    }

    val onMicClickAction = {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.onMicClicked()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (state.isVoiceViewActive) Icons.Default.Mic else Icons.Default.History,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (state.isVoiceViewActive) "Voice Assistant" else "AnixAI",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = if (state.isVoiceViewActive) viewModel::onBackToTextView else onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (state.isBusy && !state.isVoiceViewActive) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "AI Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
            )
        }
    ) { paddingValues ->
        if (state.isVoiceViewActive) {
            VoiceChatView(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onMicClick = onMicClickAction,
                onSendVoiceClick = viewModel::onSendVoiceClicked,
                onRetryClick = viewModel::onRetryClicked,
                onBackClick = viewModel::onBackToTextView
            )
        } else {
            TextChatView(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onInputChanged = viewModel::onInputChanged,
                onSendClicked = viewModel::onSendClicked,
                onMicClicked = onMicClickAction,
                onCapabilitySelected = viewModel::onCapabilitySelected,
                onCapabilityCleared = viewModel::onCapabilityCleared,
                onRewriteToneSelected = viewModel::onRewriteToneSelected
            )
        }
    }
}

@Composable
private fun TextChatView(
    modifier: Modifier = Modifier,
    state: AiChatUiState,
    onInputChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onMicClicked: () -> Unit,
    onCapabilitySelected: (TextChatCapability) -> Unit,
    onCapabilityCleared: () -> Unit,
    onRewriteToneSelected: (RewriteTone) -> Unit
) {
    var isCapabilityMenuExpanded by remember { mutableStateOf(false) }

    val isInputEnabled = !state.isBusy && (
            state.selectedCapability != TextChatCapability.REWRITE ||
                    state.selectedRewriteTone != null
            )

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.messages) { message ->
                ChatBubble(message)
            }
        }

        state.errorMessage?.let { error ->
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        SelectedCapabilityBanner(
            selectedCapability = state.selectedCapability,
            selectedTone = state.selectedRewriteTone,
            toneOptions = state.rewriteToneOptions,
            onClearSelection = onCapabilityCleared,
            onToneSelected = onRewriteToneSelected
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            CapabilityPicker(
                capabilities = state.availableCapabilities,
                enabled = !state.isBusy,
                expanded = isCapabilityMenuExpanded,
                onExpandedChange = { isCapabilityMenuExpanded = it },
                onCapabilitySelected = { capability ->
                    onCapabilitySelected(capability)
                    isCapabilityMenuExpanded = false
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ProcessingStatusBanner(processingFeature = state.currentProcessingFeature)

        ChatInput(
            value = state.inputText,
            isBusy = !isInputEnabled,
            isListening = state.isListening,
            onValueChange = onInputChanged,
            onSendClick = onSendClicked,
            onMicClick = onMicClicked,
            shouldShowMicButton = state.shouldShowMicButton()
        )
    }
}

@Composable
private fun SelectedCapabilityBanner(
    selectedCapability: TextChatCapability?,
    selectedTone: RewriteTone?,
    toneOptions: List<RewriteTone>,
    onClearSelection: () -> Unit,
    onToneSelected: (RewriteTone) -> Unit
) {
    val colors = AnixColors.current
    if (selectedCapability == null) return

    val label = if (selectedCapability == TextChatCapability.REWRITE && selectedTone != null) {
        "Selected: Rewrite (${selectedTone.name.lowercase().capitalizeFirstLetter()})"
    } else {
        "Selected: ${selectedCapability.displayName()}"
    }

    Surface(
        color = colors.primary.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = colors.primary,
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                TextButton(onClick = onClearSelection) {
                    Text("Clear")
                }
            }

            if (selectedCapability == TextChatCapability.REWRITE && selectedTone == null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select a tone to continue:",
                    fontSize = 12.sp,
                    color = colors.subText,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(toneOptions) { tone ->
                        AssistChip(
                            onClick = { onToneSelected(tone) },
                            label = { Text(tone.name.lowercase().capitalizeFirstLetter()) },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = colors.primary,
                                containerColor = Color.White
                            ),
                            border = AssistChipDefaults.assistChipBorder(
                                borderColor = colors.primary.copy(alpha = 0.3f),
                                enabled = true
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun String.capitalizeFirstLetter(): String =
    this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.ROOT) else it.toString() }

@Composable
private fun CapabilityPicker(
    capabilities: List<TextChatCapabilityOption>,
    enabled: Boolean,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCapabilitySelected: (TextChatCapability) -> Unit
) {
    Box {
        IconButton(
            onClick = { onExpandedChange(!expanded) },
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color(0xFF6200EE),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFB39DDB),
                disabledContentColor = Color.White
            ),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Show capabilities"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            capabilities.forEachIndexed { index, capability ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = capability.title,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = capability.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = { onCapabilitySelected(capability.capability) }
                )
                if (index != capabilities.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun VoiceChatView(
    modifier: Modifier = Modifier,
    state: AiChatUiState,
    onMicClick: () -> Unit,
    onSendVoiceClick: () -> Unit,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val colors = AnixColors.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.messages) { message ->
                ChatBubble(message)
            }

            if (state.isListening || state.voiceTranscription.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.primary,
                            tonalElevation = 4.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.isListening) {
                                    ThreeDotLoadingAnimation(color = Color.White)
                                } else {
                                    Text(
                                        state.voiceTranscription,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (state.isBusy) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.surface,
                            tonalElevation = 4.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ThreeDotLoadingAnimation(color = colors.primary)
                            }
                        }
                    }
                }
            }

            state.errorMessage?.let { error ->
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp),
                            maxLines = 6,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onMicClick,
                    modifier = Modifier.size(80.dp),
                    enabled = !state.isBusy,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (state.isListening) Color.Red else Color(0xFF6200EE),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Control",
                        modifier = Modifier.size(40.dp)
                    )
                }

                if (state.showRetryButton) {
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(
                        onClick = onRetryClick,
                        modifier = Modifier.size(60.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFFFA000),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Retry",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                } else if (state.voiceTranscription.isNotEmpty() && !state.isBusy) {
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(
                        onClick = onSendVoiceClick,
                        modifier = Modifier.size(60.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Voice Transcription",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Back to Text Chat",
                    tint = Color.Gray
                )
            }
            Text("Switch to Text Chat", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun ThreeDotLoadingAnimation(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(color, dot1Alpha)
        Dot(color, dot2Alpha)
        Dot(color, dot3Alpha)
    }
}

@Composable
private fun Dot(color: Color, alpha: Float) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .alpha(alpha)
            .background(color, CircleShape)
    )
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val colors = AnixColors.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (message.isFromUser) colors.primary else colors.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    color = if (message.isFromUser) colors.surface else colors.mainText,
                    fontSize = 14.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    if (message.isVoice) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = if (message.isFromUser) colors.surface.copy(alpha = 0.7f) else colors.subText,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = message.time,
                        color = if (message.isFromUser) colors.surface.copy(alpha = 0.7f) else colors.subText,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProcessingStatusBanner(processingFeature: String?) {
    val colors = AnixColors.current
    if (processingFeature == null) return

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = processingFeature,
            color = colors.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ChatInput(
    value: String,
    isBusy: Boolean,
    isListening: Boolean,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit,
    shouldShowMicButton: Boolean = true
) {
    val colors = AnixColors.current

    Surface(
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        "Type your message...",
                        color = colors.subText,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = !isBusy,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = colors.mainText,
                    unfocusedTextColor = colors.mainText,
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xBFF5F5F5),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            if (shouldShowMicButton) {
                IconButton(
                    onClick = onMicClick,
                    enabled = !isBusy,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isListening) Color.Red else Color.Transparent,
                        contentColor = if (isListening) Color.White else colors.primary
                    )
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice Chat")
                }
            } else {
                IconButton(
                    onClick = onSendClick,
                    enabled = !isBusy && value.isNotBlank(),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFF6200EE),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFB39DDB),
                        disabledContentColor = Color.White
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

private fun TextChatCapability.displayName(): String = when (this) {
    TextChatCapability.SUMMARIZE -> "Summarize"
    TextChatCapability.PROOFREAD -> "Proofread"
    TextChatCapability.REWRITE -> "Rewrite"
}
