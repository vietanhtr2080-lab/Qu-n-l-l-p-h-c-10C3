package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ClassRepository
import com.example.model.ClassSettings
import com.example.model.ConductRecord
import com.example.model.SampleData
import com.example.model.SeatAssignment
import com.example.model.Student
import com.example.model.StudentEvaluation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ClassTab(val title: String) {
    DASHBOARD("Tổng quan"),
    STUDENTS("Danh sách HS"),
    GROUPS("Quản lý tổ"),
    SEATING("Sơ đồ chỗ ngồi"),
    MERIT_POINTS("Điểm cộng trừ"),
    DISCIPLINE("Sổ lỗi vi phạm"),
    HONOR_ROLL("Tuyên dương"),
    WARNINGS("Cảnh báo & Nhắc nhở"),
    EVALUATION("Xếp loại tháng"),
    STATISTICS("Báo cáo thống kê"),
    SETTINGS("Cài đặt")
}

class ClassViewModel(
    private val repository: ClassRepository,
    private val userId: String
) : ViewModel() {

    private val _currentTab = MutableStateFlow(ClassTab.DASHBOARD)
    val currentTab: StateFlow<ClassTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGroupFilter = MutableStateFlow<Int?>(null)
    val selectedGroupFilter: StateFlow<Int?> = _selectedGroupFilter.asStateFlow()

    private val _selectedMonth = MutableStateFlow("2024-10")
    val selectedMonth: StateFlow<String> = _selectedMonth.asStateFlow()

    // Active User Role Mode (GVCN, Tổ trưởng, Học sinh)
    private val _userRoleMode = MutableStateFlow(com.example.model.UserRoleMode.TEACHER)
    val userRoleMode: StateFlow<com.example.model.UserRoleMode> = _userRoleMode.asStateFlow()

    // Group selected when in GROUP_LEADER mode (1..4)
    private val _actingGroupLeaderNumber = MutableStateFlow(1)
    val actingGroupLeaderNumber: StateFlow<Int> = _actingGroupLeaderNumber.asStateFlow()

    // Student selected when in STUDENT mode
    private val _actingStudent = MutableStateFlow<Student?>(null)
    val actingStudent: StateFlow<Student?> = _actingStudent.asStateFlow()

    // Firestore streams
    val students: StateFlow<List<Student>> = repository.observeStudents(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val records: StateFlow<List<ConductRecord>> = repository.observeRecords(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val seating: StateFlow<List<SeatAssignment>> = repository.observeSeating(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<ClassSettings> = repository.observeSettings(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ClassSettings())

    // Filtered students by search & group
    val filteredStudents = combine(students, searchQuery, selectedGroupFilter) { list, query, grp ->
        list.filter { student ->
            val matchesQuery = query.isBlank() ||
                    student.fullName.contains(query, ignoreCase = true) ||
                    student.studentCode.contains(query, ignoreCase = true)
            val matchesGrp = grp == null || student.groupNumber == grp
            matchesQuery && matchesGrp
        }.sortedBy { it.studentCode }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly evaluations calculated from stored students & records
    val evaluations = combine(students, records, selectedMonth) { stList, recList, month ->
        val monthRecords = recList.filter { it.month == month || it.date.startsWith(month) }
        stList.map { student ->
            val stRecords = monthRecords.filter { it.studentId == student.id }
            val plus = stRecords.filter { it.type == "PLUS" }.sumOf { it.points }
            val minus = stRecords.filter { it.type == "MINUS" }.sumOf { kotlin.math.abs(it.points) }
            val violations = stRecords.count { it.type == "VIOLATION" }
            val violationDeduction = stRecords.filter { it.type == "VIOLATION" }.sumOf { kotlin.math.abs(it.points) }
            val baseScore = 80 // Điểm rèn luyện khởi điểm chuẩn
            val net = baseScore + plus - minus - violationDeduction

            val rating = when {
                violations >= 3 || net < 65 -> "Chưa đạt"
                net < 75 -> "Đạt"
                net < 90 -> "Khá"
                else -> "Tốt"
            }
            StudentEvaluation(
                student = student,
                totalPlusPoints = plus,
                totalMinusPoints = minus + violationDeduction,
                violationCount = violations,
                netScore = net.coerceIn(0, 100),
                rating = rating
            )
        }.sortedByDescending { it.netScore }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Honor roll: students with high plus points
    val honorRoll = combine(students, records, selectedMonth) { stList, recList, month ->
        val monthRecords = recList.filter { (it.month == month || it.date.startsWith(month)) && it.type == "PLUS" }
        stList.map { student ->
            val pts = monthRecords.filter { it.studentId == student.id }.sumOf { it.points }
            student to pts
        }.filter { it.second > 0 }
            .sortedByDescending { it.second }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Frequent violators: students with >= 2 violations in month
    val warningStudents = combine(students, records, selectedMonth) { stList, recList, month ->
        val monthViolations = recList.filter { (it.month == month || it.date.startsWith(month)) && it.type == "VIOLATION" }
        stList.map { student ->
            val violList = monthViolations.filter { it.studentId == student.id }
            student to violList
        }.filter { it.second.isNotEmpty() }
            .sortedByDescending { it.second.size }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: ClassTab) {
        _currentTab.value = tab
    }

    fun setUserRoleMode(mode: com.example.model.UserRoleMode) {
        _userRoleMode.value = mode
    }

    fun setActingGroupLeaderNumber(groupNum: Int) {
        _actingGroupLeaderNumber.value = groupNum
    }

    fun setActingStudent(student: Student?) {
        _actingStudent.value = student
    }

    // Teacher assigns role to a student (Lớp trưởng, Tổ trưởng, etc.)
    fun assignStudentRole(student: Student, newRole: String, targetGroup: Int? = null, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.assignRole(userId, student.id, newRole, targetGroup)
            onDone()
        }
    }

    // Student self-submits merit/plus point
    fun studentSubmitPlusPoint(
        student: Student,
        category: String,
        points: Int,
        description: String,
        date: String,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val record = ConductRecord(
                id = "",
                studentId = student.id,
                studentName = student.fullName,
                groupNumber = student.groupNumber,
                type = "PLUS",
                category = category,
                points = kotlin.math.abs(points),
                description = description,
                date = date,
                month = selectedMonth.value,
                recordedBy = "Học sinh ${student.fullName}"
            )
            repository.saveRecord(userId, record)
            onDone()
        }
    }

    // Group leader submits demerit/violation for a member in their group
    fun groupLeaderSubmitDemeritPoint(
        leaderName: String,
        groupNumber: Int,
        targetStudent: Student,
        category: String,
        points: Int,
        description: String,
        actionTaken: String,
        date: String,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val record = ConductRecord(
                id = "",
                studentId = targetStudent.id,
                studentName = targetStudent.fullName,
                groupNumber = targetStudent.groupNumber,
                type = "VIOLATION",
                category = category,
                points = -kotlin.math.abs(points),
                description = description,
                actionTaken = actionTaken,
                date = date,
                month = selectedMonth.value,
                resolved = false,
                recordedBy = "Tổ trưởng $leaderName (Tổ $groupNumber)"
            )
            repository.saveRecord(userId, record)
            onDone()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setGroupFilter(group: Int?) {
        _selectedGroupFilter.value = group
    }

    fun setSelectedMonth(month: String) {
        _selectedMonth.value = month
    }

    // Student CRUD
    fun saveStudent(student: Student, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveStudent(userId, student)
            onDone()
        }
    }

    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            repository.deleteStudent(userId, studentId)
        }
    }

    // Record CRUD
    fun saveRecord(record: ConductRecord, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val recWithMonth = if (record.month.isBlank()) {
                val m = if (record.date.length >= 7) record.date.substring(0, 7) else "2024-10"
                record.copy(month = m)
            } else record
            repository.saveRecord(userId, recWithMonth)
            onDone()
        }
    }

    fun toggleRecordResolved(record: ConductRecord) {
        viewModelScope.launch {
            repository.toggleRecordResolved(userId, record.id, record.resolved)
        }
    }

    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            repository.deleteRecord(userId, recordId)
        }
    }

    // Seating
    fun assignSeat(
        seatId: String,
        row: Int,
        col: Int,
        position: String,
        studentId: String,
        studentName: String,
        groupNumber: Int = 1,
        studentRole: String = ""
    ) {
        viewModelScope.launch {
            repository.saveSeat(
                userId,
                SeatAssignment(
                    id = seatId,
                    row = row,
                    col = col,
                    position = position,
                    studentId = studentId,
                    studentName = studentName,
                    groupNumber = groupNumber,
                    studentRole = studentRole
                )
            )
        }
    }

    fun clearSeat(seatId: String) {
        viewModelScope.launch {
            repository.clearSeat(userId, seatId)
        }
    }

    // Auto-assign by Group: Group 1 -> Row 1 (Dãy 1), Group 2 -> Row 2, etc.
    fun autoAssignSeatsByGroup() {
        viewModelScope.launch {
            val stList = students.value
            val newSeats = mutableListOf<SeatAssignment>()
            (1..4).forEach { grp ->
                val groupSts = stList.filter { it.groupNumber == grp }
                    .sortedWith(
                        compareByDescending<Student> { it.role == "Lớp trưởng" }
                            .thenByDescending { it.role == "Tổ trưởng" }
                            .thenBy { it.studentCode }
                    )
                var idx = 0
                for (row in 1..5) {
                    listOf("L", "R").forEach { pos ->
                        if (idx < groupSts.size) {
                            val st = groupSts[idx]
                            newSeats.add(
                                SeatAssignment(
                                    id = "${grp}_${row}_$pos",
                                    row = row,
                                    col = grp,
                                    position = pos,
                                    studentId = st.id,
                                    studentName = st.fullName,
                                    groupNumber = st.groupNumber,
                                    studentRole = st.role
                                )
                            )
                            idx++
                        }
                    }
                }
            }
            repository.clearAllSeats(userId)
            repository.saveAllSeats(userId, newSeats)
        }
    }

    // Auto-assign by Alphabetical order
    fun autoAssignSeatsAlphabetical() {
        viewModelScope.launch {
            val sortedList = students.value.sortedBy { it.fullName.split(" ").lastOrNull() ?: it.fullName }
            val newSeats = mutableListOf<SeatAssignment>()
            var idx = 0
            for (row in 1..5) {
                for (col in 1..4) {
                    for (pos in listOf("L", "R")) {
                        if (idx < sortedList.size) {
                            val st = sortedList[idx]
                            newSeats.add(
                                SeatAssignment(
                                    id = "${col}_${row}_$pos",
                                    row = row,
                                    col = col,
                                    position = pos,
                                    studentId = st.id,
                                    studentName = st.fullName,
                                    groupNumber = st.groupNumber,
                                    studentRole = st.role
                                )
                            )
                            idx++
                        }
                    }
                }
            }
            repository.clearAllSeats(userId)
            repository.saveAllSeats(userId, newSeats)
        }
    }

    // Auto-assign Randomly
    fun autoAssignSeatsRandom() {
        viewModelScope.launch {
            val shuffled = students.value.shuffled()
            val newSeats = mutableListOf<SeatAssignment>()
            var idx = 0
            for (row in 1..5) {
                for (col in 1..4) {
                    for (pos in listOf("L", "R")) {
                        if (idx < shuffled.size) {
                            val st = shuffled[idx]
                            newSeats.add(
                                SeatAssignment(
                                    id = "${col}_${row}_$pos",
                                    row = row,
                                    col = col,
                                    position = pos,
                                    studentId = st.id,
                                    studentName = st.fullName,
                                    groupNumber = st.groupNumber,
                                    studentRole = st.role
                                )
                            )
                            idx++
                        }
                    }
                }
            }
            repository.clearAllSeats(userId)
            repository.saveAllSeats(userId, newSeats)
        }
    }

    // Auto-assign from direct list of names entered by teacher
    fun autoAssignFromTextNames(names: List<String>) {
        viewModelScope.launch {
            val existingStudents = students.value
            val newSeats = mutableListOf<SeatAssignment>()
            var idx = 0
            for (row in 1..5) {
                for (col in 1..4) {
                    for (pos in listOf("L", "R")) {
                        if (idx < names.size) {
                            val rawName = names[idx].trim()
                            if (rawName.isNotBlank()) {
                                val matched = existingStudents.find { it.fullName.equals(rawName, ignoreCase = true) }
                                newSeats.add(
                                    SeatAssignment(
                                        id = "${col}_${row}_$pos",
                                        row = row,
                                        col = col,
                                        position = pos,
                                        studentId = matched?.id ?: "",
                                        studentName = rawName,
                                        groupNumber = matched?.groupNumber ?: col,
                                        studentRole = matched?.role ?: "Học sinh"
                                    )
                                )
                            }
                            idx++
                        }
                    }
                }
            }
            repository.clearAllSeats(userId)
            repository.saveAllSeats(userId, newSeats)
        }
    }

    fun clearAllSeats() {
        viewModelScope.launch {
            repository.clearAllSeats(userId)
        }
    }

    // Load reference seating chart directly from Vietnamese classroom layout model
    fun loadReferenceSeatingChart() {
        viewModelScope.launch {
            val seats = SampleData.getReferenceSeatingChart()
            repository.clearAllSeats(userId)
            repository.saveAllSeats(userId, seats)
        }
    }

    // Settings
    fun saveSettings(newSettings: ClassSettings) {
        viewModelScope.launch {
            repository.saveSettings(userId, newSettings)
        }
    }

    // Seed sample data
    fun seedSampleData() {
        viewModelScope.launch {
            repository.seedInitialData(
                userId = userId,
                students = SampleData.getInitialStudents(),
                records = SampleData.getInitialRecords()
            )
        }
    }
}
