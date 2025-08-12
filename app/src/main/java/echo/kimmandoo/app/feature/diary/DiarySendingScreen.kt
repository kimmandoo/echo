package echo.kimmandoo.app.feature.diary
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DiarySendingScreen() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(color = Color(0xFF6A5ACD))
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "오늘의 이야기를 전달하고 있어요...",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
