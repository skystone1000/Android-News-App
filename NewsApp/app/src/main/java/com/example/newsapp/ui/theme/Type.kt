package com.example.newsapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.newsapp.R

// Brief design system uses two variable Grotesk families (bundled in res/font):
//   Schibsted Grotesk — display / headline / title  (weights 600/700/800)
//   Hanken Grotesk     — body / label / overline      (weights 400/500/600/700)
// Variable fonts are loaded per weight via FontVariation (Compose 1.7+).

@OptIn(ExperimentalTextApi::class)
private fun schibsted(weight: Int) = Font(
    R.font.schibsted_grotesk_variable,
    weight = FontWeight(weight),
    style = FontStyle.Normal,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

@OptIn(ExperimentalTextApi::class)
private fun hanken(weight: Int) = Font(
    R.font.hanken_grotesk_variable,
    weight = FontWeight(weight),
    style = FontStyle.Normal,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val Schibsted = FontFamily(
    schibsted(500), schibsted(600), schibsted(700), schibsted(800),
)

val Hanken = FontFamily(
    hanken(400), hanken(500), hanken(600), hanken(700),
)

// Brief type scale (see docs/UI_REFACTOR_PLAN.md §1c).
// Mapping to Material3 slots:
//   Display  -> displaySmall    Headline -> headlineMedium   Title -> titleMedium
//   Body     -> bodyMedium      Label    -> labelLarge       Overline -> labelSmall
val BriefTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = Schibsted,
        fontWeight = FontWeight(800),
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.035).em,
    ),
    headlineMedium = TextStyle(
        fontFamily = Schibsted,
        fontWeight = FontWeight(700),
        fontSize = 22.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.025).em,
    ),
    titleMedium = TextStyle(
        fontFamily = Schibsted,
        fontWeight = FontWeight(600),
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.015).em,
    ),
    bodyMedium = TextStyle(
        fontFamily = Hanken,
        fontWeight = FontWeight(400),
        fontSize = 14.sp,
        lineHeight = 22.4.sp, // 1.6
    ),
    labelLarge = TextStyle(
        fontFamily = Hanken,
        fontWeight = FontWeight(600),
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Hanken,
        fontWeight = FontWeight(700),
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.09.em,
    ),
)
