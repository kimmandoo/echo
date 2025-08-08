package echo.kimmandoo.app.feature.receive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.kimmandoo.app.data.DiaryRepository
import echo.kimmandoo.app.data.ReceiveLetterResult
import echo.kimmandoo.app.feature.diary.model.Diary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReceiveDiaryUiState(
    val isLoading: Boolean = false,
    val isFreeChanceAvailable: Boolean = false, // 초기값 false, 로드 후 결정
    val receivedDiary: Diary? = null,
    val error: String? = null,
)

class ReceiveDiaryViewModel(
    private val repository: DiaryRepository = DiaryRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReceiveDiaryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadFreeChanceCount()
    }

    private fun loadFreeChanceCount() {
        viewModelScope.launch {
            val count = repository.getUserFreeChanceCount()
            _uiState.update { it.copy(isFreeChanceAvailable = count > 0) }
        }
    }

    fun receiveDiary(useCurrency: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.receiveLetter(useCurrency)) {
                is ReceiveLetterResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            receivedDiary = result.diary,
                            isFreeChanceAvailable = result.paidWith == "free" && (repository.getUserFreeChanceCount() > 0)
                        )
                    }
                }
                is ReceiveLetterResult.NoDiaryAvailable -> {
                    _uiState.update { it.copy(isLoading = false, error = "새로운 일기가 없어요.") }
                }
                is ReceiveLetterResult.InsufficientFunds -> {
                    val message = if (useCurrency) "재화가 부족해요." else "오늘의 무료 기회를 모두 사용했어요."
                    _uiState.update { it.copy(isLoading = false, error = message) }
                }
                is ReceiveLetterResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun consumeDiary() {
        _uiState.update { it.copy(receivedDiary = null) }
    }

    fun consumeError() {
        _uiState.update { it.copy(error = null) }
    }
}
