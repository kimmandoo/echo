package echo.kimmandoo.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserData(
    val freeLetterCount: Long = 0,
    val currency: Long = 0,
)

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    companion object {
        private const val USERS_COLLECTION = "users"
    }

    fun listenToUserData(): Flow<UserData> =
        callbackFlow {
            val uid =
                auth.currentUser?.uid ?: run {
                    close(IllegalStateException("User not logged in"))
                    return@callbackFlow
                }

            val userRef = db.collection(USERS_COLLECTION).document(uid)

            val listener =
                userRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val userData = snapshot.toObject(UserData::class.java) ?: UserData()
                        trySend(userData).isSuccess
                    } else {
                        trySend(UserData()).isSuccess // 문서가 없으면 기본값 전송
                    }
                }

            awaitClose { listener.remove() }
        }

    suspend fun addDailyFreeChanceIfNeeded() {
        val uid = auth.currentUser?.uid ?: return
        val userRef = db.collection(USERS_COLLECTION).document(uid)
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        try {
            val doc = userRef.get().await()
            if (!doc.exists()) {
                // 사용자가 없으면 새로 생성
                userRef
                    .set(
                        mapOf(
                            "freeLetterCount" to 1,
                            "currency" to 0,
                            "lastFreeChanceDate" to todayStr,
                        ),
                    ).await()
            } else {
                val lastDate = doc.getString("lastFreeChanceDate")
                if (lastDate != todayStr) {
                    // 마지막 지급일이 오늘이 아니면 무료 기회 1개 지급
                    userRef
                        .update(
                            mapOf(
                                "freeLetterCount" to 1,
                                "lastFreeChanceDate" to todayStr,
                            ),
                        ).await()
                }
            }
        } catch (e: Exception) {
            println("Error adding daily free chance: $e")
        }
    }
}
