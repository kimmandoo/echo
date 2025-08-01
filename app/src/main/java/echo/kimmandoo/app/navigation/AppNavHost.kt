package echo.kimmandoo.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import echo.kimmandoo.app.feature.auth.AuthScreen
import echo.kimmandoo.app.feature.diary.DiaryCreationRoute
import echo.kimmandoo.app.feature.history.HistoryScreen
import echo.kimmandoo.app.feature.home.HomeScreen
import echo.kimmandoo.app.feature.mydiaries.MyDiariesScreen
import echo.kimmandoo.app.feature.profile.ProfileScreen
import echo.kimmandoo.app.feature.receive.ReceiveDiaryScreen
import echo.kimmandoo.app.feature.receive.ReceivedDiaryScreen
import echo.kimmandoo.app.feature.receive.ReplyDiaryScreen

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
            HomeScreen(navController = navController)
        }
        composable<Screen.Profile> {
            ProfileScreen()
        }
        composable<Screen.MyDiaries> {
            MyDiariesScreen()
        }
        composable<Screen.History> {
            HistoryScreen()
        }
        composable<Screen.DiaryCreation> {
            DiaryCreationRoute(onExit = { navController.popBackStack() })
        }
        composable<Screen.ReceiveDiary> {
            ReceiveDiaryScreen(navController = navController)
        }
        composable<Screen.ReceivedDiary> {
            val args = it.toRoute<Screen.ReceivedDiary>()
            ReceivedDiaryScreen(navController = navController, diaryContent = args.diaryContent)
        }
        composable<Screen.ReplyDiary> {
            ReplyDiaryScreen(navController = navController)
        }
    }
}
