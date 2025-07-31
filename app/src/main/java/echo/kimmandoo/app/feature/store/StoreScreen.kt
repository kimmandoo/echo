package echo.kimmandoo.app.feature.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import echo.kimmandoo.app.ui.theme.GradientBackground

data class StoreItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
)

data class PurchaseItem(
    val id: String,
    val name: String,
    val description: String,
    val price: String, // "₩1,200"
)

val starStoreItems = listOf(
    StoreItem("theme_sunset", "노을 테마", "프로필을 아름다운 노을 빛으로 물들여보세요.", 100),
    StoreItem("theme_forest", "숲속 테마", "상쾌한 숲속의 향기를 프로필에 담아보세요.", 100),
    StoreItem("sticker_pack_animals", "동물 스티커 팩", "귀여운 동물 친구들 스티커로 일기를 꾸며보세요.", 50),
    StoreItem("sticker_pack_seasons", "계절 스티커 팩", "계절의 변화를 스티커로 표현해보세요.", 50),
    StoreItem("exchange_ticket", "일기 교환권", "하루에 한 번 더 일기를 교환할 수 있습니다.", 200),
)

val cashStoreItems = listOf(
    PurchaseItem("coin_pack_1", "별 500개", "앱 활동에 사용할 수 있는 별입니다.", "₩1,200"),
    PurchaseItem("coin_pack_2", "별 3,000개", "앱 활동에 사용할 수 있는 별입니다.", "₩5,900"),
    PurchaseItem("ad_removal", "광고 제거", "모든 광고를 영구적으로 제거합니다.", "₩8,900"),
)

@Composable
fun StoreScreen() {
    Scaffold(
        containerColor = Color.Transparent
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientBackground)
                .padding(it)
                .padding(16.dp)
        ) {
            item {
                Text("상점", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                Text("별을 사용하거나, 특별 상품을 구매하여 앱을 더 풍부하게 즐겨보세요!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("특별 상품", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(cashStoreItems.chunked(2)) { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    rowItems.forEach { item ->
                        PurchaseItemCard(item = item, modifier = Modifier.weight(1f))
                    }
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("별로 구매", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(starStoreItems.chunked(2)) { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    rowItems.forEach { item ->
                        StoreItemCard(item = item, modifier = Modifier.weight(1f))
                    }
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StoreItemCard(item: StoreItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.height(60.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "별", tint = Color(0xFFFFA000), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(item.price.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { /* TODO: 별로 구매 처리 */ },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("구매")
                }
            }
        }
    }
}

@Composable
fun PurchaseItemCard(item: PurchaseItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val icon = if(item.id.contains("ad")) Icons.Default.ShoppingCart else Icons.Default.Star
            Icon(icon, contentDescription = item.name, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f), modifier = Modifier.height(40.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO: 인앱 결제 처리 */ },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(item.price, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}