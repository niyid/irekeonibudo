package com.techducat.irekeonibudo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val IrekeColorScheme = darkColorScheme(
    primary = EmberGold,
    onPrimary = DeepSeaBlue,
    secondary = EmberOrange,
    background = DeepSeaBlue,
    onBackground = BoneWhite,
    surface = MidSeaTeal,
    onSurface = BoneWhite,
    error = BloodRed,
    tertiary = SpiritViolet
)

@Composable
fun IrekeOnibudoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IrekeColorScheme,
        typography = IrekeTypography,
        content = content
    )
}
