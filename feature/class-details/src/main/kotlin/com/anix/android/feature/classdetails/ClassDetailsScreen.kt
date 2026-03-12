package com.anix.android.anixstudyassist.feature.classdetails

import androidx.compose.runtime.Composable
import com.anix.android.anixstudyassist.core.nav.ClassDetailsScreenNavigations
import com.anix.android.anixstudyassist.feature.classdetails.ui.ClassDetailsScreen as ClassDetailsScreenContent

@Composable
fun ClassDetailsScreen(
    classId: String,
    navigations: ClassDetailsScreenNavigations
) {
    ClassDetailsScreenContent(
        subjectName = classId.toReadableSubjectName(),
        onBackClick = navigations.onBack,
        onMenuClick = { navigations.onOpenSettings(classId) }
    )
}

private fun String.toReadableSubjectName(): String {
    return split('-', '_')
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            part.lowercase().replaceFirstChar { it.uppercase() }
        }
        .ifBlank { this }
}
