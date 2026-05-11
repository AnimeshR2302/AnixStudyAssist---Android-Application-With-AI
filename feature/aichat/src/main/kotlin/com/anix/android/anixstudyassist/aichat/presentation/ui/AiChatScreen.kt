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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anix.android.anixstudyassist.aichat.presentation.state.ChatMessage
import com.anix.android.anixstudyassist.aichat.presentation.viewmodel.AiChatViewModel
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
                onBackClick = viewModel::onBackToTextView
            )
        } else {
            TextChatView(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onInputChanged = viewModel::onInputChanged,
                onSendClicked = viewModel::onSendClicked,
                onMicClicked = onMicClickAction
            )
        }
    }
}

@Composable
private fun TextChatView(
    modifier: Modifier = Modifier,
    state: com.anix.android.anixstudyassist.aichat.presentation.state.AiChatUiState,
    onInputChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onMicClicked: () -> Unit
) {
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

        ChatInput(
            value = state.inputText,
            isBusy = state.isBusy,
            isListening = state.isListening,
            onValueChange = onInputChanged,
            onSendClick = onSendClicked,
            onMicClick = onMicClicked
        )
    }
}

@Composable
private fun VoiceChatView(
    modifier: Modifier = Modifier,
    state: com.anix.android.anixstudyassist.aichat.presentation.state.AiChatUiState,
    onMicClick: () -> Unit,
    onSendVoiceClick: () -> Unit,
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
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (state.isListening) Color.Red else Color(0xFF6200EE),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Control",
                        modifier = Modifier.size(40.dp)
                    )
                }

                if (state.voiceTranscription.isNotEmpty() && !state.isBusy) {
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
private fun ChatInput(
    value: String,
    isBusy: Boolean,
    isListening: Boolean,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit
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
            Spacer(modifier = Modifier.width(4.dp))
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
