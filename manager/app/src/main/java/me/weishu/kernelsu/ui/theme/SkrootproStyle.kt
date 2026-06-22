package me.weishu.kernelsu.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import me.weishu.kernelsu.ui.InterfaceStyle
import me.weishu.kernelsu.ui.LocalInterfaceStyle
import me.weishu.kernelsu.ui.LocalSkrootproTopBarColor
import me.weishu.kernelsu.ui.LocalSkrootproTopBarContentColor

data class TopBarColors(
    val container: Color,
    val content: Color,
)

@Composable
fun skrootproTopBarColors(defaultContainer: Color, defaultContent: Color): TopBarColors {
    return if (LocalInterfaceStyle.current == InterfaceStyle.Skrootpro.value) {
        TopBarColors(
            container = LocalSkrootproTopBarColor.current,
            content = LocalSkrootproTopBarContentColor.current,
        )
    } else {
        TopBarColors(container = defaultContainer, content = defaultContent)
    }
}
