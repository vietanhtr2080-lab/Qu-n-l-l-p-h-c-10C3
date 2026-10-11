package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConductRecord
import com.example.model.Student
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.RoyalBluePrimary

@Composable
fun WarningStudentsScreen(
    warningList: List<Pair<Student, List<ConductRecord>>>,
    selectedMonth: String,
    teacherName: String
) {
    var selectedParentNoticeStudent by remember { mutableStateOf<Pair<Student, List<ConductRecord>>?>(null) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

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
                    text = "CẢNH BÁO & NHẮC NHỞ HỌC SINH",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Danh sách học sinh có từ 1 lỗi vi phạm trở lên trong tháng $selectedMonth",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (warningList.isEmpty()) {
            EmptyStateView(
                title = "Lớp 10C3 không có học sinh vi phạm!",
                description = "Thật tuyệt vời! Tất cả học sinh đều tuân thủ tốt nội quy và nề nếp trong tháng $selectedMonth.",
                icon = Icons.Default.CheckCircle
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(warningList, key = { it.first.id }) { (student, violations) ->
                    val isHighAlert = violations.size >= 3

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.5.dp,
                                if (isHighAlert) CoralRed else CardBorder,
                                RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isHighAlert) Color(0xFFFFF5F5) else MaterialTheme.colorScheme.surface
                        )
                    ) {
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (isHighAlert) CoralRed else AmberAccent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationImportant,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = student.fullName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Mã: ${student.studentCode} • Tổ ${student.groupNumber} • SĐT: ${student.phone.ifBlank { "Chưa có" }}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isHighAlert) CoralRed.copy(alpha = 0.15f) else AmberAccent.copy(alpha = 0.15f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${violations.size} LỖI VI PHẠM",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isHighAlert) CoralRed else Color(0xFFB78103)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Các lỗi cụ thể trong tháng:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            violations.forEach { viol ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "•", color = CoralRed, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${viol.category} (${viol.date})",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Xử lý: ${viol.actionTaken.ifBlank { "Chưa xử lý" }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { selectedParentNoticeStudent = Pair(student, violations) },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Mẫu tin nhắn phụ huynh")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Parent Notice Message Dialog
    if (selectedParentNoticeStudent != null) {
        val (st, viols) = selectedParentNoticeStudent!!
        val violsSummary = viols.joinToString("; ") { "${it.category} ngày ${it.date}" }
        val messageText = """
Kính gửi Phụ huynh em ${st.fullName} (Học sinh lớp 10C3),
Tôi là $teacherName - Giáo viên chủ nhiệm lớp 10C3.
Tôi xin phép trao đổi với gia đình về tình hình nề nếp học tập của em trong tháng $selectedMonth.
Hiện tại, em đã vi phạm ${viols.size} lần gồm: $violsSummary.
Rất mong quý phụ huynh phối hợp cùng nhà trường nhắc nhở, động viên để em chấn chỉnh kịp thời và đạt kết quả rèn luyện tốt hơn.
Trân trọng cảm ơn quý phụ huynh!
        """.trimIndent()

        AlertDialog(
            onDismissRequest = { selectedParentNoticeStudent = null },
            title = { Text("Thông báo phụ huynh em ${st.fullName}") },
            text = {
                Column {
                    Text(
                        text = "Mẫu nội dung thông báo gửi qua Zalo / SMS:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = messageText,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(messageText))
                        Toast.makeText(context, "Đã sao chép nội dung tin nhắn!", Toast.LENGTH_SHORT).show()
                        selectedParentNoticeStudent = null
                    }
                ) {
                    Text("Sao chép tin nhắn")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedParentNoticeStudent = null }) {
                    Text("Đóng")
                }
            }
        )
    }
}
