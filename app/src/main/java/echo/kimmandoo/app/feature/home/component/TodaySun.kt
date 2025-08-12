package echo.kimmandoo.app.feature.home.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import echo.kimmandoo.app.feature.home.component.DraggableMenuButton
import echo.kimmandoo.app.feature.home.component.TodaySun

@Composable
fun TodaySun(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.10f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(3000),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "pulseAnimation",
    )

    Box(
        modifier =
            Modifier
                .size(160.dp)
                .clickable(
                    remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush =
                    Brush.radialGradient(
                        colors =
                            listOf(
                                Color(0xFFFAF3BA).copy(alpha = 0.9f * pulse),
                                Color(0xFFFACD5D).copy(alpha = 0.5f * pulse),
                                Color.Transparent,
                            ),
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.width / 2 * pulse,
                    ),
            )
        }
    }
}
