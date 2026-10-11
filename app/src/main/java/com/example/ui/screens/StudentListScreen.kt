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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.Student
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.RoyalBluePrimary

@Composable
fun StudentListScreen(
    students: List<Student>,
    searchQuery: String,
    selectedGroupFilter: Int?,
    onSearchQueryChange: (String) -> Unit,
    onGroupFilterChange: (Int?) -> Unit,
    onSaveStudent: (Student) -> Unit,
    onDeleteStudent: (String) -> Unit,
    onAssignRole: (Student, String, Int?) -> Unit
) {
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    var roleStudent by remember { mutableStateOf<Student?>(null) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingStudent = null
                    showAddEditDialog = true
                },
                containerColor = RoyalBluePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm học sinh")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header & Search
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DANH SÁCH HỌC SINH",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "GVCN click vào học sinh để giao vai trò Lớp trưởng, Tổ trưởng",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoyalBluePrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = {
                        editingStudent = null
                        showAddEditDialog = true
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Thêm mới")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tìm theo tên hoặc mã học sinh...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedGroupFilter == null,
                    onClick = { onGroupFilterChange(null) },
                    label = { Text("Tất cả") }
                )
                (1..4).forEach { grp ->
                    FilterChip(
                        selected = selectedGroupFilter == grp,
                        onClick = { onGroupFilterChange(grp) },
                        label = { Text("Tổ $grp") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Student list
            if (students.isEmpty()) {
                EmptyStateView(
                    title = "Không tìm thấy học sinh",
                    description = if (searchQuery.isNotBlank() || selectedGroupFilter != null)
                        "Không có học sinh nào phù hợp với bộ lọc hiện tại."
                    else "Chưa có học sinh nào trong lớp 10C3. Hãy thêm học sinh đầu tiên!",
                    icon = Icons.Default.Person,
                    actionButtonText = "Thêm học sinh",
                    onActionClick = {
                        editingStudent = null
                        showAddEditDialog = true
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(students, key = { it.id }) { student ->
                        StudentCard(
                            student = student,
                            onClickRole = { roleStudent = student },
                            onEdit = {
                                editingStudent = student
                                showAddEditDialog = true
                            },
                            onDelete = { studentToDelete = student }
                        )
                    }
                }
            }
        }
    }

    // Role Assignment Dialog
    if (roleStudent != null) {
        RoleAssignmentDialog(
            student = roleStudent!!,
            onDismiss = { roleStudent = null },
            onAssign = { st, newRole, targetGrp ->
                onAssignRole(st, newRole, targetGrp)
                roleStudent = null
            }
        )
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        StudentFormDialog(
            student = editingStudent,
            onDismiss = { showAddEditDialog = false },
            onSave = { saved ->
                onSaveStudent(saved)
                showAddEditDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (studentToDelete != null) {
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Xác nhận xóa học sinh") },
            text = { Text("Bạn có chắc chắn muốn xóa học sinh ${studentToDelete?.fullName} (Mã: ${studentToDelete?.studentCode}) khỏi lớp 10C3?") },
            confirmButton = {
                Button(
                    onClick = {
                        studentToDelete?.let { onDeleteStudent(it.id) }
                        studentToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun StudentCard(
    student: Student,
    onClickRole: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClickRole)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
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
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (student.role == "Lớp trưởng") Color(0xFFFFF3E0)
                        else if (student.role == "Tổ trưởng") EmeraldSecondary.copy(alpha = 0.15f)
                        else if (student.gender == "Nữ") Color(0xFFFCE4EC) else Color(0xFFE3F2FD)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (student.role == "Lớp trưởng") "👑"
                    else if (student.role == "Tổ trưởng") "🎖️"
                    else student.fullName.takeLast(1),
                    fontWeight = FontWeight.Bold,
                    color = if (student.gender == "Nữ") Color(0xFFC2185B) else RoyalBluePrimary,
                    fontSize = if (student.role == "Lớp trưởng" || student.role == "Tổ trưởng") 18.sp else 16.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = student.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(RoyalBluePrimary.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = student.studentCode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalBluePrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tổ ${student.groupNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = "•", color = MaterialTheme.colorScheme.outline)

                    // Clickable Role Badge for Quick Assignment
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (student.role) {
                                    "Lớp trưởng" -> Color(0xFFFFF3E0)
                                    "Tổ trưởng" -> EmeraldSecondary.copy(alpha = 0.15f)
                                    "Học sinh" -> MaterialTheme.colorScheme.surfaceVariant
                                    else -> RoyalBluePrimary.copy(alpha = 0.12f)
                                }
                            )
                            .clickable(onClick = onClickRole)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${student.role} ✎",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = when (student.role) {
                                "Lớp trưởng" -> Color(0xFFE65100)
                                "Tổ trưởng" -> EmeraldSecondary
                                "Học sinh" -> MaterialTheme.colorScheme.onSurfaceVariant
                                else -> RoyalBluePrimary
                            }
                        )
                    }

                    if (student.phone.isNotBlank()) {
                        Text(text = "•", color = MaterialTheme.colorScheme.outline)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = student.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = RoyalBluePrimary)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = CoralRed)
            }
        }
    }
}

@Composable
fun RoleAssignmentDialog(
    student: Student,
    onDismiss: () -> Unit,
    onAssign: (Student, String, Int?) -> Unit
) {
    var selectedRole by remember { mutableStateOf(student.role) }
    var selectedGroup by remember { mutableStateOf(student.groupNumber) }

    val rolesList = listOf(
        "Lớp trưởng" to "👑 Điều hành chung nề nếp toàn lớp 10C3",
        "Lớp phó học tập" to "🥈 Quản lý tình hình học tập và sổ đầu bài",
        "Lớp phó lao động" to "🧹 Phụ trách vệ sinh, trực nhật và cơ sở vật chất",
        "Lớp phó phong trào" to "🏃 Quản lý hoạt động ngoại khóa, văn thể mỹ",
        "Bí thư chi đoàn" to "🚩 Phụ trách phong trào Đoàn và sinh hoạt chi đoàn",
        "Tổ trưởng" to "🎖️ Quản lý nề nếp và nhập điểm trừ cho thành viên tổ",
        "Học sinh" to "👤 Thành viên lớp học, tự nhập điểm cộng thành tích"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("GVCN GIAO VAI TRÒ CÁN BỘ LỚP", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RoyalBluePrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Học sinh: ${student.fullName} (Mã: ${student.studentCode})", style = MaterialTheme.typography.bodyMedium)
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text("Chọn vai trò cần giao:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }

                items(rolesList) { (roleName, desc) ->
                    val isSelected = selectedRole == roleName
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRole = roleName }
                            .border(
                                1.5.dp,
                                if (isSelected) RoyalBluePrimary else CardBorder,
                                RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) RoyalBluePrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = roleName,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) RoyalBluePrimary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Text("✓ Đang chọn", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalBluePrimary)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (selectedRole == "Tổ trưởng") {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Phân làm Tổ trưởng của:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (1..4).forEach { grp ->
                                FilterChip(
                                    selected = selectedGroup == grp,
                                    onClick = { selectedGroup = grp },
                                    label = { Text("Tổ $grp") }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalGroup = if (selectedRole == "Tổ trưởng") selectedGroup else student.groupNumber
                    onAssign(student, selectedRole, finalGroup)
                }
            ) {
                Text("Lưu vai trò")
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
fun StudentFormDialog(
    student: Student?,
    onDismiss: () -> Unit,
    onSave: (Student) -> Unit
) {
    var fullName by remember { mutableStateOf(student?.fullName ?: "") }
    var studentCode by remember { mutableStateOf(student?.studentCode ?: "") }
    var gender by remember { mutableStateOf(student?.gender ?: "Nam") }
    var dateOfBirth by remember { mutableStateOf(student?.dateOfBirth ?: "2009-01-01") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }
    var address by remember { mutableStateOf(student?.address ?: "") }
    var groupNumber by remember { mutableStateOf(student?.groupNumber ?: 1) }
    var role by remember { mutableStateOf(student?.role ?: "Học sinh") }
    var notes by remember { mutableStateOf(student?.notes ?: "") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val roles = listOf("Học sinh", "Lớp trưởng", "Lớp phó học tập", "Lớp phó lao động", "Bí thư", "Tổ trưởng")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (student == null) "Thêm học sinh mới" else "Chỉnh sửa thông tin học sinh") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Họ và tên *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = studentCode,
                        onValueChange = { studentCode = it },
                        label = { Text("Mã học sinh *") },
                        placeholder = { Text("VD: 10C3-01") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { gender = "Nam" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = if (gender == "Nam") androidx.compose.foundation.BorderStroke(2.dp, RoyalBluePrimary) else null
                        ) {
                            Text("Nam")
                        }
                        OutlinedButton(
                            onClick = { gender = "Nữ" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = if (gender == "Nữ") androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFC2185B)) else null
                        ) {
                            Text("Nữ")
                        }
                    }
                }
                item {
                    Text("Phân vào tổ:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..4).forEach { grp ->
                            FilterChip(
                                selected = groupNumber == grp,
                                onClick = { groupNumber = grp },
                                label = { Text("Tổ $grp") }
                            )
                        }
                    }
                }
                item {
                    Text("Chức vụ trong lớp:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        roles.take(3).forEach { r ->
                            FilterChip(
                                selected = role == r,
                                onClick = { role = r },
                                label = { Text(r, fontSize = 11.sp) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        roles.drop(3).forEach { r ->
                            FilterChip(
                                selected = role == r,
                                onClick = { role = r },
                                label = { Text(r, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Số điện thoại liên hệ") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Địa chỉ") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                if (errorText != null) {
                    item {
                        Text(errorText ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isBlank()) {
                        errorText = "Vui lòng nhập họ và tên học sinh"
                        return@Button
                    }
                    if (studentCode.isBlank()) {
                        errorText = "Vui lòng nhập mã học sinh"
                        return@Button
                    }
                    val newStudent = student?.copy(
                        fullName = fullName.trim(),
                        studentCode = studentCode.trim(),
                        gender = gender,
                        dateOfBirth = dateOfBirth,
                        phone = phone.trim(),
                        address = address.trim(),
                        groupNumber = groupNumber,
                        role = role,
                        notes = notes.trim()
                    ) ?: Student(
                        id = "",
                        fullName = fullName.trim(),
                        studentCode = studentCode.trim(),
                        gender = gender,
                        dateOfBirth = dateOfBirth,
                        phone = phone.trim(),
                        address = address.trim(),
                        groupNumber = groupNumber,
                        role = role,
                        notes = notes.trim()
                    )
                    onSave(newStudent)
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
