package echo.kimmandoo.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import echo.kimmandoo.app.data.HistoryRepository
import echo.kimmandoo.app.data.UserData
import echo.kimmandoo.app.data.UserRepository
import echo.kimmandoo.app.data.UserStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userName: String? = FirebaseAuth.getInstance().currentUser?.displayName,
    val userEmail: String? = FirebaseAuth.getInstance().currentUser?.email,
    val userData: UserData = UserData(),
    val userStats: UserStats = UserStats(),
)

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val historyRepository: HistoryRepository = HistoryRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val stats = historyRepository.getUserStats()
            _uiState.update { it.copy(userStats = stats) }

            userRepository.listenToUserData().collect { userData ->
                _uiState.update { it.copy(isLoading = false, userData = userData) }
            }
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}
