package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConductRecord
import com.example.model.Student
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.RoyalBluePrimary

@Composable
fun MeritPointsScreen(
    students: List<Student>,
    records: List<ConductRecord>,
    selectedMonth: String,
    onSaveRecord: (ConductRecord) -> Unit,
    onDeleteRecord: (String) -> Unit
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: All, 1: Plus, 2: Minus
    var showAddDialog by remember { mutableStateOf(false) }
    var showStudentSelfDialog by remember { mutableStateOf(false) }

    val monthRecords = records.filter {
        (it.month == selectedMonth || it.date.startsWith(selectedMonth)) &&
                (it.type == "PLUS" || it.type == "MINUS")
    }

    val filteredRecords = when (selectedFilterIndex) {
        1 -> monthRecords.filter { it.type == "PLUS" }
        2 -> monthRecords.filter { it.type == "MINUS" }
        else -> monthRecords
    }.sortedByDescending { it.date }

    val totalPlus = monthRecords.filter { it.type == "PLUS" }.sumOf { it.points }
    val totalMinus = monthRecords.filter { it.type == "MINUS" }.sumOf { kotlin.math.abs(it.points) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SỔ ĐIỂM THI ĐUA CỘNG TRỪ",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tháng: $selectedMonth • Tổng cộng: +$totalPlus đ • Tổng trừ: -$totalMinus đ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Student self-submit & Teacher submit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showStudentSelfDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Học sinh tự nhập điểm cộng (+)")
                }

                OutlinedButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("GVCN ghi điểm")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Tabs
        TabRow(
            selectedTabIndex = selectedFilterIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = RoyalBluePrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedFilterIndex == 0,
                onClick = { selectedFilterIndex = 0 },
                text = { Text("Tất cả (${monthRecords.size})") }
            )
            Tab(
                selected = selectedFilterIndex == 1,
                onClick = { selectedFilterIndex = 1 },
                text = { Text("Điểm cộng (+)") }
            )
            Tab(
                selected = selectedFilterIndex == 2,
                onClick = { selectedFilterIndex = 2 },
                text = { Text("Điểm trừ (-)") }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredRecords.isEmpty()) {
            EmptyStateView(
                title = "Chưa có bản ghi điểm thi đua",
                description = "Hãy nhấn 'Ghi điểm' để cộng hoặc trừ điểm cho học sinh khi có thành tích hoặc vi phạm nhẹ.",
                icon = Icons.Default.Stars,
                actionButtonText = "Ghi điểm ngay",
                onActionClick = { showAddDialog = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredRecords, key = { it.id }) { rec ->
                    val isPlus = rec.type == "PLUS"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlus) EmeraldSecondary.copy(alpha = 0.15f) else Color(0xFFFFEBEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isPlus) "+${rec.points}" else "-${kotlin.math.abs(rec.points)}",
                                    color = if (isPlus) EmeraldSecondary else CoralRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = rec.studentName,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Tổ ${rec.groupNumber}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Text(
                                    text = rec.category,
                                    fontWeight = FontWeight.Medium,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isPlus) EmeraldSecondary else CoralRed
                                )
                                if (rec.description.isNotBlank()) {
                                    Text(
                                        text = rec.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "Ngày: ${rec.date}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                if (rec.recordedBy.isNotBlank()) {
                                    Text(
                                        text = "Người ghi: ${rec.recordedBy}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = if (rec.recordedBy.startsWith("Học sinh")) EmeraldSecondary else RoyalBluePrimary
                                    )
                                }
                            }

                            IconButton(onClick = { onDeleteRecord(rec.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = CoralRed)
                            }
                        }
                    }
                }
            }
        }
    }

    // Teacher Add Dialog
    if (showAddDialog) {
        MeritRecordDialog(
            students = students,
            selectedMonth = selectedMonth,
            onDismiss = { showAddDialog = false },
            onSave = {
                val withRecordedBy = it.copy(recordedBy = "GVCN")
                onSaveRecord(withRecordedBy)
                showAddDialog = false
            }
        )
    }

    // Student Self-Submit Plus Point Dialog
    if (showStudentSelfDialog) {
        StudentSelfPlusDialog(
            students = students,
            selectedMonth = selectedMonth,
            onDismiss = { showStudentSelfDialog = false },
            onSave = {
                onSaveRecord(it)
                showStudentSelfDialog = false
            }
        )
    }
}

@Composable
fun StudentSelfPlusDialog(
    students: List<Student>,
    selectedMonth: String,
    onDismiss: () -> Unit,
    onSave: (ConductRecord) -> Unit
) {
    var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
    var category by remember { mutableStateOf("Phát biểu xây dựng bài") }
    var points by remember { mutableIntStateOf(2) }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("$selectedMonth-10") }
    var expandedDropdown by remember { mutableStateOf(false) }

    val studentPlusReasons = listOf(
        "Phát biểu xây dựng bài tích cực" to 2,
        "Giải bài tập khó / làm bài trên bảng" to 3,
        "Đạt điểm kiểm tra 9 - 10" to 5,
        "Tham gia phong trào thi đua / văn nghệ" to 5,
        "Làm trực nhật xuất sắc, đúng giờ" to 2,
        "Giúp đỡ bạn học tập tiến bộ" to 3
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "HỌC SINH TỰ BÁO CÁO ĐIỂM CỘNG (+)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EmeraldSecondary
                )
                Text(
                    text = "Báo cáo thành tích nỗ lực để cộng điểm thi đua",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("Em là học sinh nào?", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedStudent?.let { "${it.fullName} (Tổ ${it.groupNumber})" } ?: "Chọn tên học sinh",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expandedDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            students.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text("${st.fullName} (Tổ ${st.groupNumber} - ${st.studentCode})") },
                                    onClick = {
                                        selectedStudent = st
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Chọn thành tích đạt được:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        studentPlusReasons.forEach { (cat, pts) ->
                            FilterChip(
                                selected = category == cat,
                                onClick = {
                                    category = cat
                                    points = pts
                                },
                                label = { Text("$cat (+$pts đ)", fontSize = 12.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Lý do chi tiết") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = points.toString(),
                        onValueChange = { points = it.toIntOrNull() ?: 2 },
                        label = { Text("Số điểm cộng (+)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Mô tả cụ thể / Tiết học / Môn học") },
                        placeholder = { Text("VD: Tiết 3 môn Toán, phát biểu bài hình học") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Ngày đạt thành tích (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val st = selectedStudent
                    if (st != null && category.isNotBlank()) {
                        val rec = ConductRecord(
                            id = "",
                            studentId = st.id,
                            studentName = st.fullName,
                            groupNumber = st.groupNumber,
                            type = "PLUS",
                            category = category.trim(),
                            points = kotlin.math.abs(points),
                            description = description.trim(),
                            date = date.trim(),
                            month = selectedMonth,
                            recordedBy = "Học sinh ${st.fullName}"
                        )
                        onSave(rec)
                    }
                },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = EmeraldSecondary)
            ) {
                Text("Gửi điểm cộng (+)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun MeritRecordDialog(
    students: List<Student>,
    selectedMonth: String,
    onDismiss: () -> Unit,
    onSave: (ConductRecord) -> Unit
) {
    var recordType by remember { mutableStateOf("PLUS") } // PLUS or MINUS
    var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
    var category by remember { mutableStateOf("Phát biểu xây dựng bài") }
    var points by remember { mutableIntStateOf(5) }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("$selectedMonth-10") }
    var expandedStudentDropdown by remember { mutableStateOf(false) }

    val quickPlusOptions = listOf(
        "Phát biểu xây dựng bài" to 2,
        "Điểm kiểm tra tốt (9-10)" to 5,
        "Tham gia phong trào thi đua" to 5,
        "Làm bài tập đầy đủ, tích cực" to 2,
        "Giúp đỡ bạn tiến bộ" to 3,
        "Trực nhật lớp sạch sẽ" to 2
    )

    val quickMinusOptions = listOf(
        "Không làm bài tập về nhà" to -2,
        "Quên đồ dùng học tập / sách vở" to -2,
        "Mất trật tự / nói chuyện riêng" to -2,
        "Không chú ý nghe giảng" to -2,
        "Trực nhật chưa sạch" to -2
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (recordType == "PLUS") "Ghi nhận Điểm Cộng (+)" else "Ghi nhận Điểm Trừ (-)") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                recordType = "PLUS"
                                category = "Phát biểu xây dựng bài"
                                points = 5
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = if (recordType == "PLUS") androidx.compose.foundation.BorderStroke(2.dp, EmeraldSecondary) else null
                        ) {
                            Text("Điểm cộng (+)", color = if (recordType == "PLUS") EmeraldSecondary else Color.Unspecified)
                        }

                        OutlinedButton(
                            onClick = {
                                recordType = "MINUS"
                                category = "Không làm bài tập về nhà"
                                points = 2
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = if (recordType == "MINUS") androidx.compose.foundation.BorderStroke(2.dp, CoralRed) else null
                        ) {
                            Text("Điểm trừ (-)", color = if (recordType == "MINUS") CoralRed else Color.Unspecified)
                        }
                    }
                }

                item {
                    Text("Chọn học sinh:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedStudent?.let { "${it.fullName} (Tổ ${it.groupNumber})" } ?: "Chọn học sinh",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expandedStudentDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expandedStudentDropdown,
                            onDismissRequest = { expandedStudentDropdown = false }
                        ) {
                            students.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text("${st.fullName} (Tổ ${st.groupNumber} - ${st.studentCode})") },
                                    onClick = {
                                        selectedStudent = st
                                        expandedStudentDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Lý do nhanh:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    val options = if (recordType == "PLUS") quickPlusOptions else quickMinusOptions
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        options.forEach { (cat, pts) ->
                            FilterChip(
                                selected = category == cat,
                                onClick = {
                                    category = cat
                                    points = kotlin.math.abs(pts)
                                },
                                label = { Text("$cat (${if (pts > 0) "+$pts" else "$pts"}đ)", fontSize = 12.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Nội dung / Lý do cụ thể") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = points.toString(),
                        onValueChange = { points = it.toIntOrNull() ?: 1 },
                        label = { Text("Số điểm") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Ghi chú chi tiết (nếu có)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Ngày ghi nhận (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val st = selectedStudent
                    if (st != null && category.isNotBlank()) {
                        val finalPoints = if (recordType == "PLUS") kotlin.math.abs(points) else -kotlin.math.abs(points)
                        val rec = ConductRecord(
                            id = "",
                            studentId = st.id,
                            studentName = st.fullName,
                            groupNumber = st.groupNumber,
                            type = recordType,
                            category = category.trim(),
                            points = finalPoints,
                            description = description.trim(),
                            date = date.trim(),
                            month = selectedMonth
                        )
                        onSave(rec)
                    }
                }
            ) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
