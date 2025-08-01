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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import echo.kimmandoo.app.navigation.Screen
import kotlin.math.roundToInt
import kotlin.math.sqrt

private data class MenuItem(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val route: Screen,
    var offset: Offset
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
@Composable
fun HomeScreen(
    padding: PaddingValues = PaddingValues(),
    navController: NavController,
) {
    val currentUser = Firebase.auth.currentUser
    val starCoins = 12
    val hasFreeExchange = true

    val menuItemsState = remember {
        mutableStateOf(
            listOf(
                MenuItem(1, "일기 쓰기", Icons.Default.Create, Screen.DiaryCreation, Offset(-120f, 350f)),
                MenuItem(2, "나의 일기장", Icons.Default.Home, Screen.MyDiaries, Offset(-40f, 350f)),
                MenuItem(3, "내 프로필", Icons.Default.Person, Screen.Profile, Offset(40f, 350f)),
                MenuItem(4, "교환 기록", Icons.Default.Refresh, Screen.History, Offset(120f, 350f))
            )
        )
    }

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.padding(top = 48.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("오늘의 햇살,", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4E342E))
                    Text("${currentUser?.displayName ?: "익명"}님의 이야기", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4E342E))
                    Spacer(modifier = Modifier.height(24.dp))
                    UserStatus(starCoins = starCoins, hasFreeExchange = hasFreeExchange)
                }
            }

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val density = LocalDensity.current
                val itemRadiusPx = remember { with(density) { (64.dp / 2).toPx() } }
                val itemWidthPx = remember { with(density) { 64.dp.toPx() } }
                val itemHeightPx = remember { with(density) { 90.dp.toPx() } } // Approximate height of the draggable item

                val screenWidthPx = with(density) { maxWidth.toPx() }
                val screenHeightPx = with(density) { maxHeight.toPx() }

                fun handleDrag(draggedItemId: Int, dragAmount: Offset) {
                    val currentItems = menuItemsState.value.toMutableList()
                    val draggedItemIndex = currentItems.indexOfFirst { it.id == draggedItemId }
                    if (draggedItemIndex == -1) return

                    val draggedItem = currentItems[draggedItemIndex]
                    currentItems[draggedItemIndex] = draggedItem.copy(offset = draggedItem.offset + dragAmount)

                    for (i in 0 until currentItems.size) {
                        for (j in i + 1 until currentItems.size) {
                            val itemA = currentItems[i]
                            val itemB = currentItems[j]

                            val dx = itemA.offset.x - itemB.offset.x
                            val dy = itemA.offset.y - itemB.offset.y
                            val distance = sqrt(dx * dx + dy * dy)
                            val minDistance = 2 * itemRadiusPx

                            if (distance < minDistance && distance != 0f) {
                                val overlap = minDistance - distance
                                val normal = Offset(dx / distance, dy / distance)

                                when {
                                    itemA.id == draggedItemId -> currentItems[j] = itemB.copy(offset = itemB.offset - normal * overlap)
                                    itemB.id == draggedItemId -> currentItems[i] = itemA.copy(offset = itemA.offset + normal * overlap)
                                    else -> {
                                        val halfOverlap = overlap / 2
                                        currentItems[i] = itemA.copy(offset = itemA.offset + normal * halfOverlap)
                                        currentItems[j] = itemB.copy(offset = itemB.offset - normal * halfOverlap)
                                    }
                                }
                            }
                        }
                    }

                    val xMax = (screenWidthPx - itemWidthPx) / 2
                    val yMax = (screenHeightPx - itemHeightPx) / 2

                    val clampedItems = currentItems.map {
                        val clampedX = it.offset.x.coerceIn(-xMax, xMax)
                        val clampedY = it.offset.y.coerceIn(-yMax, yMax)
                        it.copy(offset = Offset(clampedX, clampedY))
                    }

                    menuItemsState.value = clampedItems
                }

                TodaySun(onClick = { navController.navigate(Screen.ReceiveDiary) })

                menuItemsState.value.forEach { item ->
                    DraggableMenuButton(
                        modifier = Modifier.offset { IntOffset(item.offset.x.roundToInt(), item.offset.y.roundToInt()) },
                        icon = item.icon,
                        title = item.title,
                        onClick = { navController.navigate(item.route) },
                        onDrag = { dragAmount -> handleDrag(item.id, dragAmount) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserStatus(starCoins: Int, hasFreeExchange: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.05f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
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
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (hasFreeExchange) "무료 교환 가능" else "교환 완료",
                color = textColor, fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TodaySun(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(animation = tween(3000), repeatMode = RepeatMode.Reverse),
        label = "pulseAnimation"
    )

    Box(
        modifier = Modifier
            .size(160.dp)
            .clickable(remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF59D).copy(alpha = 0.9f * pulse),
                        Color(0xFFFBC02D).copy(alpha = 0.5f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.width / 2 * pulse
                )
            )
        }
        Text("오늘의 햇살", color = Color(0xFF4E342E), fontWeight = FontWeight.Bold, fontSize = 22.sp, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun DraggableMenuButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    onDrag: (Offset) -> Unit
) {
    Column(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures {
                    change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                }
            }
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
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


