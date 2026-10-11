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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.model.StudentEvaluation
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CoralRed
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.RoyalBluePrimary

@Composable
fun MonthlyEvaluationScreen(
    evaluations: List<StudentEvaluation>,
    selectedMonth: String
) {
    val totalCount = evaluations.size
    val totCount = evaluations.count { it.rating == "Tốt" }
    val khaCount = evaluations.count { it.rating == "Khá" }
    val datCount = evaluations.count { it.rating == "Đạt" }
    val chuaDatCount = evaluations.count { it.rating == "Chưa đạt" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "XẾP LOẠI RÈN LUYỆN THÁNG $selectedMonth",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Tính toán tự động từ điểm thi đua nề nếp và vi phạm kỷ luật",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Ratio summary card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EvaluationSummaryItem(
                    title = "TỐT",
                    count = totCount,
                    total = totalCount,
                    color = EmeraldSecondary
                )
                EvaluationSummaryItem(
                    title = "KHÁ",
                    count = khaCount,
                    total = totalCount,
                    color = RoyalBluePrimary
                )
                EvaluationSummaryItem(
                    title = "ĐẠT",
                    count = datCount,
                    total = totalCount,
                    color = AmberAccent
                )
                EvaluationSummaryItem(
                    title = "CHƯA ĐẠT",
                    count = chuaDatCount,
                    total = totalCount,
                    color = CoralRed
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "BẢNG ĐIỂM RÈN LUYỆN TOÀN LỚP (${evaluations.size} HỌC SINH)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (evaluations.isEmpty()) {
            EmptyStateView(
                title = "Chưa có danh sách học sinh để xếp loại",
                description = "Hãy thêm học sinh hoặc nạp dữ liệu mẫu lớp 10C3 để xem bảng xếp loại rèn luyện.",
                icon = Icons.Default.Assessment
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(evaluations, key = { it.student.id }) { item ->
                    val ratingColor = when (item.rating) {
                        "Tốt" -> EmeraldSecondary
                        "Khá" -> RoyalBluePrimary
                        "Đạt" -> AmberAccent
                        else -> CoralRed
                    }

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
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(ratingColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${item.netScore}",
                                    fontWeight = FontWeight.Bold,
                                    color = ratingColor,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.student.fullName,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Mã: ${item.student.studentCode} • Tổ ${item.student.groupNumber}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = "+${item.totalPlusPoints} điểm thưởng • -${item.totalMinusPoints} điểm phạt • ${item.violationCount} lỗi",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(ratingColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = item.rating.uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ratingColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EvaluationSummaryItem(
    title: String,
    count: Int,
    total: Int,
    color: Color
) {
    val percent = if (total > 0) (count * 100 / total) else 0

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$count",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "$percent%",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
