package com.example.data

import com.example.model.ClassSettings
import com.example.model.ConductRecord
import com.example.model.SeatAssignment
import com.example.model.Student
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ClassRepository(private val db: FirebaseFirestore) {

    // Observe students
    fun observeStudents(userId: String): Flow<List<Student>> =
        db.collection("users").document(userId).collection("students")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(Student::class.java) }

    // Observe conduct records
    fun observeRecords(userId: String): Flow<List<ConductRecord>> =
        db.collection("users").document(userId).collection("records")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(ConductRecord::class.java) }

    // Observe seating assignments
    fun observeSeating(userId: String): Flow<List<SeatAssignment>> =
        db.collection("users").document(userId).collection("seating")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(SeatAssignment::class.java) }

    // Observe class settings
    fun observeSettings(userId: String): Flow<ClassSettings> =
        db.collection("users").document(userId).collection("settings").document("class_info")
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(ClassSettings::class.java) ?: ClassSettings()
            }

    // Upsert Student
    suspend fun saveStudent(userId: String, student: Student) {
        val docId = if (student.id.isBlank()) db.collection("users").document(userId).collection("students").document().id else student.id
        val itemToSave = student.copy(id = docId)
        db.collection("users").document(userId).collection("students").document(docId).set(itemToSave).await()
    }

    // Assign student role (Lớp trưởng, Tổ trưởng, etc.)
    suspend fun assignRole(userId: String, studentId: String, newRole: String, newGroup: Int? = null) {
        val docRef = db.collection("users").document(userId).collection("students").document(studentId)
        val updates = mutableMapOf<String, Any>("role" to newRole)
        if (newGroup != null) {
            updates["groupNumber"] = newGroup
        }
        docRef.update(updates).await()
    }

    // Delete Student
    suspend fun deleteStudent(userId: String, studentId: String) {
        db.collection("users").document(userId).collection("students").document(studentId).delete().await()
    }

    // Upsert Conduct Record
    suspend fun saveRecord(userId: String, record: ConductRecord) {
        val docId = if (record.id.isBlank()) db.collection("users").document(userId).collection("records").document().id else record.id
        val itemToSave = record.copy(id = docId)
        db.collection("users").document(userId).collection("records").document(docId).set(itemToSave).await()
    }

    // Toggle violation resolved
    suspend fun toggleRecordResolved(userId: String, recordId: String, currentResolved: Boolean) {
        db.collection("users").document(userId).collection("records").document(recordId)
            .update("resolved", !currentResolved).await()
    }

    // Delete Record
    suspend fun deleteRecord(userId: String, recordId: String) {
        db.collection("users").document(userId).collection("records").document(recordId).delete().await()
    }

    // Save Seat Assignment
    suspend fun saveSeat(userId: String, seat: SeatAssignment) {
        db.collection("users").document(userId).collection("seating").document(seat.id).set(seat).await()
    }

    // Save multiple seat assignments in a batch
    suspend fun saveAllSeats(userId: String, seats: List<SeatAssignment>) {
        val batch = db.batch()
        val seatingCol = db.collection("users").document(userId).collection("seating")
        for (seat in seats) {
            batch.set(seatingCol.document(seat.id), seat)
        }
        batch.commit().await()
    }

    // Clear seat assignment
    suspend fun clearSeat(userId: String, seatId: String) {
        db.collection("users").document(userId).collection("seating").document(seatId).delete().await()
    }

    // Clear all seat assignments
    suspend fun clearAllSeats(userId: String) {
        val snapshot = db.collection("users").document(userId).collection("seating").get().await()
        val batch = db.batch()
        for (doc in snapshot.documents) {
            batch.delete(doc.reference)
        }
        batch.commit().await()
    }

    // Save Settings
    suspend fun saveSettings(userId: String, settings: ClassSettings) {
        db.collection("users").document(userId).collection("settings").document("class_info").set(settings).await()
    }

    // Batch seed sample data
    suspend fun seedInitialData(userId: String, students: List<Student>, records: List<ConductRecord>) {
        val batch = db.batch()
        val studentsCol = db.collection("users").document(userId).collection("students")
        for (st in students) {
            batch.set(studentsCol.document(st.id), st)
        }
        val recordsCol = db.collection("users").document(userId).collection("records")
        for (rec in records) {
            batch.set(recordsCol.document(rec.id), rec)
        }
        val settingsDoc = db.collection("users").document(userId).collection("settings").document("class_info")
        batch.set(settingsDoc, ClassSettings())
        batch.commit().await()
    }
}
