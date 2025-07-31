package echo.kimmandoo.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AuthNavigationEffect(
    navController: NavHostController,
    isLoggedIn: Boolean,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(isLoggedIn, currentDestination) {
        if (isLoggedIn) {
            if (currentDestination?.hasRoute<Screen.Home>() != true) {
                navController.navigate(Screen.Home) {
                    popUpTo(Screen.Auth) {
                        inclusive = true
                    }
                }
            }
        } else {
            if (currentDestination?.hasRoute<Screen.Auth>() != true) {
                navController.navigate(Screen.Auth) {
                    popUpTo(Screen.Auth) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}
