package com.anix.android.anixstudyassist.feature.landing.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anix.android.anixstudyassist.feature.landing.ui.components.SubjectCard

data class Subject(val name: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    onSubjectClick: (String) -> Unit,
    onAiModeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val subjects = listOf(
        Subject("Mathematics", Color(0xFF6C63FF)),
        Subject("Physics", Color(0xFFFF6B9B)),
        Subject("Chemistry", Color(0xFF1BC191)),
        Subject("Biology", Color(0xFFFFA000)),
        Subject("History", Color(0xFF9163FF)),
        Subject("Geography", Color(0xFF1BC1BD)),
        Subject("English", Color(0xFFF44336)),
        Subject("Computer Science", Color(0xFF4285F4))
    )

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Anix Study Assist",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    TextButton(onClick = onAiModeClick) {
                        Text("AI MODE", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFF6200EE),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(subjects) { subject ->
                SubjectCard(
                    name = subject.name,
                    color = subject.color,
                    onClick = { onSubjectClick(subject.name) },
                    onMenuClick = { /* Handle menu click */ }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    LandingScreen(
        onSubjectClick = {},
        onAiModeClick = {},
        onSettingsClick = {},
        onAddClick = {}
    )
}
