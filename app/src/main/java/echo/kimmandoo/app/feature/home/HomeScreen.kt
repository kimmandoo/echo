package echo.kimmandoo.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import echo.kimmandoo.app.feature.home.component.Mailbox
import echo.kimmandoo.app.feature.home.component.UserStatus
import echo.kimmandoo.app.feature.home.model.MenuItem
import echo.kimmandoo.app.navigation.Screen
import echo.kimmandoo.app.ui.theme.GradientBackground
import kotlinx.coroutines.delay

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

private data class MenuItemData(val id: Int, val title: String, val icon: ImageVector, val route: Screen)

private val homeMenuItemsData = listOf(
    MenuItemData(1, "일기 쓰기", Icons.Default.Create, Screen.DiaryCreation),
    MenuItemData(2, "나의 일기장", Icons.Default.Home, Screen.MyDiaries),
    MenuItemData(3, "내 프로필", Icons.Default.Person, Screen.Profile),
    MenuItemData(4, "상점", Icons.Default.Star, Screen.Store)
)

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
                    val spacing = screenWidthPx / (homeMenuItemsData.size + 1)
                    mutableStateListOf(
                        *homeMenuItemsData.mapIndexed { index, data ->
                            MenuItem(
                                id = data.id,
                                title = data.title,
                                icon = data.icon,
                                route = data.route,
                                offset = Offset(spacing * (index + 1) - screenWidthPx / 2, yOffset),
                            )
                        }.toTypedArray()
                    )
                }

            var draggedItemId by remember { mutableStateOf<Int?>(null) }

            val physicsController =
                remember(key1 = size) {
                    val itemRadiusPx = with(density) { 32.dp.toPx() }
                    MenuPhysicsController(menuItemsState, size, itemRadiusPx)
                }

            var mailboxSize by remember { mutableStateOf(IntSize.Zero) }
            var isHoveringOnMailbox by remember { mutableStateOf(false) }
            var isJustDropped by remember { mutableStateOf(false) }

            LaunchedEffect(isJustDropped) {
                if (isJustDropped) {
                    delay(2000L)
                    isJustDropped = false
                }
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
                        modifier =
                        Modifier.graphicsLayer {
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
                                val newItem =
                                    menuItemsState[index].copy(offset = menuItemsState[index].offset + dragAmount)
                                menuItemsState[index] = newItem

                                val distance = newItem.offset.getDistance()
                                val mailboxRadius = mailboxSize.width / 2f
                                isHoveringOnMailbox = if (mailboxRadius > 0) distance < mailboxRadius else false
                            }
                        },
                        onDragEnd = {
                            if (isHoveringOnMailbox) {
                                isJustDropped = true
                                navigateToReceivedDiaryScreen(Screen.ReceiveDiary)
                                val index = menuItemsState.indexOfFirst { it.id == draggedItemId }
                                if (index != -1) {
                                    menuItemsState.removeAt(index)
                                }
                            }
                            draggedItemId = null
                            isHoveringOnMailbox = false
                        },
                    )
                }
            }

            Mailbox(
                modifier = Modifier.onSizeChanged { mailboxSize = it },
                isHovering = isHoveringOnMailbox,
                isJustDropped = isJustDropped
            )
        }
    }
}