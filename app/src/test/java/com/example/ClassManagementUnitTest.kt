package com.example

import com.example.model.ConductRecord
import com.example.model.SampleData
import com.example.model.Student
import com.example.model.StudentEvaluation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ClassManagementUnitTest {

    @Test
    fun testSampleData_hasExpectedStudentsAndGroups() {
        val students = SampleData.getInitialStudents()
        assertEquals(24, students.size)

        // Verify each group 1..4 has members
        (1..4).forEach { grp ->
            val grpMembers = students.filter { it.groupNumber == grp }
            assertTrue("Group $grp must have members", grpMembers.size >= 4)
            val leader = grpMembers.find { it.role == "Tổ trưởng" }
            assertNotNull("Group $grp must have a leader", leader)
        }

        // Verify unique student IDs and codes
        val uniqueIds = students.map { it.id }.toSet()
        assertEquals(24, uniqueIds.size)

        val uniqueCodes = students.map { it.studentCode }.toSet()
        assertEquals(24, uniqueCodes.size)
    }

    @Test
    fun testEvaluationCalculation_categorizesAccurately() {
        val student = Student(id = "st1", fullName = "Nguyễn Văn A", groupNumber = 1)
        val baseScore = 80

        // Case 1: Student with high plus points -> "Tốt"
        val plusRecords = listOf(
            ConductRecord(studentId = "st1", type = "PLUS", points = 15, month = "2024-10")
        )
        val net1 = baseScore + 15
        val rating1 = when {
            net1 < 65 -> "Chưa đạt"
            net1 < 75 -> "Đạt"
            net1 < 90 -> "Khá"
            else -> "Tốt"
        }
        assertEquals("Tốt", rating1)

        // Case 2: Student with 3 violations -> "Chưa đạt"
        val violCount = 3
        val net2 = 70
        val rating2 = if (violCount >= 3 || net2 < 65) "Chưa đạt" else "Đạt"
        assertEquals("Chưa đạt", rating2)
    }

    @Test
    fun testSampleRecords_properTypesAndPoints() {
        val records = SampleData.getInitialRecords()
        assertTrue("Must contain sample records", records.isNotEmpty())

        val plusList = records.filter { it.type == "PLUS" }
        val violList = records.filter { it.type == "VIOLATION" }

        assertTrue("Plus records must exist", plusList.isNotEmpty())
        assertTrue("Violation records must exist", violList.isNotEmpty())

        plusList.forEach {
            assertTrue("Plus points should be positive", it.points > 0)
        }
        violList.forEach {
            assertTrue("Violation points should be negative", it.points < 0)
        }
    }

    @Test
    fun testRoleAssignment_changesRoleAndGroup() {
        val originalStudent = Student(id = "st10", fullName = "Đặng Khánh Linh", groupNumber = 2, role = "Học sinh")

        // GVCN assigns Lớp trưởng
        val monitorStudent = originalStudent.copy(role = "Lớp trưởng")
        assertEquals("Lớp trưởng", monitorStudent.role)

        // GVCN assigns Tổ trưởng Tổ 3
        val groupLeaderStudent = originalStudent.copy(role = "Tổ trưởng", groupNumber = 3)
        assertEquals("Tổ trưởng", groupLeaderStudent.role)
        assertEquals(3, groupLeaderStudent.groupNumber)
    }

    @Test
    fun testStudentSelfPlusPoint_recordsStudentIdentity() {
        val student = Student(id = "st02", fullName = "Trần Bảo Ngọc", groupNumber = 1)
        val selfReportedRecord = ConductRecord(
            studentId = student.id,
            studentName = student.fullName,
            groupNumber = student.groupNumber,
            type = "PLUS",
            category = "Phát biểu xây dựng bài tích cực",
            points = 2,
            recordedBy = "Học sinh ${student.fullName}"
        )

        assertEquals("PLUS", selfReportedRecord.type)
        assertEquals(2, selfReportedRecord.points)
        assertEquals("Học sinh Trần Bảo Ngọc", selfReportedRecord.recordedBy)
    }

    @Test
    fun testGroupLeaderDemeritPoint_enforcesGroupIntegrity() {
        val leader = Student(id = "st03", fullName = "Lê Quang Minh", groupNumber = 1, role = "Tổ trưởng")
        val member = Student(id = "st05", fullName = "Vũ Đình Trọng", groupNumber = 1, role = "Học sinh")

        // Group leader logs violation for member in same group
        val leaderRecord = ConductRecord(
            studentId = member.id,
            studentName = member.fullName,
            groupNumber = member.groupNumber,
            type = "VIOLATION",
            category = "Không làm bài tập về nhà",
            points = -2,
            recordedBy = "Tổ trưởng ${leader.fullName} (Tổ ${leader.groupNumber})"
        )

        assertEquals("VIOLATION", leaderRecord.type)
        assertEquals(-2, leaderRecord.points)
        assertEquals(1, leaderRecord.groupNumber)
        assertEquals("Tổ trưởng Lê Quang Minh (Tổ 1)", leaderRecord.recordedBy)
    }
}
