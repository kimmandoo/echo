package echo.kimmandoo.app.feature.receive

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import echo.kimmandoo.app.navigation.Screen
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun ReceivedDiaryScreen(
    navController: NavController,
    diaryContent: String,
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val yPosition = remember { Animatable(-screenHeight.value) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(300) // Initial delay
        // Animate the boat appearing and floating down
        yPosition.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 2500, easing = EaseInOutCubic)
        )
        // Animate alpha for fade-in effect
        alpha.animateTo(1f, animationSpec = tween(durationMillis = 1500))
    }

    // Gentle rocking animation
    LaunchedEffect(yPosition.isRunning) {
        if (!yPosition.isRunning) {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    Scaffold(
        containerColor = Color.Transparent, // Make scaffold transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFB3E5FC), Color(0xFF81D4FA), Color(0xFF4FC3F7))
                    )
                )
                .padding(paddingValues)
        ) {
            // Floating Paper Boat
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = yPosition.value.dp)
                    .alpha(alpha.value)
            ) {
                PaperBoat(diaryContent = diaryContent)
            }

            // Buttons at the bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
                    .alpha(alpha.value) // Fade in with the boat
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("닫기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { navController.navigate(Screen.ReplyDiary) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "답장하기")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("답장하기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PaperBoat(diaryContent: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(300.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val path = Path().apply {
                moveTo(width * 0.05f, height * 0.6f)
                lineTo(width * 0.95f, height * 0.6f)
                lineTo(width * 0.75f, height * 0.9f)
                lineTo(width * 0.25f, height * 0.9f)
                close()

                moveTo(width * 0.5f, height * 0.05f)
                lineTo(width * 0.1f, height * 0.6f)
                lineTo(width * 0.9f, height * 0.6f)
                close()
            }
            drawPath(path, color = Color.White,)
        }
        Text(
            modifier = Modifier.padding(bottom = 40.dp, start = 32.dp, end = 32.dp),
            text = diaryContent,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReceivedDiaryScreenPreview() {
    ReceivedDiaryScreen(navController = rememberNavController(), diaryContent = "오늘 하루는 정말 특별했어요. 작은 새가 창가에 찾아와 노래를 불러주었답니다.")
}
