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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun DisciplineBookScreen(
    students: List<Student>,
    records: List<ConductRecord>,
    selectedMonth: String,
    onSaveRecord: (ConductRecord) -> Unit,
    onToggleResolved: (ConductRecord) -> Unit,
    onDeleteRecord: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Unresolved, 2: Resolved
    var showAddDialog by remember { mutableStateOf(false) }
    var showGroupLeaderDialog by remember { mutableStateOf(false) }

    val monthViolations = records.filter {
        (it.month == selectedMonth || it.date.startsWith(selectedMonth)) && it.type == "VIOLATION"
    }

    val filteredList = when (selectedTab) {
        1 -> monthViolations.filter { !it.resolved }
        2 -> monthViolations.filter { it.resolved }
        else -> monthViolations
    }.sortedByDescending { it.date }

    val unresolvedCount = monthViolations.count { !it.resolved }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SỔ LỖI VI PHẠM KỶ LUẬT",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tháng: $selectedMonth • $unresolvedCount vi phạm chưa giải quyết",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (unresolvedCount > 0) CoralRed else EmeraldSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showGroupLeaderDialog = true },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tổ trưởng trừ điểm")
                }

                OutlinedButton(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("GVCN ghi lỗi")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = RoyalBluePrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Tất cả (${monthViolations.size})") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Chưa giải quyết ($unresolvedCount)") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Đã giải quyết (${monthViolations.size - unresolvedCount})") }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredList.isEmpty()) {
            EmptyStateView(
                title = "Không có vi phạm nào",
                description = if (selectedTab == 1) "Tuyệt vời! Tất cả vi phạm trong tháng đã được xử lý xong."
                else "Chưa có vi phạm nào được ghi nhận cho lớp 10C3 trong tháng $selectedMonth.",
                icon = Icons.Default.CheckCircle,
                actionButtonText = "Ghi nhận vi phạm",
                onActionClick = { showAddDialog = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { viol ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (viol.resolved) EmeraldSecondary.copy(alpha = 0.15f) else Color(0xFFFFEBEE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (viol.resolved) Icons.Default.CheckCircle else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (viol.resolved) EmeraldSecondary else CoralRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = viol.studentName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Tổ ${viol.groupNumber} • Ngày: ${viol.date}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${viol.points} đ",
                                        fontWeight = FontWeight.Bold,
                                        color = CoralRed
                                    )
                                    IconButton(onClick = { onDeleteRecord(viol.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Details
                            Text(
                                text = "Hành vi: ${viol.category}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = CoralRed
                            )

                            if (viol.description.isNotBlank()) {
                                Text(
                                    text = "Mô tả: ${viol.description}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (viol.actionTaken.isNotBlank()) {
                                Text(
                                    text = "Biện pháp xử lý: ${viol.actionTaken}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = RoyalBluePrimary
                                )
                            }

                            if (viol.recordedBy.isNotBlank()) {
                                Text(
                                    text = "Người ghi nhận: ${viol.recordedBy}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (viol.recordedBy.startsWith("Tổ trưởng")) Color(0xFFE65100) else RoyalBluePrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action button to toggle resolved status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (viol.resolved) EmeraldSecondary.copy(alpha = 0.15f) else Color(0xFFFFF3E0),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (viol.resolved) "✓ ĐÃ GIẢI QUYẾT" else "⚠ CHƯA GIẢI QUYẾT",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (viol.resolved) EmeraldSecondary else Color(0xFFE65100)
                                    )
                                }

                                OutlinedButton(
                                    onClick = { onToggleResolved(viol) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (viol.resolved) "Đánh dấu chưa xong" else "Đánh dấu đã khắc phục",
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        DisciplineRecordDialog(
            students = students,
            selectedMonth = selectedMonth,
            onDismiss = { showAddDialog = false },
            onSave = {
                onSaveRecord(it)
                showAddDialog = false
            }
        )
    }

    if (showGroupLeaderDialog) {
        GroupLeaderDirectDemeritDialog(
            students = students,
            selectedMonth = selectedMonth,
            onDismiss = { showGroupLeaderDialog = false },
            onSave = {
                onSaveRecord(it)
                showGroupLeaderDialog = false
            }
        )
    }
}

@Composable
fun GroupLeaderDirectDemeritDialog(
    students: List<Student>,
    selectedMonth: String,
    onDismiss: () -> Unit,
    onSave: (ConductRecord) -> Unit
) {
    var selectedGroup by remember { mutableIntStateOf(1) }
    val groupMembers = remember(selectedGroup, students) {
        students.filter { it.groupNumber == selectedGroup }
    }
    var selectedStudent by remember(groupMembers) { mutableStateOf(groupMembers.firstOrNull()) }
    var category by remember { mutableStateOf("Không làm bài tập về nhà") }
    var points by remember { mutableIntStateOf(-2) }
    var description by remember { mutableStateOf("") }
    var actionTaken by remember { mutableStateOf("Ghi sổ theo dõi tổ & nhắc nhở") }
    var date by remember { mutableStateOf("$selectedMonth-10") }
    var expandedDropdown by remember { mutableStateOf(false) }

    val groupDemeritOptions = listOf(
        "Không làm bài tập về nhà" to -2,
        "Quên sách vở / đồ dùng học tập" to -2,
        "Nói chuyện riêng / mất trật tự" to -2,
        "Đi học muộn" to -3,
        "Không thuộc bài kiểm tra miệng" to -5,
        "Trực nhật tổ chưa sạch sẽ" to -2,
        "Vi phạm đồng phục / tác phong" to -2
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "TỔ TRƯỞNG NHẬP ĐIỂM TRỪ THÀNH VIÊN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFE65100)
                )
                Text(
                    text = "Chỉ chấm điểm trừ cho các bạn trong cùng tổ phụ trách",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("Em là Tổ trưởng Tổ mấy?", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..4).forEach { grp ->
                            FilterChip(
                                selected = selectedGroup == grp,
                                onClick = {
                                    selectedGroup = grp
                                    selectedStudent = students.find { it.groupNumber == grp }
                                },
                                label = { Text("Tổ $grp") }
                            )
                        }
                    }
                }

                item {
                    Text("Học sinh vi phạm (Thành viên Tổ $selectedGroup):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedStudent?.fullName ?: "Chưa có thành viên",
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
                            groupMembers.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text("${st.fullName} (${st.studentCode})") },
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
                    Text("Lỗi vi phạm thường gặp trong tổ:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        groupDemeritOptions.forEach { (cat, pts) ->
                            FilterChip(
                                selected = category == cat,
                                onClick = {
                                    category = cat
                                    points = pts
                                },
                                label = { Text("$cat ($pts đ)", fontSize = 12.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Lỗi vi phạm cụ thể") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = points.toString(),
                        onValueChange = { points = it.toIntOrNull() ?: -2 },
                        label = { Text("Điểm trừ (số âm, VD: -2)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Mô tả chi tiết vi phạm") },
                        placeholder = { Text("VD: Không làm bài tập Toán trang 45") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = actionTaken,
                        onValueChange = { actionTaken = it },
                        label = { Text("Biện pháp xử lý của Tổ trưởng") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Ngày vi phạm (YYYY-MM-DD)") },
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
                        val record = ConductRecord(
                            id = "",
                            studentId = st.id,
                            studentName = st.fullName,
                            groupNumber = st.groupNumber,
                            type = "VIOLATION",
                            category = category.trim(),
                            points = -kotlin.math.abs(points),
                            description = description.trim(),
                            actionTaken = actionTaken.trim(),
                            date = date.trim(),
                            month = selectedMonth,
                            resolved = false,
                            recordedBy = "Tổ trưởng Tổ $selectedGroup"
                        )
                        onSave(record)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
            ) {
                Text("Ghi điểm trừ")
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
fun DisciplineRecordDialog(
    students: List<Student>,
    selectedMonth: String,
    onDismiss: () -> Unit,
    onSave: (ConductRecord) -> Unit
) {
    var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
    var category by remember { mutableStateOf("Đi học muộn") }
    var points by remember { mutableIntStateOf(-3) }
    var description by remember { mutableStateOf("") }
    var actionTaken by remember { mutableStateOf("Nhắc nhở trước lớp") }
    var date by remember { mutableStateOf("$selectedMonth-10") }
    var expandedStudentDropdown by remember { mutableStateOf(false) }

    val violationCategories = listOf(
        "Đi học muộn" to -3,
        "Không thuộc bài / Không soạn bài" to -5,
        "Vi phạm đồng phục / Tác phong" to -2,
        "Sử dụng điện thoại trong giờ" to -5,
        "Nói chuyện riêng / Làm việc riêng" to -2,
        "Nghỉ học không phép" to -10,
        "Gây mất trật tự nghiêm trọng" to -5
    )

    val actionOptions = listOf(
        "Nhắc nhở trước lớp",
        "Chép phạt bài học",
        "Viết bản kiểm điểm",
        "Trực nhật phạt cuối tuần",
        "Thông báo phụ huynh",
        "Mời phụ huynh lên trường"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ghi nhận lỗi vi phạm kỷ luật") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("Học sinh vi phạm *", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
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
                    Text("Hành vi vi phạm *", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        violationCategories.forEach { (cat, pts) ->
                            FilterChip(
                                selected = category == cat,
                                onClick = {
                                    category = cat
                                    points = pts
                                },
                                label = { Text("$cat ($pts đ)", fontSize = 12.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Chi tiết hành vi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = points.toString(),
                        onValueChange = { points = it.toIntOrNull() ?: -3 },
                        label = { Text("Điểm trừ thi đua") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("Biện pháp xử lý:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        actionOptions.forEach { act ->
                            FilterChip(
                                selected = actionTaken == act,
                                onClick = { actionTaken = act },
                                label = { Text(act, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = actionTaken,
                        onValueChange = { actionTaken = it },
                        label = { Text("Nhập biện pháp xử lý khác") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Mô tả hoàn cảnh / lời khai") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Ngày vi phạm (YYYY-MM-DD)") },
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
                            type = "VIOLATION",
                            category = category.trim(),
                            points = -kotlin.math.abs(points),
                            description = description.trim(),
                            actionTaken = actionTaken.trim(),
                            date = date.trim(),
                            month = selectedMonth,
                            resolved = false
                        )
                        onSave(rec)
                    }
                },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = CoralRed)
            ) {
                Text("Ghi vào sổ lỗi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
