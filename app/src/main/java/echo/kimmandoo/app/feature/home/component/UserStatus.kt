package echo.kimmandoo.app.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import echo.kimmandoo.app.feature.home.component.DraggableMenuButton
import echo.kimmandoo.app.feature.home.component.TodaySun

@Composable
fun UserStatus(
    starCoins: Int,
    hasFreeExchange: Boolean,
) {
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.05f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        val textColor = Color(0xFF5D4037)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Star,
                contentDescription = "별코인",
                tint = Color(0xFFFFA000),
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "$starCoins 개", color = textColor, fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (hasFreeExchange) Icons.Default.Check else Icons.Default.Star,
                contentDescription = "무료 교환",
                tint = if (hasFreeExchange) Color(0xFF4CAF50) else textColor.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (hasFreeExchange) "무료 교환 가능" else "교환 완료",
                color = textColor,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
