package echo.kimmandoo.app.feature.receive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import echo.kimmandoo.app.navigation.Screen
import echo.kimmandoo.app.ui.theme.GradientBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReceivedDiaryScreen(
    navController: NavController,
    diaryId: String,
    diaryContent: String,
) {
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp

    var letterState by remember { mutableStateOf(LetterState.Folded) }

    val offsetY = remember { Animatable(-screenHeightDp.value) }
    val rotationZ = remember { Animatable(0f) }
    val rotationX = remember { Animatable(0f) }
    val offsetX = remember { Animatable(0f) }
    val letterAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 1. Letter appears
        letterAlpha.animateTo(1f, tween(500))

        // 2. Main descent animation
        val descentJob =
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 5000, easing = EaseInOutCubic),
                )
            }

        // 3. Dynamic falling leaf animation
        var spinDirection = 1
        launch {
            while (descentJob.isActive) {
                val targetRotationX = (Math.random() * 80 - 40).toFloat() // Wider tumble
//                val targetRotationZ = rotationZ.value + (Math.random() * 90 - 45).toFloat() // Sharper turns
                val targetRotationZ =
                    rotationZ.value + ((Math.random() * 45 + 45).toFloat() * spinDirection)
                spinDirection *= -1
                val targetOffsetX = (Math.random() * 400 - 200).toFloat() // Wider sway
                val duration = (1500 + Math.random() * 300).toLong() // Faster transitions

                launch {
                    rotationX.animateTo(
                        targetValue = targetRotationX,
                        animationSpec =
                            tween(
                                durationMillis = duration.toInt(),
                                easing = EaseInOutCubic,
                            ),
                    )
                }
                launch {
                    rotationZ.animateTo(
                        targetValue = targetRotationZ,
                        animationSpec =
                            tween(
                                durationMillis = duration.toInt(),
                                easing = EaseInOutCubic,
                            ),
                    )
                }
                launch {
                    offsetX.animateTo(
                        targetValue = targetOffsetX,
                        animationSpec =
                            tween(
                                durationMillis = duration.toInt(),
                                easing = EaseInOutCubic,
                            ),
                    )
                }
                delay(duration)
            }
        }

        descentJob.join()

        // 4. Settle the letter
        launch { rotationZ.animateTo(0f, tween(500, easing = EaseInOutCubic)) }
        launch { rotationX.animateTo(0f, tween(500, easing = EaseInOutCubic)) }
        launch { offsetX.animateTo(0f, tween(500, easing = EaseInOutCubic)) }
        delay(500)

        // 5. Unfold the letter
        letterState = LetterState.Unfolding
        delay(800)

        // 6. Fade in content and buttons
        contentAlpha.animateTo(1f, animationSpec = tween(500))
        buttonAlpha.animateTo(1f, animationSpec = tween(500))
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)),
    ) {
        Scaffold(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(brush = GradientBackground),
            containerColor = Color.Transparent,
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                // Animated Letter
                FlyingLetter(
                    modifier =
                        Modifier.graphicsLayer {
                            translationX = offsetX.value
                            translationY = offsetY.value
                            this.rotationZ = rotationZ.value
                            this.rotationX = rotationX.value
                            alpha = letterAlpha.value
                        },
                    state = letterState,
                    content = diaryContent,
                    contentAlpha = contentAlpha.value,
                )

                // Buttons at the bottom
                Row(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 48.dp, start = 24.dp, end = 24.dp)
                            .alpha(buttonAlpha.value),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("닫기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { navController.navigate(Screen.ReplyDiary(diaryId = diaryId)) },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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

enum class LetterState { Folded, Unfolding }

@Composable
fun FlyingLetter(
    modifier: Modifier = Modifier,
    state: LetterState,
    content: String,
    contentAlpha: Float,
) {
    Card(
        modifier =
            modifier
                .animateContentSize(
                    animationSpec =
                        tween(
                            durationMillis = 800,
                            easing = EaseInOutCubic,
                        ),
                ).then(
                    if (state == LetterState.Folded) {
                        Modifier.size(120.dp, 80.dp)
                    } else {
                        Modifier
                            .fillMaxWidth(0.9f)
                            .height(400.dp)
                    },
                ).clip(shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F5F0)), // New paper color
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp), // Increased elevation
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (state == LetterState.Folded) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "편지",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF8D6E63),
                )
            } else {
                Text(
                    modifier =
                        Modifier
                            .padding(24.dp)
                            .alpha(contentAlpha),
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF5D4037),
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReceivedDiaryScreenPreview() {
    ReceivedDiaryScreen(
        navController = rememberNavController(),
        diaryId = "1",
        diaryContent = "오늘 하루는 정말 특별했어요. 작은 새가 창가에 찾아와 노래를 불러주었답니다.",
    )
}
