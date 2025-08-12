package echo.kimmandoo.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import echo.kimmandoo.app.feature.home.component.DraggableMenuButton
import echo.kimmandoo.app.feature.home.component.TodaySun
import echo.kimmandoo.app.feature.home.component.UserStatus
import echo.kimmandoo.app.feature.home.model.MenuItem
import echo.kimmandoo.app.navigation.Screen
import echo.kimmandoo.app.ui.theme.GradientBackground

@Composable
fun HomeScreen(
    navigateToReceivedDiaryScreen: (Screen) -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val currentUser = Firebase.auth.currentUser
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        navigateToReceivedDiaryScreen = navigateToReceivedDiaryScreen,
        userName = currentUser?.displayName ?: "익명",
        starCoins = uiState.userData.currency.toInt(),
        hasFreeExchange = uiState.userData.freeLetterCount > 0,
    )
}

@Composable
private fun HomeScreen(
    navigateToReceivedDiaryScreen: (Screen) -> Unit,
    userName: String,
    starCoins: Int,
    hasFreeExchange: Boolean,
) {
    Scaffold { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        brush = GradientBackground,
                    ).padding(paddingValues),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.padding(top = 48.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "안녕하세요,",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4E342E),
                    )
                    Text(
                        "${userName}님",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4E342E),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    UserStatus(
                        starCoins = starCoins,
                        hasFreeExchange = hasFreeExchange,
                    )
                }
            }

            PhysicsBasedMenuLayout(navigateToReceivedDiaryScreen)
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        navigateToReceivedDiaryScreen = {},
        userName = "익명",
        starCoins = 100,
        hasFreeExchange = true,
    )
}

@Composable
fun PhysicsBasedMenuLayout(navigateToReceivedDiaryScreen: (Screen) -> Unit) {
    var containerSize by remember { mutableStateOf<IntSize?>(null) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .onSizeChanged { containerSize = it },
        contentAlignment = Alignment.Center,
    ) {
        containerSize?.let { size ->
            val density = LocalDensity.current
            val screenWidthPx = size.width.toFloat()
            val screenHeightPx = size.height.toFloat()

            val menuItemsState =
                remember(key1 = size) {
                    val yOffset = screenHeightPx * 0.35f
                    val itemCount = 4
                    val spacing = screenWidthPx / (itemCount + 1)
                    mutableStateListOf(
                        MenuItem(
                            id = 1,
                            title = "일기 쓰기",
                            icon = Icons.Default.Create,
                            route = Screen.DiaryCreation,
                            offset = Offset(spacing * 1 - screenWidthPx / 2, yOffset),
                        ),
                        MenuItem(
                            id = 2,
                            title = "나의 일기장",
                            icon = Icons.Default.Home,
                            route = Screen.MyDiaries,
                            offset = Offset(spacing * 2 - screenWidthPx / 2, yOffset),
                        ),
                        MenuItem(
                            id = 3,
                            title = "내 프로필",
                            icon = Icons.Default.Person,
                            route = Screen.Profile,
                            offset = Offset(spacing * 3 - screenWidthPx / 2, yOffset),
                        ),
                        MenuItem(
                            id = 4,
                            title = "상점",
                            icon = Icons.Default.Star,
                            route = Screen.Store,
                            offset = Offset(spacing * 4 - screenWidthPx / 2, yOffset),
                        ),
                    )
                }

            var draggedItemId by remember { mutableStateOf<Int?>(null) }

            val physicsController =
                remember(key1 = size) {
                    val itemRadiusPx = with(density) { 32.dp.toPx() }
                    MenuPhysicsController(menuItemsState, size, itemRadiusPx)
                }

            LaunchedEffect(key1 = physicsController) {
                var lastFrameTime = withFrameNanos { it }
                while (true) {
                    val frameTime = withFrameNanos { it }
                    val deltaTime =
                        ((frameTime - lastFrameTime) / 1_000_000_000f).coerceAtMost(0.016f)
                    lastFrameTime = frameTime

                    physicsController.update(deltaTime, draggedItemId)
                }
            }

            menuItemsState.forEach { item ->
                key(item.id) {
                    DraggableMenuButton(
//                        modifier = Modifier.offset {
//                            IntOffset(
//                                item.offset.x.roundToInt(),
//                                item.offset.y.roundToInt()
//                            )
                        modifier =
                            Modifier.graphicsLayer {
                                // TODO: graphicsLayer랑 그냥 생으로 offset 처리하는 거 조사하기
                                translationX = item.offset.x
                                translationY = item.offset.y
                            },
                        icon = item.icon,
                        title = item.title,
                        onClick = {
                            navigateToReceivedDiaryScreen(item.route)
                        },
                        onDragStart = { draggedItemId = item.id },
                        onDrag = { dragAmount ->
                            val index = menuItemsState.indexOfFirst { it.id == item.id }
                            if (index != -1) {
                                menuItemsState[index] =
                                    menuItemsState[index].copy(offset = menuItemsState[index].offset + dragAmount)
                            }
                        },
                        onDragEnd = { draggedItemId = null },
                    )
                }
            }

            TodaySun(onClick = { navigateToReceivedDiaryScreen(Screen.ReceiveDiary) })
        }
    }
}
