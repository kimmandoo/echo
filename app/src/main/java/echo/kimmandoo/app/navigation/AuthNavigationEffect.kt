package echo.kimmandoo.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

@Composable
fun AuthNavigationEffect(
    navController: NavHostController,
    isLoggedIn: Boolean,
) {
    LaunchedEffect(isLoggedIn) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        if (isLoggedIn) {
            // 로그인 상태인데 현재 화면이 AuthScreen이면 HomeScreen으로 이동
            if (currentRoute ==
                Screen.Auth
                    .serializer()
                    .descriptor.serialName
            ) {
                navController.navigate(Screen.Home) {
                    popUpTo(Screen.Auth) { inclusive = true }
                }
            }
        } else {
            // 로그아웃 상태인데 현재 화면이 AuthScreen이 아니면 AuthScreen으로 이동
            if (currentRoute !=
                Screen.Auth
                    .serializer()
                    .descriptor.serialName
            ) {
                navController.navigate(Screen.Auth) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}
