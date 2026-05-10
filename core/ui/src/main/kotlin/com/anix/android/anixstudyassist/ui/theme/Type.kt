package com.anix.android.anixstudyassist.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.anix.android.anixstudyassist.ui.common.adaptiveSp

enum class TextSizeToken(val fontSizeSp: Int) {
    S11(11),
    S13(13),
    S17(17),
    S21(21)
}

enum class TextWeightToken(val fontWeight: FontWeight) {
    Normal(FontWeight.Normal),
    SemiBold(FontWeight.SemiBold),
    Bold(FontWeight.Bold)
}

enum class AnixStudyAssistTextStyleToken(
    val size: TextSizeToken,
    val weight: TextWeightToken
) {
    BodySmall(TextSizeToken.S11, TextWeightToken.Normal),
    BodyMedium(TextSizeToken.S13, TextWeightToken.Normal),
    BodyLarge(TextSizeToken.S17, TextWeightToken.Normal),
    LabelSmall(TextSizeToken.S11, TextWeightToken.SemiBold),
    LabelMedium(TextSizeToken.S13, TextWeightToken.SemiBold),
    LabelLarge(TextSizeToken.S21, TextWeightToken.SemiBold),
    TitleSmall(TextSizeToken.S13, TextWeightToken.Bold),
    TitleMedium(TextSizeToken.S17, TextWeightToken.Bold),
    TitleLarge(TextSizeToken.S21, TextWeightToken.Bold),
    DisplaySmall(TextSizeToken.S11, TextWeightToken.Normal),
    DisplayMedium(TextSizeToken.S17, TextWeightToken.SemiBold),
    DisplayLarge(TextSizeToken.S21, TextWeightToken.Bold)
}

private val lineHeightMatrix: Map<Pair<TextSizeToken, TextWeightToken>, Int> = mapOf(
    (TextSizeToken.S11 to TextWeightToken.Normal) to 17,
    (TextSizeToken.S11 to TextWeightToken.SemiBold) to 18,
    (TextSizeToken.S11 to TextWeightToken.Bold) to 18,
    (TextSizeToken.S13 to TextWeightToken.Normal) to 20,
    (TextSizeToken.S13 to TextWeightToken.SemiBold) to 21,
    (TextSizeToken.S13 to TextWeightToken.Bold) to 21,
    (TextSizeToken.S17 to TextWeightToken.Normal) to 26,
    (TextSizeToken.S17 to TextWeightToken.SemiBold) to 27,
    (TextSizeToken.S17 to TextWeightToken.Bold) to 28,
    (TextSizeToken.S21 to TextWeightToken.Normal) to 32,
    (TextSizeToken.S21 to TextWeightToken.SemiBold) to 33,
    (TextSizeToken.S21 to TextWeightToken.Bold) to 34
)

@Composable
fun anixStudyAssistTypography(): Typography {
    return Typography(
        bodyLarge = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.BodyLarge),
        bodyMedium = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.BodyMedium),
        bodySmall = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.BodySmall),
        labelLarge = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.LabelLarge),
        labelMedium = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.LabelMedium),
        labelSmall = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.LabelSmall),
        titleLarge = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.TitleLarge),
        titleMedium = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.TitleMedium),
        titleSmall = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.TitleSmall),
        displayLarge = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.DisplayLarge),
        displayMedium = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.DisplayMedium),
        displaySmall = anixStudyAssistTextStyle(AnixStudyAssistTextStyleToken.DisplaySmall)
    )
}

@Composable
fun anixStudyAssistTextStyle(token: AnixStudyAssistTextStyleToken): TextStyle {
    val baseSize: TextUnit = token.size.fontSizeSp.sp
    val lineHeight = lineHeightFor(token.size, token.weight).sp

    return TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = token.weight.fontWeight,
        fontSize = adaptiveSp(baseSize),
        lineHeight = adaptiveSp(lineHeight),
        letterSpacing = 0.sp
    )
}

private fun lineHeightFor(size: TextSizeToken, weight: TextWeightToken): Int {
    return lineHeightMatrix[size to weight]
        ?: error("Missing line-height mapping for $size and $weight")
}
