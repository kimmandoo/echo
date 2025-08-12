package echo.kimmandoo.app.feature.diary.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Diary(
    @DocumentId val id: String = "",
    val authorId: String? = null,
    val authorName: String? = null,
    val content: String = "",
    val emotion: String = "", // weather -> emotion
    @ServerTimestamp val timestamp: Date? = null,
    val status: String = "PRIVATE", // PRIVATE, PENDING, REPLIED
    val reply: String? = null,
)
