package com.anix.android.anixstudyassist.feature.classdetails.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anix.android.anixstudyassist.feature.classdetails.ui.components.ChapterItem

data class Chapter(val title: String, val progress: Float, val isCompleted: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailsScreen(
    subjectName: String,
    onBackClick: () -> Unit
) {
    val chapters = listOf(
        Chapter("Introduction and Fundamentals", 1.0f, true),
        Chapter("Chapter 1: Core Concepts", 1.0f, true),
        Chapter("Chapter 2: Advanced Topics", 0.6f, false),
        Chapter("Chapter 3: Practical Applications", 0.3f, false),
        Chapter("Chapter 4: Problem Solving", 0.0f, false),
        Chapter("Chapter 5: Review and Practice", 0.0f, false)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subjectName, color = Color.White, fontWeight = FontWeight.Bold) },
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
                    IconButton(onClick = { /* Handle menu */ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EE))
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                OverallProgressHeader(progress = 0.48f)
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
                text = "${(progress * 100).toInt()}%",
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
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ClassDetailsScreenPreview() {
    ClassDetailsScreen(subjectName = "Subject", onBackClick = {})
}
