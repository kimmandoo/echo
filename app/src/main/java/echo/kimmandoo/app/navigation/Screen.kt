package echo.kimmandoo.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Auth : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object DiaryCreation : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object MyDiaries : Screen()

    @Serializable
    data class MyDiaryDetail(
        val diaryId: String,
    ) : Screen()

    @Serializable
    data object Store : Screen()

    @Serializable
    data object ReceiveDiary : Screen()

    @Serializable
    data class ReceivedDiary(
        val diaryId: String,
        val diaryContent: String,
    ) : Screen()

    @Serializable
    data class ReplyDiary(
        val diaryId: String,
    ) : Screen()
}
