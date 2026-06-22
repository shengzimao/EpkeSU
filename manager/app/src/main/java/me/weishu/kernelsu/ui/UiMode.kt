package me.weishu.kernelsu.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class UiMode(val value: String) {
    Miuix("miuix"),
    Material("material");

    companion object {
        fun fromValue(value: String): UiMode = when (value) {
            Material.value -> Material
            else -> Miuix
        }

        val DEFAULT_VALUE = Miuix.value
    }
}

enum class InterfaceStyle(val value: String, val label: String) {
    Miuix(UiMode.Miuix.value, "Miuix"),
    Material(UiMode.Material.value, "Material"),
    LiquidGlass("liquid_glass", "液态玻璃"),
    Skrootpro("skrootpro", "skrootpro");

    companion object {
        fun fromIndex(index: Int): InterfaceStyle = entries.getOrElse(index) { Miuix }

        fun selectedIndex(value: String): Int = entries.indexOfFirst { it.value == value }
            .takeIf { it >= 0 } ?: entries.indexOf(Miuix)

        fun isMiuixBased(value: String): Boolean = value != Material.value
    }
}

val LocalUiMode = staticCompositionLocalOf { UiMode.Miuix }

val LocalInterfaceStyle = staticCompositionLocalOf { InterfaceStyle.Miuix.value }

val LocalSkrootproTopBarColor = staticCompositionLocalOf { Color(0xFF6A00F4) }

val LocalSkrootproTopBarContentColor = staticCompositionLocalOf { Color.White }
