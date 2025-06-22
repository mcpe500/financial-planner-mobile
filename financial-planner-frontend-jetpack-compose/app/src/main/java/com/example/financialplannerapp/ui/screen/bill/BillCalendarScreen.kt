package com.example.financialplannerapp.ui.screen.bill

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financialplannerapp.data.model.RecurringBill
import com.example.financialplannerapp.data.model.RepeatCycle
import com.example.financialplannerapp.data.model.BillStatus
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financialplannerapp.ui.viewmodel.BillViewModel
import com.example.financialplannerapp.data.model.BillPayment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.navigation.NavController
import com.example.financialplannerapp.MainApplication
import com.example.financialplannerapp.ui.viewmodel.BillViewModelFactory
import androidx.compose.ui.platform.LocalContext
import com.example.financialplannerapp.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillCalendarScreen(navController: NavController, tokenManager: TokenManager) {
    val context = LocalContext.current
    val application = context.applicationContext as MainApplication
    val billViewModel: BillViewModel = viewModel(
        factory = BillViewModelFactory(application.appContainer.billRepository, tokenManager)
    )
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }

    // Colors
    val BibitGreen = Color(0xFF4CAF50)
    val BibitDarkGreen = Color(0xFF2E7D32)
    val LightGreen = Color(0xFFE8F5E8)

    val billEntities by billViewModel.localBills.collectAsState()
    val bills = billEntities.map { entity ->
        RecurringBill(
            id = entity.uuid,
            name = entity.name,
            estimatedAmount = entity.estimatedAmount,
            dueDate = entity.dueDate,
            repeatCycle = entity.repeatCycle,
            category = entity.category,
            notes = entity.notes,
            isActive = entity.isActive,
            payments = Gson().fromJson<List<BillPayment>>(entity.paymentsJson, object : TypeToken<List<BillPayment>>() {}.type) ?: emptyList(),
            autoPay = entity.autoPay,
            notificationEnabled = entity.notificationEnabled,
            lastPaymentDate = entity.lastPaymentDate,
            creationDate = entity.creationDate
        )
    }

    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
    val dayFormat = SimpleDateFormat("d", Locale("id", "ID"))
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    // Get bills for selected date
    val selectedDateBills = selectedDate?.let { date ->
        bills.filter { bill ->
            val billCalendar = Calendar.getInstance().apply { time = bill.dueDate }
            val selectedCalendar = Calendar.getInstance().apply { time = date }
            billCalendar.get(Calendar.DAY_OF_MONTH) == selectedCalendar.get(Calendar.DAY_OF_MONTH) &&
                    billCalendar.get(Calendar.MONTH) == selectedCalendar.get(Calendar.MONTH) &&
                    billCalendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR)
        }
    } ?: emptyList()

    // Get calendar days
    val calendarDays = remember(currentMonth) {
        generateCalendarDays(currentMonth)
    }

    // Get bills for each day
    val billsByDate = remember(bills, currentMonth) {
        bills.groupBy { bill ->
            val calendar = Calendar.getInstance().apply { time = bill.dueDate }
            calendar.get(Calendar.DAY_OF_MONTH)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Kalender Tagihan",
                        fontWeight = FontWeight.Bold,
                        color = BibitDarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = BibitGreen
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("add_bill") }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Tambah Tagihan",
                            tint = BibitGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Month Navigation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            currentMonth = Calendar.getInstance().apply {
                                time = currentMonth.time
                                add(Calendar.MONTH, -1)
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Bulan Sebelumnya",
                            tint = BibitDarkGreen
                        )
                    }

                    Text(
                        text = monthFormat.format(currentMonth.time),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BibitDarkGreen
                    )

                    IconButton(
                        onClick = {
                            currentMonth = Calendar.getInstance().apply {
                                time = currentMonth.time
                                add(Calendar.MONTH, 1)
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Bulan Berikutnya",
                            tint = BibitDarkGreen
                        )
                    }
                }

                // Monthly Summary
                val monthlyTotal = bills.sumOf { it.estimatedAmount }
                val monthlyCount = bills.size

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = monthlyCount.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BibitDarkGreen
                        )
                        Text(
                            text = "Tagihan",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currencyFormat.format(monthlyTotal).replace("Rp", "").trim(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BibitDarkGreen
                        )
                        Text(
                            text = "Total Estimasi",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            // Calendar Grid
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Day Headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach { day ->
                            Text(
                                text = day,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF666666),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar Days Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.height(240.dp)
                    ) {
                        items(calendarDays) { day ->
                            CalendarDayCell(
                                day = day,
                                bills = billsByDate[day.dayOfMonth] ?: emptyList(),
                                isSelected = selectedDate?.let { selected ->
                                    val selectedCalendar = Calendar.getInstance().apply { time = selected }
                                    selectedCalendar.get(Calendar.DAY_OF_MONTH) == day.dayOfMonth &&
                                            selectedCalendar.get(Calendar.MONTH) == day.month &&
                                            selectedCalendar.get(Calendar.YEAR) == day.year
                                } ?: false,
                                isToday = day.isToday,
                                isCurrentMonth = day.isCurrentMonth,
                                onClick = {
                                    selectedDate = Calendar.getInstance().apply {
                                        set(Calendar.YEAR, day.year)
                                        set(Calendar.MONTH, day.month)
                                        set(Calendar.DAY_OF_MONTH, day.dayOfMonth)
                                    }.time
                                }
                            )
                        }
                    }
                }
            }

            // Selected Date Bills
            if (selectedDate != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Tagihan ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(selectedDate!!)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BibitDarkGreen
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (selectedDateBills.isEmpty()) {
                            Text(
                                text = "Tidak ada tagihan pada tanggal ini",
                                fontSize = 14.sp,
                                color = Color(0xFF666666),
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.heightIn(max = 200.dp)
                            ) {
                                items(selectedDateBills) { bill ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { navController.navigate("bill_details/${bill.id}") }
                                            .background(
                                                color = Color(0xFFF8F9FA),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = bill.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = currencyFormat.format(bill.estimatedAmount),
                                                fontSize = 12.sp,
                                                color = Color(0xFF666666)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = bill.status.color.copy(alpha = 0.1f),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = bill.status.label,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = bill.status.color
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Total: ${currencyFormat.format(selectedDateBills.sumOf { it.estimatedAmount })}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BibitDarkGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

data class CalendarDay(
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean
)

@Composable
fun CalendarDayCell(
    day: CalendarDay,
    bills: List<RecurringBill>,
    isSelected: Boolean,
    isToday: Boolean,
    isCurrentMonth: Boolean,
    onClick: () -> Unit
) {
    val BibitGreen = Color(0xFF4CAF50)

    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable { onClick() }
            .background(
                color = when {
                    isSelected -> BibitGreen
                    isToday -> BibitGreen.copy(alpha = 0.2f)
                    else -> Color.Transparent
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                fontSize = 12.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected -> Color.White
                    !isCurrentMonth -> Color(0xFFCCCCCC)
                    isToday -> BibitGreen
                    else -> Color(0xFF333333)
                }
            )

            // Bill indicators
            if (bills.isNotEmpty() && isCurrentMonth) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    bills.take(3).forEach { bill ->
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .background(
                                    color = if (isSelected) Color.White else bill.status.color,
                                    shape = CircleShape
                                )
                        )
                    }
                    if (bills.size > 3) {
                        Text(
                            text = "+",
                            fontSize = 6.sp,
                            color = if (isSelected) Color.White else Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }
}

fun generateCalendarDays(currentMonth: Calendar): List<CalendarDay> {
    val days = mutableListOf<CalendarDay>()
    val today = Calendar.getInstance()

    // Get first day of month
    val firstDay = Calendar.getInstance().apply {
        time = currentMonth.time
        set(Calendar.DAY_OF_MONTH, 1)
    }

    // Get last day of month
    val lastDay = Calendar.getInstance().apply {
        time = currentMonth.time
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    }

    // Add days from previous month to fill the first week
    val firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK)
    val daysToAdd = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2

    for (i in daysToAdd downTo 1) {
        val prevDay = Calendar.getInstance().apply {
            time = firstDay.time
            add(Calendar.DAY_OF_MONTH, -i)
        }
        days.add(
            CalendarDay(
                dayOfMonth = prevDay.get(Calendar.DAY_OF_MONTH),
                month = prevDay.get(Calendar.MONTH),
                year = prevDay.get(Calendar.YEAR),
                isCurrentMonth = false,
                isToday = false
            )
        )
    }

    // Add days of current month
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    for (day in 1..daysInMonth) {
        val currentDay = Calendar.getInstance().apply {
            time = currentMonth.time
            set(Calendar.DAY_OF_MONTH, day)
        }

        val isToday = currentDay.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentDay.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                currentDay.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)

        days.add(
            CalendarDay(
                dayOfMonth = day,
                month = currentMonth.get(Calendar.MONTH),
                year = currentMonth.get(Calendar.YEAR),
                isCurrentMonth = true,
                isToday = isToday
            )
        )
    }

    // Add days from next month to fill the last week
    val remainingDays = 42 - days.size // 6 weeks * 7 days
    for (i in 1..remainingDays) {
        val nextDay = Calendar.getInstance().apply {
            time = lastDay.time
            add(Calendar.DAY_OF_MONTH, i)
        }
        days.add(
            CalendarDay(
                dayOfMonth = nextDay.get(Calendar.DAY_OF_MONTH),
                month = nextDay.get(Calendar.MONTH),
                year = nextDay.get(Calendar.YEAR),
                isCurrentMonth = false,
                isToday = false
            )
        )
    }

    return days
}

