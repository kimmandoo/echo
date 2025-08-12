package echo.kimmandoo.app.feature.home.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import echo.kimmandoo.app.navigation.Screen

@Immutable
data class MenuItem(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val route: Screen,
    val offset: Offset,
    val velocity: Offset = Offset.Zero,
)
