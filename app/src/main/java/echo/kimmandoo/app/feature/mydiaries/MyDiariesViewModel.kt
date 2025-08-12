package echo.kimmandoo.app.feature.mydiaries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import echo.kimmandoo.app.data.DiaryRepository
import echo.kimmandoo.app.feature.diary.model.Diary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class MyDiariesUiState(
    val diaries: List<Diary> = emptyList(),
    val isLoading: Boolean = false,
    val selectedYear: Int = LocalDate.now().year,
    val selectedMonth: Int = LocalDate.now().monthValue,
)

class MyDiariesViewModel(
    private val repository: DiaryRepository = DiaryRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyDiariesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMyDiariesForMonth(YearMonth.now())
    }

    fun onMonthChange(yearMonth: YearMonth) {
        _uiState.update { it.copy(selectedYear = yearMonth.year, selectedMonth = yearMonth.monthValue) }
        loadMyDiariesForMonth(yearMonth)
    }

    private fun loadMyDiariesForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val diaries = repository.getMyDiariesForMonth(yearMonth.year, yearMonth.monthValue)
            _uiState.update { it.copy(diaries = diaries, isLoading = false) }
        }
    }
}
