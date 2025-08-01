package echo.kimmandoo.app.feature.diary

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class DiaryStatus {
    WRITING,
    SENDING,
    COMPLETED,
}

data class WeatherSticker(
    val icon: ImageVector,
    val contentDescription: String,
    val color: Color,
)

data class DiaryUiState(
    val status: DiaryStatus = DiaryStatus.WRITING,
    val diaryText: String = "",
    val selectedWeather: WeatherSticker? = null,
    val weatherStickers: List<WeatherSticker> = emptyList(),
)

class DiaryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()

    init {
        loadWeatherStickers()
    }

    private fun loadWeatherStickers() {
        val stickers =
            listOf(
                WeatherSticker(Icons.Default.Face, "맑음", Color(0xFFFFB74D)),
                WeatherSticker(Icons.Default.FavoriteBorder, "흐림", Color(0xFF90A4AE)),
                WeatherSticker(Icons.Default.Favorite, "사랑", Color(0xFFE57373)),
                WeatherSticker(Icons.Default.Notifications, "좋음", Color(0xFF81C784)),
            )
        _uiState.update { it.copy(weatherStickers = stickers) }
    }

    fun onTextChange(text: String) {
        if (text.length <= 300) {
            _uiState.update { it.copy(diaryText = text) }
        }
    }

    fun onWeatherSelect(weather: WeatherSticker) {
        _uiState.update { it.copy(selectedWeather = weather) }
    }

    fun sendDiary() {
        if (_uiState.value.diaryText.isNotBlank() && _uiState.value.selectedWeather != null) {
            viewModelScope.launch {
                _uiState.update { it.copy(status = DiaryStatus.SENDING) }
                delay(2000) // 서버에 전송하고 완료 받아오면
                _uiState.update { it.copy(status = DiaryStatus.COMPLETED) }
            }
        }
    }
}
