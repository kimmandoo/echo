package echo.kimmandoo.app.feature.auth

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(padding: PaddingValues = PaddingValues()) {
    val viewModel: AuthViewModel =
        viewModel(
            viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        )
    val state by viewModel.signInState.collectAsState()

    AuthScreenContent(
        isLoading = state.isLoading,
        onSignInClick = viewModel::signIn,
        modifier = Modifier.padding(padding),
    )
}

@Composable
private fun AuthScreenContent( // Stateless
    isLoading: Boolean,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = onSignInClick) {
                Text(text = "Google 계정으로 로그인")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AuthScreenContent(isLoading = false, onSignInClick = {})
}

@Preview(showBackground = true)
@Composable
fun AuthScreenLoadingPreview() {
    AuthScreenContent(isLoading = true, onSignInClick = {})
}
