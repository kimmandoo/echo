package echo.kimmandoo.app.feature.diary.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DiaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
            textStyle =
            MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp,
            ),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        "오늘 하루, 어떤 이야기를 담고 있나요?",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    )
                }
                innerTextField()
            },
        )
        Text(
            text = "${value.length} / 300",
            modifier =
            Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            fontSize = 12.sp,
        )
    }
}