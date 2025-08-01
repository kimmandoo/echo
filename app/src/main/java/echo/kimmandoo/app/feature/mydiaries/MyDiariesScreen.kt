package echo.kimmandoo.app.feature.mydiaries

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class DiaryType { SENT, RECEIVED }
data class DiaryEntry(val date: String, val content: String, val type: DiaryType)

@Composable
fun MyDiariesScreen() {
    val diaries = listOf(
        DiaryEntry("2024년 7월 28일", "오늘은 햇살이 정말 좋았다. 공원에서 책을 읽으며 시간을 보냈다.", DiaryType.SENT),
        DiaryEntry("2024년 7월 27일", "당신의 일기를 읽고 저도 공원에 가보고 싶어졌어요.", DiaryType.RECEIVED),
        DiaryEntry("2024년 7월 25일", "오랜만에 친구를 만나 즐거운 수다를 떨었다. 역시 친구가 최고다!", DiaryType.SENT),
        DiaryEntry("2024년 7월 22일", "새로운 프로젝트를 시작했다. 조금은 막막하지만, 설레는 마음이 더 크다.", DiaryType.SENT),
        DiaryEntry("2024년 7월 21일", "새로운 시작을 응원합니다! 분명 잘 해내실 거예요.", DiaryType.RECEIVED),
        DiaryEntry("2024년 7월 19일", "비가 오는 날, 창밖을 보며 마시는 커피는 언제나 옳다.", DiaryType.SENT)
    )

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
                    )
                )
                .padding(it)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "추억의 서랍",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4E342E),
                    modifier = Modifier.padding(top = 48.dp, bottom = 24.dp)
                )
                StackedLetters(diaries = diaries)
            }
        }
    }
}

@Composable
fun StackedLetters(diaries: List<DiaryEntry>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy((-80).dp) // Overlap cards to create a stack
    ) {
        itemsIndexed(diaries) { index, diary ->
            val rotation = remember { (Math.random() * 1.5 - 0.75).toFloat() } // -0.75 to 0.75 degrees
            val offsetX = remember { (Math.random() * 6 - 3).toFloat() }    // -3 to 3 dp

            LetterCard(
                diary = diary,
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = rotation
                        translationX = offsetX
                    }
                    .padding(bottom = 120.dp) // Space for the next card to overlap
            )
        }
    }
}

@Composable
fun LetterCard(diary: DiaryEntry, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { /* TODO: Navigate to detail */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Colored tab to indicate diary type
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight()
                    .background(
                        color = if (diary.type == DiaryType.SENT) Color(0xFFFFD54F) else Color(0xFF81D4FA),
                        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                    )
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = diary.date,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF5D4037).copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = diary.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF5D4037),
                    maxLines = 3
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyDiariesScreenPreview() {
    MyDiariesScreen()
}
