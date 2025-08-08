package echo.kimmandoo.app.feature.receive

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import echo.kimmandoo.app.navigation.Screen
import echo.kimmandoo.app.ui.theme.GradientBackground

@Composable
fun ReceiveDiaryScreen(navController: NavController) {
    val viewModel: ReceiveDiaryViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .background(
                    brush = GradientBackground,
                ).padding(it),
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
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(48.dp))

                // Action Buttons
                AnimatedVisibility(
                    visible = !uiState.isLoading,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedVisibility(visible = uiState.isFreeChanceAvailable) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Button(onClick = { viewModel.receiveDiary(useCurrency = false) }) {
                                    Text("오늘의 무료 기회 사용하기")
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        Button(onClick = { viewModel.receiveDiary(useCurrency = true) }) {
                            Text("재화 사용하기")
                        }
                    }
                }

                // Loading Indicator
                AnimatedVisibility(
                    visible = uiState.isLoading,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF8D6E63))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "일기를 찾고 있어요...",
                            color = Color(0xFF5D4037),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }

    // 에러 메시지 표시
    uiState.error?.let {
        LaunchedEffect(it) {
            snackbarHostState.showSnackbar(message = it)
            viewModel.consumeError()
        }
    }

    // 일기 받기 성공 시 화면 이동
    uiState.receivedDiary?.let { diary ->
        LaunchedEffect(diary) {
            navController.navigate(Screen.ReceivedDiary(diaryId = diary.id, diaryContent = diary.content)) {
                popUpTo(Screen.ReceiveDiary) { inclusive = true }
            }
            viewModel.consumeDiary()
        }
    }
}