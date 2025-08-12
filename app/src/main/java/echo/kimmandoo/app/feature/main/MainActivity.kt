package echo.kimmandoo.app.feature.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import echo.kimmandoo.app.data.GoogleAuthUiClient
import echo.kimmandoo.app.feature.auth.AuthViewModel
import echo.kimmandoo.app.navigation.AppNavHost
import echo.kimmandoo.app.navigation.AuthNavigationEffect
import echo.kimmandoo.app.navigation.Screen
import echo.kimmandoo.app.ui.theme.EchoTheme

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = this,
            auth = Firebase.auth,
        )
    }

    private val viewModel by viewModels<AuthViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return AuthViewModel(googleAuthUiClient) as T
                }
            }
        },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EchoTheme {
                val navController = rememberNavController()
                val signInState by viewModel.signInState.collectAsState()
                val currentUser by viewModel.currentUser.collectAsState()

                LaunchedEffect(signInState.error) {
                    signInState.error?.let { error ->
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                        viewModel.consumedError()
                    }
                }

                AuthNavigationEffect(
                    navController = navController,
                    isLoggedIn = currentUser != null,
                )

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        startDestination =
                            remember {
                                if (googleAuthUiClient.getSignedInUser() != null) Screen.Home else Screen.Auth
                            },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
