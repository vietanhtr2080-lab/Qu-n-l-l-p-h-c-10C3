package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.components.EmptyStateView
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.RoyalBluePrimary
import com.example.viewmodel.ClassTab

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    students: List<Student>,
    records: List<ConductRecord>,
    settings: ClassSettings,
    selectedMonth: String,
    onNavigateTab: (ClassTab) -> Unit,
    onSeedData: () -> Unit,
    onQuickAddStudent: () -> Unit,
    onQuickAddRecord: () -> Unit
) {
    val monthRecords = records.filter { it.month == selectedMonth || it.date.startsWith(selectedMonth) }
    val totalPlus = monthRecords.filter { it.type == "PLUS" }.sumOf { it.points }
    val totalMinus = monthRecords.filter { it.type == "MINUS" }.sumOf { kotlin.math.abs(it.points) }
    val violations = monthRecords.filter { it.type == "VIOLATION" }
    val unresolvedViolations = violations.count { !it.resolved }

    val maleCount = students.count { it.gender == "Nam" }
    val femaleCount = students.count { it.gender == "Nữ" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LỚP ${settings.className} • NĂM HỌC ${settings.academicYear}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Chào mừng ${settings.teacherName}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tháng hoạt động: $selectedMonth • Báo cáo nề nếp và thi đua",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    if (students.isEmpty()) {
                        Button(
                            onClick = onSeedData,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Nạp dữ liệu mẫu 10C3")
                        }
                    }
                }
            }
        }

        // Top 4 Metrics Cards
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = 4
            ) {
                val cardModifier = Modifier.weight(1f)

                StatCard(
                    title = "Tổng sĩ số lớp",
                    value = "${students.size} HS",
                    subtitle = "$maleCount Nam • $femaleCount Nữ",
                    icon = Icons.Default.People,
                    accentColor = RoyalBluePrimary,
                    modifier = cardModifier
                )

                StatCard(
                    title = "Điểm thi đua tháng",
                    value = "+$totalPlus / -$totalMinus",
                    subtitle = "Tổng điểm tích lũy",
                    icon = Icons.Default.Stars,
                    accentColor = EmeraldSecondary,
                    modifier = cardModifier
                )

                StatCard(
                    title = "Lỗi vi phạm tháng",
                    value = "${violations.size} vụ",
                    subtitle = if (unresolvedViolations > 0) "$unresolvedViolations chưa giải quyết" else "Đã giải quyết hết",
                    icon = Icons.Default.ReportProblem,
                    accentColor = if (unresolvedViolations > 0) CoralRed else EmeraldSecondary,
                    modifier = cardModifier
                )

                StatCard(
                    title = "Quy mô phân tổ",
                    value = "4 Tổ",
                    subtitle = "Tổ trưởng & ban cán sự",
                    icon = Icons.Default.Groups,
                    accentColor = AmberAccent,
                    modifier = cardModifier
                )
            }
        }

        // Quick Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onQuickAddStudent,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Thêm học sinh", fontSize = 13.sp)
                    }

                    Button(
                        onClick = onQuickAddRecord,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary)
                    ) {
                        Icon(Icons.Default.Stars, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ghi điểm thi đua", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { onNavigateTab(ClassTab.DISCIPLINE) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp), tint = CoralRed)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sổ lỗi", fontSize = 13.sp)
                    }
                }

                // New row for Role actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onNavigateTab(ClassTab.STUDENTS) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("👑 Giao vai trò cán bộ", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { onNavigateTab(ClassTab.GROUPS) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralRed)
                    ) {
                        Text("🎖️ Tổ trưởng trừ điểm", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { onNavigateTab(ClassTab.MERIT_POINTS) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldSecondary)
                    ) {
                        Text("🙋 HS báo điểm cộng", fontSize = 12.sp)
                    }
                }
            }
        }

        // Empty state check
        if (students.isEmpty()) {
            item {
                EmptyStateView(
                    title = "Lớp 10C3 chưa có dữ liệu học sinh",
                    description = "Bạn có thể tự nhập từng học sinh hoặc ấn 'Nạp dữ liệu mẫu 10C3' để khởi tạo nhanh 24 học sinh với đầy đủ 4 tổ và các bản ghi thi đua mẫu.",
                    icon = Icons.Default.Groups,
                    actionButtonText = "Nạp nhanh dữ liệu lớp 10C3",
                    onActionClick = onSeedData
                )
            }
        } else {
            // Group comparison section
            item {
                SectionHeader(
                    title = "Thi đua 4 Tổ trong tháng $selectedMonth",
                    subtitle = "So sánh điểm tích lũy và số lỗi giữa các tổ"
                ) {
                    Text(
                        text = "Xem chi tiết tổ →",
                        style = MaterialTheme.typography.labelLarge,
                        color = RoyalBluePrimary,
                        modifier = Modifier.clickable { onNavigateTab(ClassTab.GROUPS) }
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        (1..4).forEach { grpNum ->
                            val grpStudents = students.filter { it.groupNumber == grpNum }
                            val grpIds = grpStudents.map { it.id }.toSet()
                            val grpRecords = monthRecords.filter { it.studentId in grpIds }
                            val grpPlus = grpRecords.filter { it.type == "PLUS" }.sumOf { it.points }
                            val grpMinus = grpRecords.filter { it.type == "MINUS" }.sumOf { kotlin.math.abs(it.points) }
                            val grpViolations = grpRecords.count { it.type == "VIOLATION" }
                            val netScore = (grpPlus - grpMinus).coerceAtLeast(0)

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "TỔ $grpNum (${grpStudents.size} thành viên)",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "+$grpPlus điểm",
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldSecondary
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "$grpViolations lỗi",
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (grpViolations > 0) CoralRed else MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { (netScore / 50f).coerceIn(0.05f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = when (grpNum) {
                                        1 -> RoyalBluePrimary
                                        2 -> EmeraldSecondary
                                        3 -> AmberAccent
                                        else -> CoralRed
                                    },
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Highlights: Top students & Recent violations
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top 3 positive students
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = AmberAccent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Top Tuyên Dương",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Text(
                                    text = "Xem hết",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = RoyalBluePrimary,
                                    modifier = Modifier.clickable { onNavigateTab(ClassTab.HONOR_ROLL) }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val topStudents = students.map { st ->
                                val pts = monthRecords.filter { it.studentId == st.id && it.type == "PLUS" }.sumOf { it.points }
                                st to pts
                            }.filter { it.second > 0 }.sortedByDescending { it.second }.take(3)

                            if (topStudents.isEmpty()) {
                                Text(
                                    text = "Chưa có điểm cộng trong tháng",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            } else {
                                topStudents.forEachIndexed { idx, (st, pts) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (idx == 0) AmberAccent else RoyalBluePrimary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${idx + 1}",
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = st.fullName,
                                                    fontWeight = FontWeight.Medium,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Text(
                                                    text = "Tổ ${st.groupNumber}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        }
                                        Text(
                                            text = "+$pts đ",
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Warning / Frequent violations card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = CoralRed,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Cần Nhắc Nhở",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Text(
                                    text = "Xem hết",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = RoyalBluePrimary,
                                    modifier = Modifier.clickable { onNavigateTab(ClassTab.WARNINGS) }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val warned = students.map { st ->
                                val viols = monthRecords.filter { it.studentId == st.id && it.type == "VIOLATION" }
                                st to viols
                            }.filter { it.second.isNotEmpty() }.sortedByDescending { it.second.size }.take(3)

                            if (warned.isEmpty()) {
                                Text(
                                    text = "Không có học sinh vi phạm nhiều lần",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldSecondary
                                )
                            } else {
                                warned.forEach { (st, viols) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = st.fullName,
                                                fontWeight = FontWeight.Medium,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "Tổ ${st.groupNumber} • Lỗi mới nhất: ${viols.lastOrNull()?.category ?: ""}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                        Text(
                                            text = "${viols.size} lỗi",
                                            fontWeight = FontWeight.Bold,
                                            color = CoralRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
