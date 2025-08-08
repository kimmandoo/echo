package echo.kimmandoo.app.feature.diary.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Reply(
    @DocumentId val id: String = "",
    val originalDiaryId: String = "",
    val parentReplyId: String? = null, // 대댓글의 경우 부모 답장의 ID
    val replierId: String? = null,
    val replierName: String? = null,
    val content: String = "",
    @ServerTimestamp val timestamp: Date? = null,
) {
    val isReplyToReply: Boolean
        get() = parentReplyId != null
}

