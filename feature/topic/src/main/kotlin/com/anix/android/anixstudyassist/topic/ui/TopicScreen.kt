package com.anix.android.anixstudyassist.topic.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.anix.android.anixstudyassist.topic.ui.components.ChapterItem
import com.anix.android.anixstudyassist.topic.viewmodel.TopicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(
    topicId: String,
    onBackClick: () -> Unit,
    onAddSubTopicClick: () -> Unit,
    onAddDataToStoreClick: () -> Unit,
    viewModel: TopicViewModel = hiltViewModel()
) {
    val topicState by viewModel.getTopic(topicId).collectAsState()
    val topic = topicState

    val chapters = topic?.subTopics ?: emptyList()
    val overallProgress = if (chapters.isEmpty()) 0f else chapters.sumOf { it.progress.toDouble() }
        .toFloat() / chapters.size

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        topic?.name ?: "Topic",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAddSubTopicClick) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Sub-topic",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ElevatedButton(
                onClick = onAddDataToStoreClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Add Data To Store")
            }

            if (chapters.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Its a great time to study today. Start a sub-topic",
                        color = Color.Gray,
                        modifier = Modifier.padding(32.dp),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    item {
                        OverallProgressHeader(progress = overallProgress)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    items(chapters) { chapter ->
                        ChapterItem(
                            title = chapter.title,
                            progress = chapter.progress,
                            isCompleted = chapter.isCompleted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OverallProgressHeader(progress: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Overall Progress",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = if (progress == 0f) "no progress" else "${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                color = Color(0xFF6200EE),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color(0xFF6200EE),
            trackColor = Color(0xFFEEEEEE),
            strokeCap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopicScreenPreview() {
    // Preview
}
