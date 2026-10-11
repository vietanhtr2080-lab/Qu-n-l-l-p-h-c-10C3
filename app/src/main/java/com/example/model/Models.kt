package com.example.model

/**
 * Data model for a student in class 10C3
 */
data class Student(
    val id: String = "",
    val studentCode: String = "",
    val fullName: String = "",
    val gender: String = "Nam",
    val dateOfBirth: String = "",
    val phone: String = "",
    val address: String = "",
    val groupNumber: Int = 1,
    val role: String = "Học sinh",
    val notes: String = "",
    val createdAt: String = ""
)

/**
 * Record for merit/demerit points or violations
 * type: "PLUS", "MINUS", "VIOLATION"
 */
data class ConductRecord(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val groupNumber: Int = 1,
    val type: String = "PLUS", // PLUS (+), MINUS (-), VIOLATION
    val category: String = "",
    val points: Int = 0,
    val description: String = "",
    val date: String = "", // YYYY-MM-DD
    val month: String = "", // YYYY-MM
    val resolved: Boolean = false,
    val actionTaken: String = "",
    val recordedBy: String = "", // "GVCN", "Tổ trưởng...", "Học sinh..."
    val createdAt: String = ""
)

enum class UserRoleMode(val title: String) {
    TEACHER("GVCN (Toàn quyền)"),
    GROUP_LEADER("Tổ trưởng"),
    STUDENT("Học sinh")
}

object ClassRoles {
    const val MONITOR = "Lớp trưởng"
    const val VICE_MONITOR_ACADEMIC = "Lớp phó học tập"
    const val VICE_MONITOR_ACTIVITY = "Lớp phó phong trào"
    const val VICE_MONITOR_LABOR = "Lớp phó lao động"
    const val SECRETARY = "Bí thư chi đoàn"
    const val GROUP_LEADER = "Tổ trưởng"
    const val MEMBER = "Học sinh"

    val allAssignableRoles = listOf(
        MONITOR,
        VICE_MONITOR_ACADEMIC,
        VICE_MONITOR_ACTIVITY,
        VICE_MONITOR_LABOR,
        SECRETARY,
        GROUP_LEADER,
        MEMBER
    )
}

/**
 * Desk/Seat assignment in the classroom
 */
data class SeatAssignment(
    val id: String = "", // row_col_pos (e.g., "1_1_L" or "1_1")
    val row: Int = 1,
    val col: Int = 1,
    val position: String = "L", // "L" (Trái) or "R" (Phải)
    val studentId: String = "",
    val studentName: String = "",
    val groupNumber: Int = 1,
    val studentRole: String = ""
)

/**
 * Class and teacher settings
 */
data class ClassSettings(
    val teacherName: String = "Cô Nguyễn Thị Mai",
    val className: String = "10C3",
    val academicYear: String = "2024 - 2025",
    val currentMonth: String = "10/2024",
    val schoolName: String = "THPT Chuyên Lê Hồng Phong"
)

/**
 * Monthly conduct evaluation result for each student
 */
data class StudentEvaluation(
    val student: Student,
    val totalPlusPoints: Int,
    val totalMinusPoints: Int,
    val violationCount: Int,
    val netScore: Int,
    val rating: String // Tốt, Khá, Đạt, Chưa đạt
)
