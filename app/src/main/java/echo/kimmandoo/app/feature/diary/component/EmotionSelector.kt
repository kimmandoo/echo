package echo.kimmandoo.app.feature.diary.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.kimmandoo.app.R
import echo.kimmandoo.app.feature.diary.model.EmotionSticker

@Composable
fun EmotionSelector(
    stickers: List<EmotionSticker>,
    selected: EmotionSticker?,
    onSelect: (EmotionSticker) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "오늘의 감정은 어땠나요?",
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
                        painter = painterResource(id = R.drawable.splash_logo),
                        contentDescription = sticker.contentDescription,
                        tint = colorResource(id = R.color.teal_700),
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
        }
    }
}