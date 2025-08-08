package echo.kimmandoo.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import echo.kimmandoo.app.feature.diary.model.Diary
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

// 통계 데이터 모델
data class UserStats(
    val diariesWritten: Long = 0,
    val repliesSent: Long = 0,
    val repliesReceived: Long = 0,
)

// 타임라인 아이템 모델
data class ActivityItem(
    val id: String = "",
    val userId: String = "",
    val type: String = "", // DIARY_SENT, REPLY_SENT, REPLY_RECEIVED
    val contentSnippet: String = "",
    val relatedId: String = "", // Diary ID or Reply ID
    val timestamp: Date = Date()
)

class HistoryRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val uid: String? get() = auth.currentUser?.uid

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val DIARIES_COLLECTION = "diaries"
        private const val ACTIVITY_COLLECTION = "activities"
    }

    suspend fun getUserStats(): UserStats {
        val userId = uid ?: return UserStats()
        return try {
            db.collection(USERS_COLLECTION).document(userId).get().await().toObject(UserStats::class.java) ?: UserStats()
        } catch (e: Exception) {
            println("Error getting user stats: $e")
            UserStats()
        }
    }

    suspend fun getTimelineActivities(): List<ActivityItem> {
        val userId = uid ?: return emptyList()
        return try {
            db.collection(ACTIVITY_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50) // 성능을 위해 최근 50개만 로드
                .get()
                .await()
                .toObjects(ActivityItem::class.java)
        } catch (e: Exception) {
            println("Error getting timeline: $e")
            emptyList()
        }
    }

    suspend fun getDiariesForMonth(year: Int, month: Int): List<Diary> {
        val userId = uid ?: return emptyList()

        val calendar = Calendar.getInstance()
        // 월의 시작
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.time

        // 월의 끝
        calendar.set(year, month - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.time

        return try {
            db.collection(DIARIES_COLLECTION)
                .whereEqualTo("authorId", userId)
                .whereGreaterThanOrEqualTo("timestamp", startOfMonth)
                .whereLessThanOrEqualTo("timestamp", endOfMonth)
                .get()
                .await()
                .toObjects(Diary::class.java)
        } catch (e: Exception) {
            println("Error getting diaries for month: $e")
            emptyList()
        }
    }
}
