package echo.kimmandoo.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.kimmandoo.app.data.UserData
import echo.kimmandoo.app.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val userData: UserData = UserData(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

class HomeViewModel(
    private val userRepository: UserRepository = UserRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        giveDailyChanceAndListenData()
    }

    private fun giveDailyChanceAndListenData() {
        viewModelScope.launch {
            // 1. 매일 첫 방문 시 무료 기회 지급
            userRepository.addDailyFreeChanceIfNeeded()

            // 2. 사용자 데이터 실시간 수신
            userRepository.listenToUserData()
                .onEach { userData ->
                    _uiState.update { it.copy(userData = userData, isLoading = false) }
                }
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect()
        }
    }
}
