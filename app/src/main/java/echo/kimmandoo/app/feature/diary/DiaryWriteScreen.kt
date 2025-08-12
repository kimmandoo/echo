package echo.kimmandoo.app.feature.diary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.kimmandoo.app.feature.diary.component.DiaryTextField
import echo.kimmandoo.app.feature.diary.component.EmotionSelector
import echo.kimmandoo.app.feature.diary.component.TopicCard
import echo.kimmandoo.app.feature.diary.component.TopicProvider
import echo.kimmandoo.app.feature.diary.model.EmotionSticker
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryWriteScreen(
    uiState: DiaryUiState,
    onTextChange: (String) -> Unit,
    onEmotionSelect: (EmotionSticker) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("오늘의 일기", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSendClick,
                        enabled = uiState.diaryText.isNotBlank() && uiState.selectedEmotion != null,
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "보내기",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter =
                    fadeIn(animationSpec = tween(500, delayMillis = 200)) +
                        slideInVertically(
                            animationSpec = tween(500, delayMillis = 200),
                        ),
            ) {
                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.getDefault())
                Text(
                    text = currentDate.format(formatter),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 오늘의 주제 섹션
            AnimatedVisibility(
                visible = isVisible,
                enter =
                    fadeIn(animationSpec = tween(500, delayMillis = 300)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(500, delayMillis = 300),
                        ),
            ) {
                TopicCard(topic = TopicProvider.getTodayTopic())
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter =
                    fadeIn(animationSpec = tween(500, delayMillis = 400)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(500, delayMillis = 400),
                        ),
            ) {
                EmotionSelector(
                    stickers = uiState.emotionStickers,
                    selected = uiState.selectedEmotion,
                    onSelect = onEmotionSelect,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = isVisible,
                modifier = Modifier.weight(1f),
                enter =
                    fadeIn(animationSpec = tween(500, delayMillis = 600)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(500, delayMillis = 600),
                        ),
            ) {
                DiaryTextField(
                    value = uiState.diaryText,
                    onValueChange = onTextChange,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryWriteScreenPreview() {
    MaterialTheme {
        DiaryWriteScreen(
            uiState = DiaryUiState(),
            onTextChange = {},
            onEmotionSelect = {},
            onSendClick = {},
            onBackClick = {},
        )
    }
}