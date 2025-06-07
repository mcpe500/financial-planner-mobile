package com.example.financialplannerapp.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.financialplannerapp.service.LocalAppContainer
import com.example.financialplannerapp.ui.viewmodel.CalendarViewModelFactory

data class CalendarUiState(
    val currentMonth: String = "",
    val days: List<CalendarDay> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BillCalendarScreen(navController: NavHostController) {
    val appContainer = LocalAppContainer.current
    val calendarViewModel: CalendarViewModel = viewModel(factory = CalendarViewModelFactory())
    val calendarUiState by calendarViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(calendarUiState.currentMonth) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            CalendarView(
                calendarUiState = calendarUiState,
                onDateSelected = { day ->
                    // TODO: Handle date selection, e.g., show bills for this day or navigate
                },
                onPreviousMonthClicked = { calendarViewModel.previousMonth() },
                onNextMonthClicked = { calendarViewModel.nextMonth() }
            )
            // TODO: Add UI for displaying bills for the selected date
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    calendarUiState: CalendarUiState,
    onDateSelected: (CalendarDay) -> Unit,
    onPreviousMonthClicked: () -> Unit,
    onNextMonthClicked: () -> Unit
) {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonthClicked) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous Month")
            }
            Text(
                text = calendarUiState.currentMonth,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            IconButton(onClick = onNextMonthClicked) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Next Month")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { dayLabel ->
                Text(
                    text = dayLabel,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(calendarUiState.days) { day ->
                CalendarDayView(day = day, onDateSelected = onDateSelected)
            }
        }
    }
}

@Composable
fun CalendarDayView(day: CalendarDay, onDateSelected: (CalendarDay) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    day.isToday -> MaterialTheme.colorScheme.primaryContainer
                    day.isCurrentMonth -> MaterialTheme.colorScheme.surface
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = day.isCurrentMonth) { onDateSelected(day) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            color = if (day.isCurrentMonth) MaterialTheme.colorScheme.onSurface else Color.Gray,
            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberCalendarState(): CalendarViewModel {
    return viewModel(factory = CalendarViewModelFactory())
}

@RequiresApi(Build.VERSION_CODES.O)
class CalendarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private var calendar: Calendar = Calendar.getInstance()

    init {
        updateCalendarData()
    }

    fun updateCalendarData() {
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        _uiState.value = CalendarUiState(
            currentMonth = monthFormat.format(calendar.time),
            days = generateCalendarDaysForMonth(calendar)
        )
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        updateCalendarData()
    }

    fun previousMonth() {
        calendar.add(Calendar.MONTH, -1)
        updateCalendarData()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateCalendarDaysForMonth(currentMonthCalendar: Calendar): List<CalendarDay> {
    val days = mutableListOf<CalendarDay>()
    val today = Calendar.getInstance()

    val monthCalendar = currentMonthCalendar.clone() as Calendar
    monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeekInMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)

    var daysToPrepend = firstDayOfWeekInMonth - monthCalendar.firstDayOfWeek
    if (daysToPrepend < 0) {
        daysToPrepend += 7
    }

    val prevMonthCalendarDisplay = monthCalendar.clone() as Calendar
    prevMonthCalendarDisplay.add(Calendar.DAY_OF_MONTH, -daysToPrepend)

    for (i in 0 until daysToPrepend) {
        days.add(
            CalendarDay(
                dayOfMonth = prevMonthCalendarDisplay.get(Calendar.DAY_OF_MONTH),
                month = prevMonthCalendarDisplay.get(Calendar.MONTH),
                year = prevMonthCalendarDisplay.get(Calendar.YEAR),
                isToday = false,
                isCurrentMonth = false
            )
        )
        prevMonthCalendarDisplay.add(Calendar.DAY_OF_MONTH, 1)
    }

    monthCalendar.time = currentMonthCalendar.time
    monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
    val currentMonth = monthCalendar.get(Calendar.MONTH)
    val currentYear = monthCalendar.get(Calendar.YEAR)
    val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    for (dayOfMonth in 1..daysInMonth) {
        val isTodayFlag = (today.get(Calendar.DAY_OF_MONTH) == dayOfMonth &&
                           today.get(Calendar.MONTH) == currentMonth &&
                           today.get(Calendar.YEAR) == currentYear)
        days.add(
            CalendarDay(
                dayOfMonth = dayOfMonth,
                month = currentMonth,
                year = currentYear,
                isToday = isTodayFlag,
                isCurrentMonth = true
            )
        )
    }

    val daysSoFar = days.size
    val nextMonthCalendarDisplay = monthCalendar.clone() as Calendar
    nextMonthCalendarDisplay.add(Calendar.MONTH, 1)
    nextMonthCalendarDisplay.set(Calendar.DAY_OF_MONTH, 1)

    for (i in 0 until (42 - daysSoFar)) {
        days.add(
            CalendarDay(
                dayOfMonth = nextMonthCalendarDisplay.get(Calendar.DAY_OF_MONTH),
                month = nextMonthCalendarDisplay.get(Calendar.MONTH),
                year = nextMonthCalendarDisplay.get(Calendar.YEAR),
                isToday = false,
                isCurrentMonth = false
            )
        )
        nextMonthCalendarDisplay.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days.take(42)
}

