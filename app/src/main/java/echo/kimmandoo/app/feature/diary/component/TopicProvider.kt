package echo.kimmandoo.app.feature.diary.component

import java.util.Calendar

object TopicProvider {
    private val topics =
        listOf(
            "오늘 하루, 가장 감사했던 일은 무엇인가요?",
            "최근 당신을 웃게 만든 순간을 공유해주세요.",
            "어린 시절, 가장 좋아했던 장소는 어디였나요?",
            "요즘 가장 많이 듣는 노래는 무엇인가요?",
            "10년 후의 나에게 짧은 편지를 써보세요.",
            "오늘 먹었던 음식 중 가장 인상 깊었던 것은?",
            "최근에 본 영화나 드라마 중 추천하고 싶은 것이 있나요?",
            "당신에게 '행복'이란 무엇인가요?",
            "스트레스를 푸는 당신만의 방법이 있나요?",
            "내일 하루, 꼭 하고 싶은 한 가지가 있다면?",
            // 필요에 따라 주제를 더 추가할 수 있습니다.
        )

    fun getTodayTopic(): String {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return topics[dayOfYear % topics.size]
    }
}