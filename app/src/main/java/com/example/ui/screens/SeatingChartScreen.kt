package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SeatAssignment
import com.example.model.Student
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary

@Composable
fun SeatingChartScreen(
    students: List<Student>,
    seating: List<SeatAssignment>,
    onAssignSeat: (seatId: String, row: Int, col: Int, position: String, studentId: String, studentName: String, groupNum: Int, role: String) -> Unit,
    onClearSeat: (seatId: String) -> Unit,
    onAutoAssignByGroup: () -> Unit,
    onAutoAssignAlphabetical: () -> Unit,
    onAutoAssignRandom: () -> Unit,
    onAutoAssignFromText: (List<String>) -> Unit,
    onLoadReferenceSeatingChart: () -> Unit,
    onClearAllSeats: () -> Unit
) {
    val context = LocalContext.current

    // 4 Dãy (Columns) x 5 Bàn (Rows) x 2 Chỗ (Left/Right) = 40 seats
    val totalCols = 4
    val totalRows = 5

    var selectedSeatInfo by remember { mutableStateOf<Triple<Int, Int, String>?>(null) } // Col, Row, Position
    var showAssignDialog by remember { mutableStateOf(false) }
    var showAutoAssignDialog by remember { mutableStateOf(false) }
    var showTextImportDialog by remember { mutableStateOf(false) }
    var showConfirmClearAll by remember { mutableStateOf(false) }

    // Active column filter: 0 = Toàn bộ lớp (4 dãy), 1..4 = Xem chi tiết Dãy 1, 2, 3, 4
    var selectedViewCol by remember { mutableIntStateOf(0) }

    // Map seating by key: "${col}_${row}_${pos}" or legacy "${row}_${col}"
    val seatingMap = remember(seating) {
        val map = mutableMapOf<String, SeatAssignment>()
        seating.forEach { seat ->
            if (seat.id.isNotBlank()) {
                map[seat.id] = seat
            }
            // fallback index
            map["${seat.col}_${seat.row}_${seat.position}"] = seat
        }
        map
    }

    val occupiedCount = seating.count { it.studentName.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SƠ ĐỒ CHỖ NGỒI LỚP 10C3",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Đã xếp: $occupiedCount/${students.size} học sinh • 4 Dãy x 5 Bàn đôi (40 chỗ)",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (occupiedCount > 0) EmeraldSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showAutoAssignDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBluePrimary)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tự động xếp chỗ")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tool bar: View filter chips & Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = selectedViewCol == 0,
                    onClick = { selectedViewCol = 0 },
                    label = { Text("Toàn lớp (4 Dãy)") }
                )
                (1..4).forEach { col ->
                    FilterChip(
                        selected = selectedViewCol == col,
                        onClick = { selectedViewCol = col },
                        label = { Text("Dãy $col (Tổ $col)") }
                    )
                }
            }

            if (occupiedCount > 0) {
                TextButton(
                    onClick = { showConfirmClearAll = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = CoralRed)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Làm mới", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Blackboard & Teacher Podium (Chalkboard Area)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A24)) // Dark chalkboard green
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Front Door indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF8D6E63), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DoorFront, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CỬA RA VÀO", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Blackboard
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "★ BẢNG ĐEN LỚP 10C3 ★",
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Bục giảng & Giảng đường",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }

                // Teacher Desk
                Box(
                    modifier = Modifier
                        .background(Color(0xFF5D4037), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("BÀN GVCN", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Classroom Floor Grid
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(totalRows) { rIdx ->
                val rowNum = rIdx + 1

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HÀNG BÀN $rowNum",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.outline
                        )
                        if (rowNum == 1) {
                            Text(
                                text = "Gần bục giảng nhất",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Columns row (Dãy 1..4)
                    val colsToDisplay = if (selectedViewCol == 0) (1..totalCols).toList() else listOf(selectedViewCol)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colsToDisplay.forEach { colNum ->
                            DoubleDeskView(
                                col = colNum,
                                row = rowNum,
                                seatLeft = seatingMap["${colNum}_${rowNum}_L"] ?: seatingMap["${rowNum}_${colNum}"],
                                seatRight = seatingMap["${colNum}_${rowNum}_R"],
                                onSeatClick = { pos ->
                                    selectedSeatInfo = Triple(colNum, rowNum, pos)
                                    showAssignDialog = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Bottom of Classroom (Back Wall)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BẢNG TIN CUỐI LỚP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MeetingRoom, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CỬA SAU", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Auto-Assign Dialog
    if (showAutoAssignDialog) {
        AlertDialog(
            onDismissRequest = { showAutoAssignDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RoyalBluePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("TỰ ĐỘNG XẾP CHỖ NGỒI", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "GVCN có thể tự động xếp chỗ cho học sinh lớp 10C3 theo các phương án khoa học:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Option 1: By Group (Standard in VN schools)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAutoAssignByGroup()
                                showAutoAssignDialog = false
                                Toast.makeText(context, "Đã tự động xếp chỗ theo 4 Tổ thành công!", Toast.LENGTH_SHORT).show()
                            }
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = RoyalBluePrimary.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎯 Xếp theo 4 Tổ (Khuyên dùng)", fontWeight = FontWeight.Bold, color = RoyalBluePrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tổ 1 ngồi Dãy 1, Tổ 2 ngồi Dãy 2, Tổ 3 ngồi Dãy 3, Tổ 4 ngồi Dãy 4. Lớp trưởng và Tổ trưởng được ưu tiên ngồi đầu.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Option 2: Alphabetical
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAutoAssignAlphabetical()
                                showAutoAssignDialog = false
                                Toast.makeText(context, "Đã xếp chỗ theo thứ tự bảng chữ cái!", Toast.LENGTH_SHORT).show()
                            }
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🔤 Xếp theo Bảng chữ cái Tên (A - Z)", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lần lượt điền học sinh theo vần tên từ Dãy 1 đến Dãy 4.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Option 3: Random
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAutoAssignRandom()
                                showAutoAssignDialog = false
                                Toast.makeText(context, "Đã đổi chỗ ngẫu nhiên cho toàn lớp!", Toast.LENGTH_SHORT).show()
                            }
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🎲 Xáo trộn ngẫu nhiên (Đổi chỗ hàng tháng)", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Quay vòng chỗ ngồi ngẫu nhiên tạo sự mới mẻ và công bằng.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Option 4: Direct Text Import
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showAutoAssignDialog = false
                                showTextImportDialog = true
                            }
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldSecondary.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📝 GVCN dán danh sách tên học sinh", fontWeight = FontWeight.Bold, color = EmeraldSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Dán danh sách họ tên từ Word/Excel, hệ thống tự động điền lần lượt vào từng ghế.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Option 5: Load exact reference seating design
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLoadReferenceSeatingChart()
                                showAutoAssignDialog = false
                                Toast.makeText(context, "Đã nạp sơ đồ chỗ ngồi theo thiết kế ảnh tham khảo!", Toast.LENGTH_SHORT).show()
                            }
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AmberAccent.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("⭐ Tải sơ đồ lớp học theo ảnh thiết kế tham khảo", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nạp nhanh chính xác sơ đồ chỗ ngồi chuẩn mẫu tham khảo: Hân, Khôi, Gia, Hy, Thịnh, Chí Viễn...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAutoAssignDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }

    // Direct Text Import Dialog (GVCN tự động nhập tên vào)
    if (showTextImportDialog) {
        var rawText by remember { mutableStateOf("") }
        var inputError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showTextImportDialog = false },
            title = { Text("GVCN NHẬP DANH SÁCH TÊN TỰ ĐỘNG", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Nhập hoặc dán họ tên các học sinh (phân cách bằng dấu phẩy hoặc xuống dòng). Hệ thống sẽ tự động điền vào sơ đồ theo thứ tự Dãy 1 -> Dãy 4:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = rawText,
                        onValueChange = { rawText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        placeholder = { Text("VD:\nNguyễn Văn An\nTrần Thị Bình\nLê Hoàng Cường\nPhạm Minh Dũng...") }
                    )
                    if (inputError != null) {
                        Text(inputError ?: "", color = CoralRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val names = rawText.split("\n", ",").map { it.trim() }.filter { it.isNotBlank() }
                        if (names.isEmpty()) {
                            inputError = "Vui lòng nhập ít nhất 1 tên học sinh!"
                            return@Button
                        }
                        onAutoAssignFromText(names)
                        showTextImportDialog = false
                        Toast.makeText(context, "Đã tự động xếp ${names.size} học sinh vào sơ đồ!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Tự động điền vào sơ đồ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTextImportDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Single Seat Details & Assignment Dialog
    if (showAssignDialog && selectedSeatInfo != null) {
        val (col, row, pos) = selectedSeatInfo!!
        val seatKey = "${col}_${row}_$pos"
        val currentAssignment = seatingMap[seatKey] ?: seatingMap["${row}_$col"]
        val posTitle = if (pos == "L") "Ghế Trái" else "Ghế Phải"

        var searchStudentQuery by remember { mutableStateOf("") }
        var customStudentName by remember { mutableStateOf("") }

        val filteredCandidates = remember(searchStudentQuery, students) {
            if (searchStudentQuery.isBlank()) students
            else students.filter { it.fullName.contains(searchStudentQuery, ignoreCase = true) || it.studentCode.contains(searchStudentQuery, ignoreCase = true) }
        }

        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = {
                Column {
                    Text(
                        text = "VỊ TRÍ: DÃY $col (TỔ $col) • BÀN $row • $posTitle",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = RoyalBluePrimary
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (currentAssignment != null && currentAssignment.studentName.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = RoyalBluePrimary.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = currentAssignment.studentName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Vai trò: ${currentAssignment.studentRole.ifBlank { "Học sinh" }} • Tổ ${currentAssignment.groupNumber}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                Button(
                                    onClick = {
                                        onClearSeat(seatKey)
                                        showAssignDialog = false
                                        Toast.makeText(context, "Đã làm trống vị trí này!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Làm trống", fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Hoặc đổi sang học sinh khác:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "Chọn học sinh hoặc tự nhập tên để xếp vào vị trí này:",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Direct name entry option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customStudentName,
                            onValueChange = { customStudentName = it },
                            placeholder = { Text("Tự gõ tên vào ghế...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (customStudentName.isNotBlank()) {
                                    val matched = students.find { it.fullName.equals(customStudentName.trim(), ignoreCase = true) }
                                    onAssignSeat(
                                        seatKey,
                                        row,
                                        col,
                                        pos,
                                        matched?.id ?: "",
                                        customStudentName.trim(),
                                        matched?.groupNumber ?: col,
                                        matched?.role ?: "Học sinh"
                                    )
                                    showAssignDialog = false
                                }
                            },
                            enabled = customStudentName.isNotBlank(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Gán", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Search from class list
                    OutlinedTextField(
                        value = searchStudentQuery,
                        onValueChange = { searchStudentQuery = it },
                        placeholder = { Text("Tìm trong danh sách học sinh...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredCandidates) { st ->
                            val isSeatedSomewhere = seating.any { it.studentId == st.id && it.id != seatKey }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAssignSeat(
                                            seatKey,
                                            row,
                                            col,
                                            pos,
                                            st.id,
                                            st.fullName,
                                            st.groupNumber,
                                            st.role
                                        )
                                        showAssignDialog = false
                                        Toast.makeText(context, "Đã xếp ${st.fullName} vào Dãy $col Bàn $row!", Toast.LENGTH_SHORT).show()
                                    },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (currentAssignment?.studentId == st.id)
                                        RoyalBluePrimary.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (st.role == "Lớp trưởng") "👑 " else if (st.role == "Tổ trưởng") "🎖️ " else "",
                                            fontSize = 12.sp
                                        )
                                        Column {
                                            Text(text = st.fullName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            Text(
                                                text = "Tổ ${st.groupNumber} • Mã: ${st.studentCode}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                    if (isSeatedSomewhere) {
                                        Text(
                                            text = "Đã có chỗ",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAssignDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }

    // Confirm Clear All Dialog
    if (showConfirmClearAll) {
        AlertDialog(
            onDismissRequest = { showConfirmClearAll = false },
            title = { Text("Xác nhận làm mới sơ đồ") },
            text = { Text("Bạn có chắc chắn muốn xóa toàn bộ chỗ ngồi hiện tại để xếp lại từ đầu?") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllSeats()
                        showConfirmClearAll = false
                        Toast.makeText(context, "Đã làm trống toàn bộ sơ đồ chỗ ngồi!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("Xóa tất cả")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClearAll = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

/**
 * Renders a standard Vietnamese Double Desk (Bàn đôi 2 học sinh ngồi cạnh nhau)
 */
@Composable
fun DoubleDeskView(
    col: Int,
    row: Int,
    seatLeft: SeatAssignment?,
    seatRight: SeatAssignment?,
    onSeatClick: (pos: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupColor = when (col) {
        1 -> RoyalBluePrimary
        2 -> EmeraldSecondary
        3 -> AmberAccent
        4 -> Color(0xFF8E24AA)
        else -> RoyalBluePrimary
    }

    Card(
        modifier = modifier
            .border(1.5.dp, groupColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            // Desk Header (Dãy x - Bàn y)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dãy $col • Bàn $row",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = groupColor
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(groupColor)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Two seats side by side (Ghế Trái & Ghế Phải)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SingleSeatSpot(
                    label = "Trái",
                    assignment = seatLeft,
                    onClick = { onSeatClick("L") },
                    modifier = Modifier.weight(1f)
                )

                SingleSeatSpot(
                    label = "Phải",
                    assignment = seatRight,
                    onClick = { onSeatClick("R") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SingleSeatSpot(
    label: String,
    assignment: SeatAssignment?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOccupied = assignment != null && assignment.studentName.isNotBlank()

    Card(
        modifier = modifier
            .height(64.dp)
            .clickable(onClick = onClick)
            .border(
                1.dp,
                if (isOccupied) RoyalBluePrimary.copy(alpha = 0.5f) else CardBorder,
                RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOccupied) RoyalBluePrimary.copy(alpha = 0.07f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isOccupied) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (assignment?.studentRole == "Lớp trưởng") {
                        Text("👑", fontSize = 10.sp)
                    } else if (assignment?.studentRole == "Tổ trưởng") {
                        Text("🎖️", fontSize = 10.sp)
                    }
                    Text(
                        text = assignment?.studentName?.split(" ")?.lastOrNull() ?: assignment?.studentName ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = assignment?.studentName ?: "",
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = label,
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
