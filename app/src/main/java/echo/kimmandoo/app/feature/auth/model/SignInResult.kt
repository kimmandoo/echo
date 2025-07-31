package echo.kimmandoo.app.feature.auth.model

import com.google.firebase.auth.FirebaseUser

sealed class SignInResult {
    data class Success(
        val user: FirebaseUser,
    ) : SignInResult()

    data class Error(
        val message: String,
    ) : SignInResult()
}
