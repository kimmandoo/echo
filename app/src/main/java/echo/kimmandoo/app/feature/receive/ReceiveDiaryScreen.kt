package echo.kimmandoo.app.feature.receive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import echo.kimmandoo.app.navigation.Screen

@Composable
fun ReceiveDiaryScreen(navController: NavController) {
    val viewModel: ReceiveDiaryViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
                    )
                )
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    "다른 사람의 일기를 받아보시겠어요?",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF4E342E),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(48.dp))

                // Action Buttons
                AnimatedVisibility(
                    visible = !uiState.isLoading,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedVisibility(visible = uiState.isFreeChanceAvailable) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Button(onClick = { viewModel.receiveDiary(false) }) {
                                    Text("오늘의 무료 기회 사용하기")
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        Button(onClick = { viewModel.receiveDiary(true) }) {
                            Text("재화 사용하기")
                        }
                    }
                }

                // Loading Indicator
                AnimatedVisibility(
                    visible = uiState.isLoading,
                    enter = fadeIn(animationSpec = tween(300, delayMillis = 300)),
                    exit = fadeOut(animationSpec = tween(300)),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF8D6E63))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "일기를 찾고 있어요...",
                            color = Color(0xFF5D4037),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    uiState.receivedDiary?.let {
        navController.navigate(Screen.ReceivedDiary(it)) {
            popUpTo(Screen.ReceiveDiary) { inclusive = true }
        }
    }
}

