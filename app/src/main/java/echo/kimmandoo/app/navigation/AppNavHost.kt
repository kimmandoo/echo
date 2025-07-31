package echo.kimmandoo.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import echo.kimmandoo.app.feature.auth.AuthScreen
import echo.kimmandoo.app.feature.home.HomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Screen,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<Screen.Auth> {
            AuthScreen()
        }
        composable<Screen.Home> {
            HomeScreen()
        }
    }
}
