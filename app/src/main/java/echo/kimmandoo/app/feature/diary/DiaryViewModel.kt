package echo.kimmandoo.app.feature.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.kimmandoo.app.data.DiaryRepository
import echo.kimmandoo.app.feature.diary.EmotionSticker
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

data class DiaryUiState(
    val status: DiaryStatus = DiaryStatus.WRITING,
    val diaryText: String = "",
    val selectedEmotion: EmotionSticker? = null,
    val emotionStickers: List<EmotionSticker> = emptyList(),
)

class DiaryViewModel(
    private val repository: DiaryRepository = DiaryRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()

    init {
        loadEmotionStickers()
    }

    private fun loadEmotionStickers() {
        _uiState.update { it.copy(emotionStickers = EmotionSticker.entries.toList()) }
    }

    fun onTextChange(text: String) {
        if (text.length <= 300) {
            _uiState.update { it.copy(diaryText = text) }
        }
    }

    fun onEmotionSelect(emotion: EmotionSticker) {
        _uiState.update { it.copy(selectedEmotion = emotion) }
    }

    fun sendDiary() {
        val currentState = _uiState.value
        if (currentState.diaryText.isNotBlank() && currentState.selectedEmotion != null) {
            viewModelScope.launch {
                _uiState.update { it.copy(status = DiaryStatus.SENDING) }
                val success = repository.saveDiary(currentState.diaryText, currentState.selectedEmotion.name)
                if (success) {
                    _uiState.update { it.copy(status = DiaryStatus.COMPLETED) }
                } else {
                    _uiState.update { it.copy(status = DiaryStatus.WRITING) }
                }
            }
        }
    }
}