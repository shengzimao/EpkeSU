package me.weishu.kernelsu.ui.component.skrootpro

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.weishu.kernelsu.R

object SkrootproColors {
    val Purple = Color(0xFF7000F5)
    val PurpleDark = Color(0xFF3A00B8)
    val MagentaLine = Color(0xFFE000E8)
    val Text = Color(0xFF1E1E1E)
    val Muted = Color(0xFF7B7B7B)
    val Faint = Color(0xFFD9D9D9)
    val Success = Color(0xFF45A857)
    val Disabled = Color(0xFFE2E2E2)
    val DisabledText = Color(0xFF8B8B8B)
    val BarSurface = Color(0xFFF5F5F6)
}

@Composable
fun skrootproSp(value: Float, maxScale: Float = 1.12f): TextUnit {
    val fontScale = LocalDensity.current.fontScale.coerceAtLeast(0.85f)
    val cappedScale = fontScale.coerceAtMost(maxScale)
    return (value * cappedScale / fontScale).sp
}

@Composable
fun SkrootproScreen(
    title: String,
    showAdd: Boolean = false,
    onAddClick: () -> Unit = {},
    bottomInnerPadding: Dp,
    content: @Composable (PaddingValues) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        SkrootproTopBar(
            title = title,
            showAdd = showAdd,
            onAddClick = onAddClick,
        )
        Box(modifier = Modifier.weight(1f)) {
            content(PaddingValues(bottom = bottomInnerPadding + 10.dp))
        }
    }
}

@Composable
fun SkrootproTopBar(
    title: String,
    showAdd: Boolean = false,
    onAddClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SkrootproColors.PurpleDark)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(SkrootproColors.Purple)
                .padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = skrootproSp(20f, maxScale = 1.0f),
                lineHeight = skrootproSp(24f, maxScale = 1.0f),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (showAdd) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onAddClick),
                )
            }
        }
    }
}

@Composable
fun SkrootproBottomBar(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp)
            .padding(bottom = navBottom + 7.dp)
            .height(42.dp)
            .shadow(4.dp, CircleShape)
            .background(SkrootproColors.BarSurface, CircleShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkrootproNavDestination.entries.forEachIndexed { index, destination ->
            SkrootproNavItem(
                destination = destination,
                selected = selectedIndex == index,
                onClick = { onSelected(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SkrootproNavItem(
    destination: SkrootproNavDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(34.dp)
            .background(
                color = if (selected) SkrootproColors.Purple else Color.Transparent,
                shape = CircleShape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(destination.label),
            color = if (selected) Color.White else Color(0xFF5B5B5B),
            fontSize = skrootproSp(14.5f, maxScale = 1.0f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

enum class SkrootproNavDestination(
    @StringRes val label: Int,
    val icon: ImageVector,
) {
    Home(R.string.home, Icons.Rounded.Home),
    SuperUser(R.string.skrootpro_nav_superuser, Icons.Rounded.Security),
    Module(R.string.module, Icons.Rounded.Extension),
    Settings(R.string.settings, Icons.Rounded.Settings),
}

@Composable
fun SkrootproButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .shadow(if (enabled) 2.dp else 0.dp, RoundedCornerShape(7.dp))
            .background(
                color = if (enabled) SkrootproColors.Purple else SkrootproColors.Disabled,
                shape = RoundedCornerShape(7.dp),
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) Color.White else SkrootproColors.DisabledText,
            fontSize = skrootproSp(12.5f, maxScale = 1.0f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun SkrootproSectionTitle(text: String) {
    Text(
        text = text,
        color = SkrootproColors.Text,
        fontSize = skrootproSp(13f, maxScale = 1.0f),
        lineHeight = skrootproSp(16f, maxScale = 1.0f),
        modifier = Modifier.padding(vertical = 2.dp),
    )
}

@Composable
fun SkrootproDivider(
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
) {
    Box(
        modifier = if (vertical) {
            modifier
                .width(2.dp)
                .fillMaxSize()
                .background(SkrootproColors.MagentaLine)
        } else {
            modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(SkrootproColors.MagentaLine)
        }
    )
}

@Composable
fun SkrootproEmptyText(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = SkrootproColors.Faint,
            fontSize = skrootproSp(15f, maxScale = 1.0f),
            textAlign = TextAlign.Center,
        )
    }
}
