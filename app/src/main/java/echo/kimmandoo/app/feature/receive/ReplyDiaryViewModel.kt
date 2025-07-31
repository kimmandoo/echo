package echo.kimmandoo.app.feature.receive

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.kimmandoo.app.data.DiaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReplyUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
)

class ReplyDiaryViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: DiaryRepository,
) : ViewModel() {
    private val originalDiaryId: String = checkNotNull(savedStateHandle["diaryId"])

    private val _uiState = MutableStateFlow(ReplyUiState())
    val uiState = _uiState.asStateFlow()

    fun sendReply(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.saveReply(originalDiaryId, content)
            if (success) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "답장 전송에 실패했습니다.") }
            }
        }
    }

    fun consumeError() {
        _uiState.update { it.copy(error = null) }
    }
}
