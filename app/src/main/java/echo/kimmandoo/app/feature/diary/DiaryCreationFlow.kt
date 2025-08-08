package echo.kimmandoo.app.feature.diary
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

// TopLevel
@Composable
fun DiaryCreationRoute(
    viewModel: DiaryViewModel = viewModel(),
    onExit: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    AnimatedContent(
        targetState = uiState.status,
        transitionSpec = {
            if (targetState.ordinal > initialState.ordinal) {
                // Going forward (Writing -> Sending -> Completed)
                slideInHorizontally { width -> width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> -width } + fadeOut()
            } else {
                // Going back (Completed -> Writing)
                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> width } + fadeOut()
            }.using(SizeTransform(clip = false))
        },
        label = "Diary State",
    ) { targetStatus ->
        when (targetStatus) {
            DiaryStatus.WRITING ->
                DiaryWriteScreen(
                    uiState = uiState,
                    onTextChange = viewModel::onTextChange,
                    onEmotionSelect = viewModel::onEmotionSelect,
                    onSendClick = viewModel::sendDiary,
                    onBackClick = onExit,
                )
            DiaryStatus.SENDING -> DiarySendingScreen()
            DiaryStatus.COMPLETED ->
                DiaryCompleteScreen(
                    onConfirmClick = onExit,
                )
        }
    }
}
