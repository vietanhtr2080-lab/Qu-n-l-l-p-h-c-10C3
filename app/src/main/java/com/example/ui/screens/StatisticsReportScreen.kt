package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClassSettings
import com.example.model.ConductRecord
import com.example.model.Student
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.RoyalBluePrimary

@Composable
fun StatisticsReportScreen(
    students: List<Student>,
    records: List<ConductRecord>,
    settings: ClassSettings,
    selectedMonth: String
) {
    val monthRecords = records.filter { it.month == selectedMonth || it.date.startsWith(selectedMonth) }
    val violations = monthRecords.filter { it.type == "VIOLATION" }
    val totalPlus = monthRecords.filter { it.type == "PLUS" }.sumOf { it.points }
    val totalMinus = monthRecords.filter { it.type == "MINUS" }.sumOf { kotlin.math.abs(it.points) }
    val resolvedViolations = violations.count { it.resolved }

    // Violations by category
    val categoryCounts = violations.groupBy { it.category }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "BÁO CÁO THỐNG KÊ LỚP 10C3",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Dữ liệu tổng hợp thời gian thực • Tháng $selectedMonth",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Summary Report Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Summarize, contentDescription = null, tint = RoyalBluePrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Báo cáo nề nếp tháng gửi Ban Giám Hiệu",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• Sĩ số lớp: ${students.size} học sinh (${students.count { it.gender == "Nam" }} Nam, ${students.count { it.gender == "Nữ" }} Nữ).\n" +
                                "• Tổng số điểm thưởng tích cực: +$totalPlus điểm.\n" +
                                "• Tổng số vi phạm nề nếp: ${violations.size} vụ việc.\n" +
                                "• Tỷ lệ xử lý và khắc phục: $resolvedViolations/${violations.size} vụ (${if (violations.isNotEmpty()) resolvedViolations * 100 / violations.size else 100}%).\n" +
                                "• Đánh giá chung: Lớp duy trì nề nếp học tập tốt, các tổ tích cực thi đua.",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Group Points Comparison
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = EmeraldSecondary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "So sánh điểm thi đua giữa 4 Tổ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    (1..4).forEach { grpNum ->
                        val grpStudents = students.filter { it.groupNumber == grpNum }
                        val grpIds = grpStudents.map { it.id }.toSet()
                        val grpRecords = monthRecords.filter { it.studentId in grpIds }
                        val plus = grpRecords.filter { it.type == "PLUS" }.sumOf { it.points }
                        val minus = grpRecords.filter { it.type == "MINUS" || it.type == "VIOLATION" }.sumOf { kotlin.math.abs(it.points) }
                        val net = (plus - minus).coerceAtLeast(0)

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Tổ $grpNum", fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = "+$plus đ / -$minus đ (Thuần: $net đ)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (net / 60f).coerceIn(0.05f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = when (grpNum) {
                                    1 -> RoyalBluePrimary
                                    2 -> EmeraldSecondary
                                    3 -> AmberAccent
                                    else -> CoralRed
                                }
                            )
                        }
                    }
                }
            }
        }

        // Violations Breakdown
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PieChart, contentDescription = null, tint = CoralRed, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Phân tích các hành vi vi phạm phổ biến",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (categoryCounts.isEmpty()) {
                        Text(
                            text = "Không có vi phạm nào trong tháng $selectedMonth",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldSecondary
                        )
                    } else {
                        categoryCounts.forEach { (cat, count) ->
                            val percent = if (violations.isNotEmpty()) count * 100 / violations.size else 0
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = cat, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = "$count vụ ($percent%)",
                                        fontWeight = FontWeight.Bold,
                                        color = CoralRed
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { percent / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = CoralRed,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
