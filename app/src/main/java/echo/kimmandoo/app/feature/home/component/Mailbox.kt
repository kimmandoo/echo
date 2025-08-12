package echo.kimmandoo.app.feature.home.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun Mailbox(
    modifier: Modifier = Modifier,
    isHovering: Boolean,
    isJustDropped: Boolean
) {
    val isOpen = isHovering || isJustDropped

    val lidAngle by animateFloatAsState(
        targetValue = if (isOpen) 75f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "lidAngle"
    )

    Box(
        modifier = modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        // The combined mailbox structure
        Box(modifier = Modifier.width(120.dp).height(90.dp)) {
            // Lid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 1f) // Hinge at the bottom of the lid
                        rotationX = lidAngle
                    }
                    .background(
                        Color(0xFFD32F2F),
                        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                    )
            ) {
                // Slot visual on the front of the lid
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(60.dp)
                        .height(6.dp)
                        .background(Color(0xFFB71C1C), shape = CircleShape)
                )
            }
            // Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Color(0xFFC62828),
                        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                    )
            )
        }
    }
}