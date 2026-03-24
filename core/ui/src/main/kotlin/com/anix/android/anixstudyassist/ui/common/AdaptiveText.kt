package com.anix.android.anixstudyassist.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.anix.android.anixstudyassist.ui.theme.AnixStudyAssistTextStyleToken
import com.anix.android.anixstudyassist.ui.theme.anixStudyAssistTextStyle

enum class TextColorToken {
    Black,
    Grey
}

@Composable
fun AnixStudyAssistText(
    text: String,
    modifier: Modifier = Modifier,
    styleToken: AnixStudyAssistTextStyleToken = AnixStudyAssistTextStyleToken.BodyMedium,
    colorToken: TextColorToken = TextColorToken.Black,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null
) {
    Text(
        text = text,
        modifier = modifier,
        style = anixStudyAssistTextStyle(styleToken),
        color = anixStudyAssistTextColor(colorToken),
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign
    )
}

@Composable
fun AnixStudyAssistTitleText(
    text: String,
    modifier: Modifier = Modifier,
    colorToken: TextColorToken = TextColorToken.Black
) {
    AnixStudyAssistText(
        text = text,
        modifier = modifier,
        styleToken = AnixStudyAssistTextStyleToken.TitleLarge,
        colorToken = colorToken
    )
}

@Composable
fun AnixStudyAssistBodyText(
    text: String,
    modifier: Modifier = Modifier,
    colorToken: TextColorToken = TextColorToken.Grey
) {
    AnixStudyAssistText(
        text = text,
        modifier = modifier,
        styleToken = AnixStudyAssistTextStyleToken.BodyMedium,
        colorToken = colorToken
    )
}

@Composable
fun anixStudyAssistTextColor(token: TextColorToken): Color {
    return when (token) {
        TextColorToken.Black -> MaterialTheme.colorScheme.onSurface
        TextColorToken.Grey -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}
