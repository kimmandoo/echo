package echo.kimmandoo.app.feature.receive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("다른 사람의 일기를 받아보시겠어요?", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(32.dp))
                    AnimatedVisibility(
                        visible = uiState.isFreeChanceAvailable,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(500)),
                    ) {
                        Button(onClick = { viewModel.receiveDiary(false) }) {
                            Text("오늘의 무료 기회 사용하기")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.receiveDiary(true) }) {
                        Text("재화 사용하기")
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
