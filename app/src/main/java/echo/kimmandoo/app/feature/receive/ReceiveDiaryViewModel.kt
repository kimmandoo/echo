package echo.kimmandoo.app.feature.receive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReceiveDiaryUiState(
    val isLoading: Boolean = false,
    val isFreeChanceAvailable: Boolean = true, // 초기값은 true로 설정
    val receivedDiary: String? = null,
    val error: String? = null,
)

class ReceiveDiaryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReceiveDiaryUiState())
    val uiState = _uiState.asStateFlow()

    fun receiveDiary(useCoin: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // TODO: 실제로는 서버와 통신하여 일기를 받아와야 합니다.
                delay(2000) // 2초 딜레이로 시뮬레이션
                val diary = "오늘 날씨는 정말 좋았다. 하늘은 맑고, 바람은 시원했다. 이런 날에는 어디론가 훌쩍 떠나고 싶다."
                _uiState.update {
                    it.copy(
                        receivedDiary = diary,
                        isFreeChanceAvailable = if (!useCoin) false else it.isFreeChanceAvailable,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "일기를 받아오는데 실패했습니다.") }
            }
        }
    }

    fun consumeError() {
        _uiState.update { it.copy(error = null) }
    }
}
