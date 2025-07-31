package echo.kimmandoo.app.feature.auth

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import echo.kimmandoo.app.data.GoogleAuthUiClient
import echo.kimmandoo.app.feature.auth.model.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
)

class AuthViewModel(
    private val googleAuthUiClient: GoogleAuthUiClient,
) : ViewModel() {
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _signInState = MutableStateFlow(AuthUiState())
    val signInState = _signInState.asStateFlow()

    init {
        _currentUser.value = googleAuthUiClient.getSignedInUser()
    }

    fun signIn() {
        viewModelScope.launch {
            _signInState.update { it.copy(isLoading = true) }
            when (val result = googleAuthUiClient.signIn()) {
                is SignInResult.Success -> {
                    _currentUser.value = result.user
                    _signInState.update {
                        it.copy(isLoading = false, isSuccess = true)
                    }
                }
                is SignInResult.Error -> {
                    _signInState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            googleAuthUiClient.signOut()
            _currentUser.value = null
            _signInState.value = AuthUiState()
        }
    }

    fun consumedError() {
        _signInState.update { it.copy(error = null) }
    }
}
