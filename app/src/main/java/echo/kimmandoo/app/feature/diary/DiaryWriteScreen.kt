package echo.kimmandoo.app.feature.diary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryWriteScreen(
    uiState: DiaryUiState,
    onTextChange: (String) -> Unit,
    onWeatherSelect: (WeatherSticker) -> Unit,
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
                        enabled = uiState.diaryText.isNotBlank() && uiState.selectedWeather != null,
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

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter =
                    fadeIn(animationSpec = tween(500, delayMillis = 400)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(500, delayMillis = 400),
                        ),
            ) {
                WeatherSelector(
                    stickers = uiState.weatherStickers,
                    selected = uiState.selectedWeather,
                    onSelect = onWeatherSelect,
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

@Composable
private fun WeatherSelector(
    stickers: List<WeatherSticker>,
    selected: WeatherSticker?,
    onSelect: (WeatherSticker) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "오늘의 날씨는 어땠나요?",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(stickers) { sticker ->
                val isSelected = sticker == selected
                val borderColor =
                    if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray

                Box(
                    modifier =
                        Modifier
                            .padding(horizontal = 12.dp)
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(2.dp, borderColor, CircleShape)
                            .clickable { onSelect(sticker) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = sticker.icon,
                        contentDescription = sticker.contentDescription,
                        tint = sticker.color,
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
            textStyle =
                MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 24.sp,
                ),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        "오늘 하루, 어떤 이야기를 담고 있나요?",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    )
                }
                innerTextField()
            },
        )
        Text(
            text = "${value.length} / 300",
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            fontSize = 12.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiaryWriteScreenPreview() {
    MaterialTheme {
        DiaryWriteScreen(
            uiState = DiaryUiState(),
            onTextChange = {},
            onWeatherSelect = {},
            onSendClick = {},
            onBackClick = {},
        )
    }
}
