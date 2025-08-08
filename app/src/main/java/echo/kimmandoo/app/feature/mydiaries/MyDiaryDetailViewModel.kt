package echo.kimmandoo.app.feature.mydiaries

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.kimmandoo.app.data.DiaryRepository
import echo.kimmandoo.app.feature.diary.model.Reply
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyDiaryDetailUiState(
    val isLoading: Boolean = true,
    val replies: List<Reply> = emptyList(),
    val error: String? = null,
)

class MyDiaryDetailViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val diaryId: String = savedStateHandle.get<String>("diaryId") ?: ""
    private val repository = DiaryRepository()

    private val _uiState = MutableStateFlow(MyDiaryDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadReplies()
    }

    private fun loadReplies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val replies = repository.getRepliesForDiary(diaryId)
            _uiState.update { it.copy(isLoading = false, replies = replies) }
        }
    }
}
