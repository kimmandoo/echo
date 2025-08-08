package echo.kimmandoo.app.feature.diary

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class EmotionSticker(
    val icon: ImageVector,
    val color: Color,
    val contentDescription: String,
) {
    JOYFUL(
        icon = Icons.Default.Star,
        color = Color.Yellow,
        contentDescription = "기쁨",
    ),
    SAD(
        icon = Icons.Default.Star,
        color = Color.Yellow,
        contentDescription = "슬픔",
    ),
    ANGRY(
        icon = Icons.Default.Star,
        color = Color.Yellow,
        contentDescription = "화남",
    ),
    CALM(
        icon = Icons.Default.Star,
        color = Color.Yellow,
        contentDescription = "평온",
    ),
    LOVELY(
        icon = Icons.Default.Star,
        color = Color.Yellow,
        contentDescription = "사랑",
    ),
    ;

    companion object {
        fun fromString(name: String): EmotionSticker? = entries.find { it.name.equals(name, ignoreCase = true) }
    }
}

fun getEmotionStickerFromString(emotion: String): EmotionSticker = EmotionSticker.fromString(emotion) ?: EmotionSticker.JOYFUL
