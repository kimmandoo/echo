package echo.kimmandoo.app.data

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import echo.kimmandoo.app.R
import echo.kimmandoo.app.feature.auth.model.SignInResult
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val context: Context,
    private val auth: FirebaseAuth,
) {
    companion object {
        private const val TAG = "GoogleAuthUiClient"
    }

    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(): SignInResult {
        val googleIdOption: GetGoogleIdOption =
            GetGoogleIdOption
                .Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .build()

        val request: GetCredentialRequest =
            GetCredentialRequest
                .Builder()
                .addCredentialOption(googleIdOption)
                .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential =
                    GoogleAuthProvider.getCredential(googleIdToken.idToken, null)
                val user = auth.signInWithCredential(firebaseCredential).await().user
                if (user != null) {
                    SignInResult.Success(user)
                } else {
                    SignInResult.Error("Firebase 인증에 실패했습니다.")
                }
            } else {
                Log.w(TAG, "Credential is not of type Google ID!")
                SignInResult.Error("구글 아이디 토큰을 가져오지 못했습니다.")
            }
        } catch (e: NoCredentialException) {
            Log.e(TAG, "No credentials available.", e)
            SignInResult.Error("기기에 로그인된 구글 계정이 없습니다. 계정을 추가한 후 다시 시도해주세요.")
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Couldn't retrieve user's credentials.", e)
            SignInResult.Error("로그인에 실패했습니다")
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in failed", e)
            SignInResult.Error("알 수 없는 오류가 발생했습니다")
        }
    }

    suspend fun signOut() {
        try {
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            Log.d(TAG, "Sign-out successful")
        } catch (e: Exception) {
            Log.e(TAG, "Sign-out failed: ${e.message}")
            SignInResult.Error("로그아웃에 실패했습니다")
        }
    }

    fun getSignedInUser(): FirebaseUser? = auth.currentUser
}
