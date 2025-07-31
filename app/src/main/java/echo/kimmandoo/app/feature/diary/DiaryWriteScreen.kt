package echo.kimmandoo.app.feature.diary

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class WeatherSticker(
    val icon: ImageVector,
    val contentDescription: String,
    val color: Color
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DiaryWriteScreen() {
    var diaryText by remember { mutableStateOf("") }
    val weatherStickers = remember {
        listOf(
            WeatherSticker(Icons.Default.Favorite, "맑음", Color(0xFFFFD700)),
            WeatherSticker(Icons.Default.Face, "흐림", Color(0xFFB0C4DE)),
        )
    }
    var selectedWeather by remember { mutableStateOf<WeatherSticker?>(null) }

    DiaryWriteScreenContent(
        diaryText = diaryText,
        onTextChange = { if (it.length <= 300) diaryText = it },
        weatherStickers = weatherStickers,
        selectedWeather = selectedWeather,
        onWeatherSelect = { selectedWeather = it },
        onSendClick = { /* TODO: 일기 전송 로직 */ }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryWriteScreenContent(
    diaryText: String,
    onTextChange: (String) -> Unit,
    weatherStickers: List<WeatherSticker>,
    selectedWeather: WeatherSticker?,
    onWeatherSelect: (WeatherSticker) -> Unit,
    onSendClick: () -> Unit
) {
    // 애니메이션을 위한 가시성 상태
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("오늘의 일기", fontWeight = FontWeight.Bold, color = Color.DarkGray) },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: 뒤로가기 */ }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color.Gray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSendClick) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "보내기",
                            tint = Color(0xFF6A5ACD)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF8F8F8) // 부드러운 배경색
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 날짜 표시
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 200)) + slideInVertically(
                    animationSpec = tween(500, delayMillis = 200)
                )
            ) {
                DateHeader()
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 날씨 스티커 선택
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(500, delayMillis = 400)
                )
            ) {
                WeatherSelector(
                    stickers = weatherStickers,
                    selected = selectedWeather,
                    onSelect = onWeatherSelect
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 일기 입력 필드
            AnimatedVisibility(
                visible = isVisible,
                modifier = Modifier.weight(1f),
                enter = fadeIn(animationSpec = tween(500, delayMillis = 600)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(500, delayMillis = 600)
                )
            ) {
                DiaryTextField(
                    value = diaryText,
                    onValueChange = onTextChange
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateHeader() {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.KOREAN)
    Text(
        text = currentDate.format(formatter),
        fontSize = 18.sp,
        color = Color.Gray
    )
}

@Composable
fun WeatherSelector(
    stickers: List<WeatherSticker>,
    selected: WeatherSticker?,
    onSelect: (WeatherSticker) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "오늘의 날씨는 어땠나요?",
            fontSize = 16.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(stickers) { sticker ->
                val isSelected = sticker == selected
                val borderColor = if (isSelected) Color(0xFF6A5ACD) else Color.LightGray

                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, borderColor, CircleShape)
                        .clickable { onSelect(sticker) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = sticker.icon,
                        contentDescription = sticker.contentDescription,
                        tint = sticker.color,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DiaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp,
                color = Color.DarkGray
            ),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text("오늘 하루, 어떤 이야기를 담고 있나요?", color = Color.LightGray)
                }
                innerTextField()
            }
        )
        Text(
            text = "${value.length} / 300",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp),
            color = Color.LightGray,
            fontSize = 12.sp
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DiaryWriteScreenPreview() {
    MaterialTheme {
        DiaryWriteScreen()
    }
}
