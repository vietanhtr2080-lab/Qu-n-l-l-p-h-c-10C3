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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
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
import com.example.ui.components.StatCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.RoyalBluePrimary

@Composable
fun GroupManagementScreen(
    students: List<Student>,
    records: List<ConductRecord>,
    selectedMonth: String,
    onAssignRole: (Student, String, Int?) -> Unit,
    onGroupLeaderSubmitMinus: (leader: Student?, group: Int, target: Student, category: String, points: Int, desc: String, action: String, date: String) -> Unit
) {
    var selectedGroupTab by remember { mutableIntStateOf(1) }
    var roleStudentToAssign by remember { mutableStateOf<Student?>(null) }
    var showGroupLeaderDemeritDialog by remember { mutableStateOf(false) }

    val monthRecords = records.filter { it.month == selectedMonth || it.date.startsWith(selectedMonth) }
    val groupStudents = students.filter { it.groupNumber == selectedGroupTab }
    val groupStudentIds = groupStudents.map { it.id }.toSet()
    val groupRecords = monthRecords.filter { it.studentId in groupStudentIds }

    val plusPoints = groupRecords.filter { it.type == "PLUS" }.sumOf { it.points }
    val minusPoints = groupRecords.filter { it.type == "MINUS" }.sumOf { kotlin.math.abs(it.points) }
    val violations = groupRecords.filter { it.type == "VIOLATION" }
    val leader = groupStudents.find { it.role == "Tổ trưởng" } ?: groupStudents.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "QUẢN LÝ 4 TỔ HỌC TẬP",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "GVCN bấm vào thành viên để giao vai trò Tổ trưởng • Tổ trưởng nhập điểm trừ cho tổ",
                    style = MaterialTheme.typography.bodySmall,
                    color = RoyalBluePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs 1, 2, 3, 4
        TabRow(
            selectedTabIndex = selectedGroupTab - 1,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = RoyalBluePrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        ) {
            (1..4).forEach { grp ->
                Tab(
                    selected = selectedGroupTab == grp,
                    onClick = { selectedGroupTab = grp },
                    text = {
                        Text(
                            text = "TỔ $grp",
                            fontWeight = if (selectedGroupTab == grp) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Group summary stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Sĩ số tổ $selectedGroupTab",
                value = "${groupStudents.size} HS",
                subtitle = "Tổ trưởng: ${leader?.fullName ?: "Chưa gán"}",
                icon = Icons.Default.Group,
                accentColor = RoyalBluePrimary,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Thi đua tháng",
                value = "+$plusPoints / -$minusPoints",
                subtitle = "${violations.size} lần vi phạm",
                icon = Icons.Default.Star,
                accentColor = EmeraldSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Group Leader Demerit Entry Button
        Button(
            onClick = { showGroupLeaderDemeritDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tổ trưởng nhập điểm trừ cho thành viên Tổ $selectedGroupTab")
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "DANH SÁCH THÀNH VIÊN TỔ $selectedGroupTab (Bấm tên để giao vai trò)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (groupStudents.isEmpty()) {
            EmptyStateView(
                title = "Chưa có học sinh trong Tổ $selectedGroupTab",
                description = "Bạn hãy vào mục 'Danh sách HS' để thêm hoặc phân bổ học sinh vào tổ này.",
                icon = Icons.Default.Group
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(groupStudents, key = { it.id }) { student ->
                    val stPlus = groupRecords.filter { it.studentId == student.id && it.type == "PLUS" }.sumOf { it.points }
                    val stMinus = groupRecords.filter { it.studentId == student.id && it.type == "MINUS" }.sumOf { kotlin.math.abs(it.points) }
                    val stViolations = groupRecords.count { it.studentId == student.id && it.type == "VIOLATION" }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { roleStudentToAssign = student }
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (student.role == "Tổ trưởng") AmberAccent.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (student.role == "Tổ trưởng") {
                                    Icon(Icons.Default.Star, contentDescription = "Tổ trưởng", tint = AmberAccent, modifier = Modifier.size(22.dp))
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = RoyalBluePrimary, modifier = Modifier.size(20.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = student.fullName,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (student.role == "Tổ trưởng") EmeraldSecondary.copy(alpha = 0.15f)
                                                else MaterialTheme.colorScheme.surfaceVariant,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${student.role} ✎",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (student.role == "Tổ trưởng") EmeraldSecondary else RoyalBluePrimary
                                        )
                                    }
                                }
                                Text(
                                    text = "Mã: ${student.studentCode} • ${student.gender} • SĐT: ${student.phone.ifBlank { "Chưa có" }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            // Points badges
                            Column(horizontalAlignment = Alignment.End) {
                                if (stPlus > 0) {
                                    Text(
                                        text = "+$stPlus đ",
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSecondary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                if (stViolations > 0) {
                                    Text(
                                        text = "$stViolations lỗi",
                                        fontWeight = FontWeight.SemiBold,
                                        color = CoralRed,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Role Assignment Dialog
    if (roleStudentToAssign != null) {
        RoleAssignmentDialog(
            student = roleStudentToAssign!!,
            onDismiss = { roleStudentToAssign = null },
            onAssign = { st, newRole, targetGrp ->
                onAssignRole(st, newRole, targetGrp)
                roleStudentToAssign = null
            }
        )
    }

    // Group Leader Demerit Entry Dialog (Strictly locked to members of this group)
    if (showGroupLeaderDemeritDialog) {
        GroupLeaderDemeritDialog(
            groupNumber = selectedGroupTab,
            groupMembers = groupStudents,
            groupLeader = leader,
            selectedMonth = selectedMonth,
            onDismiss = { showGroupLeaderDemeritDialog = false },
            onSubmit = { target, cat, pts, desc, act, date ->
                onGroupLeaderSubmitMinus(leader, selectedGroupTab, target, cat, pts, desc, act, date)
                showGroupLeaderDemeritDialog = false
            }
        )
    }
}

@Composable
fun GroupLeaderDemeritDialog(
    groupNumber: Int,
    groupMembers: List<Student>,
    groupLeader: Student?,
    selectedMonth: String,
    onDismiss: () -> Unit,
    onSubmit: (target: Student, category: String, points: Int, description: String, actionTaken: String, date: String) -> Unit
) {
    var selectedStudent by remember { mutableStateOf(groupMembers.firstOrNull()) }
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
                    text = "TỔ TRƯỞNG NHẬP ĐIỂM TRỪ TỔ $groupNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = CoralRed
                )
                Text(
                    text = "Tổ trưởng thực hiện: ${groupLeader?.fullName ?: "Tổ trưởng Tổ $groupNumber"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text(
                        text = "Chọn thành viên trong Tổ $groupNumber bị trừ điểm:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedStudent?.fullName ?: "Chọn thành viên",
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
                            groupMembers.forEach { mem ->
                                DropdownMenuItem(
                                    text = { Text("${mem.fullName} (Mã: ${mem.studentCode})") },
                                    onClick = {
                                        selectedStudent = mem
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Lý do trừ điểm nề nếp tổ:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
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
                        label = { Text("Lý do chi tiết") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = points.toString(),
                        onValueChange = { points = it.toIntOrNull() ?: -2 },
                        label = { Text("Số điểm trừ") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Ghi chú vi phạm của tổ") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = actionTaken,
                        onValueChange = { actionTaken = it },
                        label = { Text("Biện pháp xử lý của tổ") },
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
                    val target = selectedStudent
                    if (target != null && category.isNotBlank()) {
                        onSubmit(target, category.trim(), -kotlin.math.abs(points), description.trim(), actionTaken.trim(), date.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
            ) {
                Text("Xác nhận trừ điểm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

