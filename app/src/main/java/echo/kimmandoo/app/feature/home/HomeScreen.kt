package echo.kimmandoo.app.feature.home

import android.annotation.SuppressLint
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import echo.kimmandoo.app.navigation.Screen
import echo.kimmandoo.app.ui.theme.GradientBackground
import kotlin.math.roundToInt
import kotlin.math.sqrt

private data class MenuItem(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val route: Screen,
    var offset: Offset,
    var velocity: Offset = Offset.Zero,
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    padding: PaddingValues = PaddingValues(),
    navController: NavController,
    viewModel: HomeViewModel = viewModel(),
) {
    val currentUser = Firebase.auth.currentUser
    val uiState by viewModel.uiState.collectAsState()

    Scaffold {
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .background(
                    brush = GradientBackground,
                ).padding(padding),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.padding(top = 48.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("오늘의 햇살,", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4E342E))
                    Text(
                        "${currentUser?.displayName ?: "익명"}님의 이야기",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4E342E),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    UserStatus(
                        starCoins = uiState.userData.currency.toInt(),
                        hasFreeExchange = uiState.userData.freeLetterCount > 0
                    )
                }
            }

            PhysicsBasedMenuLayout(navController)
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun PhysicsBasedMenuLayout(navController: NavController) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val density = LocalDensity.current
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }

        val menuItemsState =
            remember(maxWidth, maxHeight) {
                val yOffset = screenHeightPx * 0.35f
                val itemCount = 4
                val spacing = screenWidthPx / (itemCount + 1)
                mutableStateOf(
                    listOf(
                        MenuItem(1, "일기 쓰기", Icons.Default.Create, Screen.DiaryCreation, Offset(spacing * 1 - screenWidthPx / 2, yOffset)),
                        MenuItem(2, "나의 일기장", Icons.Default.Home, Screen.MyDiaries, Offset(spacing * 2 - screenWidthPx / 2, yOffset)),
                        MenuItem(3, "내 프로필", Icons.Default.Person, Screen.Profile, Offset(spacing * 3 - screenWidthPx / 2, yOffset)),
                        MenuItem(4, "상점", Icons.Default.Star, Screen.Store, Offset(spacing * 4 - screenWidthPx / 2, yOffset)),
                    ),
                )
            }

        var draggedItemId by remember { mutableStateOf<Int?>(null) }

        // Physics simulation loop
        LaunchedEffect(menuItemsState) {
            var lastFrameTime = withFrameNanos { it }
            while (true) {
                val frameTime = withFrameNanos { it }
                val deltaTime = ((frameTime - lastFrameTime) / 1_000_000_000f).coerceAtMost(0.016f) // delta in seconds, capped at 60fps
                lastFrameTime = frameTime

                val currentItems = menuItemsState.value
                val newItems = currentItems.toMutableList()

                val itemRadiusPx = with(density) { 32.dp.toPx() }
                val friction = 0.98f
                val repulsionStrength = 2000f

                for (i in newItems.indices) {
                    if (newItems[i].id == draggedItemId) continue

                    var netForce = Offset.Zero

                    // Repulsion from other items
                    for (j in newItems.indices) {
                        if (i == j) continue
                        val dx = newItems[i].offset.x - newItems[j].offset.x
                        val dy = newItems[i].offset.y - newItems[j].offset.y
                        val distance = sqrt(dx * dx + dy * dy)
                        val minDistance = 2 * itemRadiusPx

                        if (distance < minDistance && distance != 0f) {
                            val direction = Offset(dx, dy) / distance
                            val forceMagnitude = repulsionStrength * (minDistance - distance) / minDistance
                            netForce += direction * forceMagnitude
                        }
                    }

                    // Update velocity and position
                    var item = newItems[i]
                    var newVelocity = (item.velocity + netForce * deltaTime) * friction
                    val newOffset = item.offset + newVelocity * deltaTime

                    // Boundary collision
                    val xMax = (screenWidthPx / 2) - itemRadiusPx
                    val yMax = (screenHeightPx / 2) - itemRadiusPx

                    if (newOffset.x !in -xMax..xMax) {
                        newVelocity = newVelocity.copy(x = -newVelocity.x * 0.8f)
                    }
                    if (newOffset.y !in -yMax..yMax) {
                        newVelocity = newVelocity.copy(y = -newVelocity.y * 0.8f)
                    }

                    val clampedX = newOffset.x.coerceIn(-xMax, xMax)
                    val clampedY = newOffset.y.coerceIn(-yMax, yMax)

                    newItems[i] = item.copy(offset = Offset(clampedX, clampedY), velocity = newVelocity)
                }

                menuItemsState.value = newItems
            }
        }

        TodaySun(onClick = { navController.navigate(Screen.ReceiveDiary) })

        menuItemsState.value.forEach { item ->
            DraggableMenuButton(
                modifier = Modifier.offset { IntOffset(item.offset.x.roundToInt(), item.offset.y.roundToInt()) },
                icon = item.icon,
                title = item.title,
                onClick = { navController.navigate(item.route) },
                onDragStart = { draggedItemId = item.id },
                onDrag = { dragAmount ->
                    val items = menuItemsState.value.toMutableList()
                    val index = items.indexOfFirst { it.id == item.id }
                    if (index != -1) {
                        items[index] = items[index].copy(offset = items[index].offset + dragAmount)
                        menuItemsState.value = items
                    }
                },
                onDragEnd = { draggedItemId = null },
            )
        }
    }
}

@Composable
fun UserStatus(
    starCoins: Int,
    hasFreeExchange: Boolean,
) {
    Row(
        modifier =
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.05f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        val textColor = Color(0xFF5D4037)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = "별코인", tint = Color(0xFFFFA000), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "$starCoins 개", color = textColor, fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (hasFreeExchange) Icons.Default.Check else Icons.Default.Star,
                contentDescription = "무료 교환",
                tint = if (hasFreeExchange) Color(0xFF4CAF50) else textColor.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (hasFreeExchange) "무료 교환 가능" else "교환 완료",
                color = textColor,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun TodaySun(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(animation = tween(3000), repeatMode = RepeatMode.Reverse),
        label = "pulseAnimation",
    )

    Box(
        modifier =
        Modifier
            .size(160.dp)
            .clickable(remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush =
                Brush.radialGradient(
                    colors =
                    listOf(
                        Color(0xFFFFF59D).copy(alpha = 0.9f * pulse),
                        Color(0xFFFBC02D).copy(alpha = 0.5f * pulse),
                        Color.Transparent,
                    ),
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.width / 2 * pulse,
                ),
            )
        }
//        Text("오늘의 햇살", color = Color(0xFF4E342E), fontWeight = FontWeight.Bold, fontSize = 22.sp, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun DraggableMenuButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
) {
    Column(
        modifier =
        modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = { onDragEnd() },
                )
            }.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
            Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFF5D4037), modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, color = Color(0xFF5D4037), fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}