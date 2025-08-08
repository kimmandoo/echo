package echo.kimmandoo.app.feature.mydiaries

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import echo.kimmandoo.app.feature.diary.model.Diary
import echo.kimmandoo.app.navigation.Screen
import echo.kimmandoo.app.ui.theme.GradientBackground
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MyDiariesScreen(
    navController: NavController,
    viewModel: MyDiariesViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(containerColor = Color.Transparent) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(GradientBackground)
                    .padding(it),
        ) {
            val yearMonth =
                remember(uiState.selectedYear, uiState.selectedMonth) {
                    YearMonth.of(uiState.selectedYear, uiState.selectedMonth)
                }

            CalendarHeader(
                yearMonth = yearMonth,
                onPrevMonth = { viewModel.onMonthChange(yearMonth.minusMonths(1)) },
                onNextMonth = { viewModel.onMonthChange(yearMonth.plusMonths(1)) },
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                CalendarGrid(yearMonth, uiState.diaries, navController)
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    yearMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        IconButton(onClick = onPrevMonth) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "이전 달",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        AnimatedContent(targetState = yearMonth, label = "month-year") {
            Text(
                text = "${it.year}년 ${it.month.getDisplayName(TextStyle.FULL, Locale.KOREAN)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        IconButton(onClick = onNextMonth) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "다음 달",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    diaries: List<Diary>,
    navController: NavController,
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7 // Sunday = 0
    val emptyDays = (0 until firstDayOfWeek).toList()

    val diariesByDate =
        remember(diaries) {
            diaries.associateBy {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    LocalDate
                        .ofInstant(
                            it.timestamp!!.toInstant(),
                            java.time.ZoneId.systemDefault(),
                        ).dayOfMonth
                } else {
                    SimpleDateFormat("dd", Locale.getDefault()).format(it.timestamp).toInt()
                }
            }
        }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        DaysOfWeekHeader()
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(emptyDays) { /* Empty cells for padding */ }

            items((1..daysInMonth).toList()) { day ->
                val diary = diariesByDate[day]
                DayCell(
                    day = day,
                    diary = diary,
                    yearMonth = yearMonth,
                    navController = navController,
                )
            }
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val days = listOf("일", "월", "화", "수", "목", "금", "토")
        days.forEach {
            Text(
                text = it,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    diary: Diary?,
    yearMonth: YearMonth,
    navController: NavController,
) {
    val isToday =
        LocalDate
            .now()
            .let { it.dayOfMonth == day && it.monthValue == yearMonth.monthValue && it.year == yearMonth.year }
    val status = diary?.status?.let { DiaryStatus.fromString(it) }

    Box(
        modifier =
            Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isToday) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    } else {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                    },
                ).border(
                    width = if (isToday) 1.5.dp else 0.dp,
                    color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                ).clickable(enabled = diary != null) {
                    diary?.let {
                        navController.navigate(
                            Screen.MyDiaryDetail(
                                it.id,
                            ),
                        )
                    }
                },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (diary != null) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (status != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = status.icon,
                    contentDescription = status.description,
                    tint = status.color,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

sealed class DiaryStatus(
    val icon: ImageVector,
    val color: Color,
    val description: String,
) {
    object Private : DiaryStatus(Icons.Default.Lock, Color.Gray, "개인 보관")

    object Pending : DiaryStatus(Icons.Default.Pending, Color.Yellow, "답장 대기중")

    object Replied : DiaryStatus(Icons.Default.Reply, Color.Blue, "답장 도착")

    companion object {
        fun fromString(status: String): DiaryStatus? =
            when (status.uppercase()) {
                "PRIVATE" -> Private
                "PENDING" -> Pending
                "REPLIED" -> Replied
                else -> null
            }
    }
}
