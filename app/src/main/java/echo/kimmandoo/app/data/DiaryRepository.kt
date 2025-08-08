package echo.kimmandoo.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import echo.kimmandoo.app.feature.diary.model.Diary
import echo.kimmandoo.app.feature.diary.model.Reply
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.Date

sealed class ReceiveLetterResult {
    data class Success(
        val diary: Diary,
        val paidWith: String,
    ) : ReceiveLetterResult()

    object NoDiaryAvailable : ReceiveLetterResult()

    object InsufficientFunds : ReceiveLetterResult()

    data class Error(
        val message: String,
    ) : ReceiveLetterResult()
}

class DiaryRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    companion object {
        private const val USERS_COLLECTION = "users"
        private const val DIARIES_COLLECTION = "diaries"
        private const val REPLIES_COLLECTION = "replies"
        private const val READ_DIARIES_COLLECTION = "readDiaries"
        private const val ACTIVITY_COLLECTION = "activities"
        private const val MAX_FETCH_ATTEMPTS = 5 // 무한 루프 방지를 위한 최대 시도 횟수
    }

    suspend fun getUserFreeChanceCount(): Long {
        val currentUser = auth.currentUser ?: return 0L
        return try {
            val document =
                db
                    .collection(USERS_COLLECTION)
                    .document(currentUser.uid)
                    .get()
                    .await()
            document.getLong("freeLetterCount") ?: 0L
        } catch (e: Exception) {
            println("Error fetching user free chance count: $e")
            0L
        }
    }

    suspend fun receiveLetter(useCurrency: Boolean): ReceiveLetterResult {
        val currentUser = auth.currentUser ?: return ReceiveLetterResult.Error("로그인이 필요합니다.")

        val diaryToRead =
            findUnreadDiary(currentUser.uid)
                ?: return ReceiveLetterResult.NoDiaryAvailable

        val userRef = db.collection(USERS_COLLECTION).document(currentUser.uid)
        return try {
            val paidWith =
                db
                    .runTransaction { transaction ->
                        val userSnapshot = transaction.get(userRef)

                        val paidResult =
                            if (useCurrency) {
                                val currency = userSnapshot.getLong("currency") ?: 0L
                                if (currency > 0) {
                                    transaction.update(userRef, "currency", FieldValue.increment(-1))
                                    "currency"
                                } else {
                                    throw FirebaseFirestoreException("재화가 부족합니다.", FirebaseFirestoreException.Code.FAILED_PRECONDITION)
                                }
                            } else {
                                val freeLetterCount = userSnapshot.getLong("freeLetterCount") ?: 0L
                                if (freeLetterCount > 0) {
                                    transaction.update(userRef, "freeLetterCount", FieldValue.increment(-1))
                                    "free"
                                } else {
                                    throw FirebaseFirestoreException("무료 기회가 없습니다.", FirebaseFirestoreException.Code.FAILED_PRECONDITION)
                                }
                            }
                        paidResult
                    }.await()

            markDiaryAsRead(currentUser.uid, diaryToRead.id)
            ReceiveLetterResult.Success(diaryToRead, paidWith)
        } catch (e: Exception) {
            if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.FAILED_PRECONDITION) {
                ReceiveLetterResult.InsufficientFunds
            } else {
                ReceiveLetterResult.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    private suspend fun findUnreadDiary(myUid: String): Diary? {
        repeat(MAX_FETCH_ATTEMPTS) {
            val candidateDiary = fetchRandomDiary(myUid)
            if (candidateDiary != null) {
                val isRead = hasUserReadDiary(myUid, candidateDiary.id)
                if (!isRead) {
                    return candidateDiary
                }
            }
        }
        return null
    }

    suspend fun saveDiary(
        content: String,
        emotion: String,
    ): Boolean {
        val currentUser = auth.currentUser ?: return false
        val diaryRef = db.collection(DIARIES_COLLECTION).document()
        val diary =
            Diary(
                id = diaryRef.id,
                authorId = currentUser.uid,
                authorName = currentUser.displayName,
                content = content,
                emotion = emotion,
            )

        val userStatsRef = db.collection(USERS_COLLECTION).document(currentUser.uid)
        val activityRef = db.collection(ACTIVITY_COLLECTION).document()
        val activity =
            ActivityItem(
                id = activityRef.id,
                userId = currentUser.uid,
                type = "DIARY_SENT",
                contentSnippet = content.take(50),
                relatedId = diary.id,
                timestamp = Date(),
            )

        return try {
            db
                .runBatch {
                    it.set(diaryRef, diary)
                    it.set(activityRef, activity)
                    it.update(userStatsRef, "diariesWritten", FieldValue.increment(1))
                }.await()
            true
        } catch (e: Exception) {
            println("Error saving diary: $e")
            false
        }
    }

    suspend fun saveReply(
        originalDiaryId: String,
        content: String,
        parentReplyId: String? = null,
    ): Boolean {
        val currentUser = auth.currentUser ?: return false
        val replyRef = db.collection(REPLIES_COLLECTION).document()
        val reply =
            Reply(
                id = replyRef.id,
                originalDiaryId = originalDiaryId,
                parentReplyId = parentReplyId,
                replierId = currentUser.uid,
                replierName = currentUser.displayName,
                content = content,
            )

        val userStatsRef = db.collection(USERS_COLLECTION).document(currentUser.uid)
        val activityRef = db.collection(ACTIVITY_COLLECTION).document()
        val activity =
            ActivityItem(
                id = activityRef.id,
                userId = currentUser.uid,
                type = "REPLY_SENT",
                contentSnippet = content.take(50),
                relatedId = reply.id,
                timestamp = Date(),
            )

        // Also update the original diary author's stats
        val originalDiary =
            db
                .collection(DIARIES_COLLECTION)
                .document(originalDiaryId)
                .get()
                .await()
                .toObject(Diary::class.java)
        val originalAuthorId = originalDiary?.authorId

        return try {
            db
                .runBatch {
                    it.set(replyRef, reply)
                    it.set(activityRef, activity)
                    it.update(userStatsRef, "repliesSent", FieldValue.increment(1))
                    if (originalAuthorId != null) {
                        val originalAuthorStatsRef = db.collection(USERS_COLLECTION).document(originalAuthorId)
                        it.update(originalAuthorStatsRef, "repliesReceived", FieldValue.increment(1))

                        val receivedActivityRef = db.collection(ACTIVITY_COLLECTION).document()
                        val receivedActivity =
                            ActivityItem(
                                id = receivedActivityRef.id,
                                userId = originalAuthorId,
                                type = "REPLY_RECEIVED",
                                contentSnippet = content.take(50),
                                relatedId = reply.id,
                                timestamp = Date(),
                            )
                        it.set(receivedActivityRef, receivedActivity)
                    }
                }.await()
            true
        } catch (e: Exception) {
            println("Error saving reply: $e")
            false
        }
    }

    suspend fun getRepliesForDiary(diaryId: String): List<Reply> =
        try {
            db
                .collection(REPLIES_COLLECTION)
                .whereEqualTo("originalDiaryId", diaryId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(Reply::class.java)
        } catch (e: Exception) {
            println("Error getting replies: $e")
            emptyList()
        }

    suspend fun getMyDiaries(): List<Diary> {
        val currentUser = auth.currentUser ?: return emptyList()
        return try {
            val result =
                db
                    .collection(DIARIES_COLLECTION)
                    .whereEqualTo("authorId", currentUser.uid)
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()
            result.toObjects(Diary::class.java)
        } catch (e: Exception) {
            println("Error loading my diaries: $e")
            emptyList()
        }
    }

    suspend fun getMyDiariesForMonth(
        year: Int,
        month: Int,
    ): List<Diary> {
        val currentUser = auth.currentUser ?: return emptyList()
        val startDate = Date.from(LocalDate.of(year, month, 1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())
        val endDate =
            Date.from(
                LocalDate
                    .of(year, month, 1)
                    .plusMonths(1)
                    .atStartOfDay(java.time.ZoneId.systemDefault())
                    .toInstant(),
            )

        return try {
            db
                .collection(DIARIES_COLLECTION)
                .whereEqualTo("authorId", currentUser.uid)
                .whereGreaterThanOrEqualTo("timestamp", startDate)
                .whereLessThan("timestamp", endDate)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Diary::class.java)
        } catch (e: Exception) {
            println("Error loading my diaries for month: $e")
            emptyList()
        }
    }

    private suspend fun fetchRandomDiary(myUid: String): Diary? {
        val randomId = db.collection(DIARIES_COLLECTION).document().id

        val query1 =
            db
                .collection(DIARIES_COLLECTION)
                .whereGreaterThanOrEqualTo(
                    com.google.firebase.firestore.FieldPath
                        .documentId(),
                    randomId,
                ).limit(10)

        val diaries1 = query1.get().await().toObjects(Diary::class.java)
        diaries1.firstOrNull { it.authorId != myUid }?.let { return it }

        val query2 =
            db
                .collection(DIARIES_COLLECTION)
                .whereLessThan(
                    com.google.firebase.firestore.FieldPath
                        .documentId(),
                    randomId,
                ).limit(10)

        val diaries2 = query2.get().await().toObjects(Diary::class.java)
        return diaries2.firstOrNull { it.authorId != myUid }
    }

    private suspend fun hasUserReadDiary(
        userId: String,
        diaryId: String,
    ): Boolean =
        try {
            val readReceiptId = "${userId}_$diaryId"
            val document =
                db
                    .collection(READ_DIARIES_COLLECTION)
                    .document(readReceiptId)
                    .get()
                    .await()
            document.exists()
        } catch (e: Exception) {
            println("Error checking if diary was read: $e")
            false
        }

    private suspend fun markDiaryAsRead(
        userId: String,
        diaryId: String,
    ) {
        val readReceiptId = "${userId}_$diaryId"
        val readData =
            mapOf(
                "userId" to userId,
                "diaryId" to diaryId,
                "timestamp" to FieldValue.serverTimestamp(),
            )
        try {
            db
                .collection(READ_DIARIES_COLLECTION)
                .document(readReceiptId)
                .set(readData)
                .await()
        } catch (e: Exception) {
            println("Error marking diary as read: $e")
        }
    }
}
