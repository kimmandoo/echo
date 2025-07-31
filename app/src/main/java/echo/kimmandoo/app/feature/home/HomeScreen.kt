package echo.kimmandoo.app.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(padding: PaddingValues = PaddingValues()) {
    Box(modifier = Modifier.padding(paddingValues = padding)) {
        Text(text = "Home")
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
