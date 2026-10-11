package com.example.model

object SampleData {
    fun getInitialStudents(): List<Student> = listOf(
        Student(id = "hs01", studentCode = "10C3-01", fullName = "Nguyễn Hoàng Nam", gender = "Nam", dateOfBirth = "2009-03-15", phone = "0912345601", address = "Phường 1, Quận 5", groupNumber = 1, role = "Lớp trưởng"),
        Student(id = "hs02", studentCode = "10C3-02", fullName = "Trần Bảo Ngọc", gender = "Nữ", dateOfBirth = "2009-07-22", phone = "0912345602", address = "Phường 4, Quận 3", groupNumber = 1, role = "Lớp phó học tập"),
        Student(id = "hs03", studentCode = "10C3-03", fullName = "Lê Quang Minh", gender = "Nam", dateOfBirth = "2009-01-10", phone = "0912345603", address = "Phường 2, Quận 10", groupNumber = 1, role = "Tổ trưởng"),
        Student(id = "hs04", studentCode = "10C3-04", fullName = "Phạm Quỳnh Anh", gender = "Nữ", dateOfBirth = "2009-11-05", phone = "0912345604", address = "Phường 8, Quận 1", groupNumber = 1, role = "Học sinh"),
        Student(id = "hs05", studentCode = "10C3-05", fullName = "Vũ Đình Trọng", gender = "Nam", dateOfBirth = "2009-05-18", phone = "0912345605", address = "Phường 11, Quận Tân Bình", groupNumber = 1, role = "Học sinh"),
        Student(id = "hs06", studentCode = "10C3-06", fullName = "Đỗ Thảo Vy", gender = "Nữ", dateOfBirth = "2009-09-30", phone = "0912345606", address = "Phường 5, Quận 5", groupNumber = 1, role = "Học sinh"),

        Student(id = "hs07", studentCode = "10C3-07", fullName = "Bùi Anh Tuấn", gender = "Nam", dateOfBirth = "2009-02-14", phone = "0912345607", address = "Phường 7, Quận 3", groupNumber = 2, role = "Tổ trưởng"),
        Student(id = "hs08", studentCode = "10C3-08", fullName = "Hoàng Mai Chi", gender = "Nữ", dateOfBirth = "2009-08-19", phone = "0912345608", address = "Phường 14, Quận 10", groupNumber = 2, role = "Bí thư"),
        Student(id = "hs09", studentCode = "10C3-09", fullName = "Ngô Gia Bảo", gender = "Nam", dateOfBirth = "2009-06-25", phone = "0912345609", address = "Phường 3, Quận 5", groupNumber = 2, role = "Học sinh"),
        Student(id = "hs10", studentCode = "10C3-10", fullName = "Đặng Khánh Linh", gender = "Nữ", dateOfBirth = "2009-12-08", phone = "0912345610", address = "Phường 9, Quận 1", groupNumber = 2, role = "Học sinh"),
        Student(id = "hs11", studentCode = "10C3-11", fullName = "Dương Quốc Huy", gender = "Nam", dateOfBirth = "2009-04-03", phone = "0912345611", address = "Phường 6, Quận 8", groupNumber = 2, role = "Học sinh"),
        Student(id = "hs12", studentCode = "10C3-12", fullName = "Lý Phương Thảo", gender = "Nữ", dateOfBirth = "2009-10-14", phone = "0912345612", address = "Phường 12, Quận 10", groupNumber = 2, role = "Học sinh"),

        Student(id = "hs13", studentCode = "10C3-13", fullName = "Võ Thành Đạt", gender = "Nam", dateOfBirth = "2009-01-28", phone = "0912345613", address = "Phường 2, Quận 5", groupNumber = 3, role = "Tổ trưởng"),
        Student(id = "hs14", studentCode = "10C3-14", fullName = "Huỳnh Mỹ Duyên", gender = "Nữ", dateOfBirth = "2009-07-09", phone = "0912345614", address = "Phường 15, Quận Tân Bình", groupNumber = 3, role = "Lớp phó phong trào"),
        Student(id = "hs15", studentCode = "10C3-15", fullName = "Mai Thanh Phong", gender = "Nam", dateOfBirth = "2009-09-11", phone = "0912345615", address = "Phường 10, Quận 3", groupNumber = 3, role = "Học sinh"),
        Student(id = "hs16", studentCode = "10C3-16", fullName = "Trương Ánh Tuyết", gender = "Nữ", dateOfBirth = "2009-03-27", phone = "0912345616", address = "Phường 1, Quận 1", groupNumber = 3, role = "Học sinh"),
        Student(id = "hs17", studentCode = "10C3-17", fullName = "Phan Hữu Phước", gender = "Nam", dateOfBirth = "2009-11-23", phone = "0912345617", address = "Phường 8, Quận 11", groupNumber = 3, role = "Học sinh"),
        Student(id = "hs18", studentCode = "10C3-18", fullName = "Lâm Tuyết Nhung", gender = "Nữ", dateOfBirth = "2009-05-02", phone = "0912345618", address = "Phường 4, Quận 5", groupNumber = 3, role = "Học sinh"),

        Student(id = "hs19", studentCode = "10C3-19", fullName = "Đinh Khắc Cường", gender = "Nam", dateOfBirth = "2009-04-16", phone = "0912345619", address = "Phường 13, Quận 10", groupNumber = 4, role = "Tổ trưởng"),
        Student(id = "hs20", studentCode = "10C3-20", fullName = "Nguyễn Ngọc Bích", gender = "Nữ", dateOfBirth = "2009-08-31", phone = "0912345620", address = "Phường 3, Quận 3", groupNumber = 4, role = "Lớp phó lao động"),
        Student(id = "hs21", studentCode = "10C3-21", fullName = "Hồ Nhật Nam", gender = "Nam", dateOfBirth = "2009-02-05", phone = "0912345621", address = "Phường 11, Quận 5", groupNumber = 4, role = "Học sinh"),
        Student(id = "hs22", studentCode = "10C3-22", fullName = "Châu Kim Yến", gender = "Nữ", dateOfBirth = "2009-10-18", phone = "0912345622", address = "Phường 6, Quận Tân Bình", groupNumber = 4, role = "Học sinh"),
        Student(id = "hs23", studentCode = "10C3-23", fullName = "Cao Việt Hoàng", gender = "Nam", dateOfBirth = "2009-06-12", phone = "0912345623", address = "Phường 7, Quận 10", groupNumber = 4, role = "Học sinh"),
        Student(id = "hs24", studentCode = "10C3-24", fullName = "Vương Hải Yến", gender = "Nữ", dateOfBirth = "2009-12-29", phone = "0912345624", address = "Phường 2, Quận 1", groupNumber = 4, role = "Học sinh")
    )

    fun getInitialRecords(): List<ConductRecord> = listOf(
        ConductRecord(id = "rec01", studentId = "hs02", studentName = "Trần Bảo Ngọc", groupNumber = 1, type = "PLUS", category = "Phát biểu xây dựng bài", points = 5, description = "Giải bài toán khó trên bảng môn Toán", date = "2024-10-02", month = "2024-10"),
        ConductRecord(id = "rec02", studentId = "hs01", studentName = "Nguyễn Hoàng Nam", groupNumber = 1, type = "PLUS", category = "Tham gia phong trào", points = 10, description = "Tổ chức tốt buổi sinh hoạt lớp đầu tuần", date = "2024-10-04", month = "2024-10"),
        ConductRecord(id = "rec03", studentId = "hs08", studentName = "Hoàng Mai Chi", groupNumber = 2, type = "PLUS", category = "Thành tích học tập", points = 10, description = "Đạt điểm 10 kiểm tra 1 tiết môn Hóa", date = "2024-10-07", month = "2024-10"),
        ConductRecord(id = "rec04", studentId = "hs05", studentName = "Vũ Đình Trọng", groupNumber = 1, type = "VIOLATION", category = "Đi học muộn", points = -3, description = "Đi muộn 15 phút tiết 1 buổi sáng", date = "2024-10-03", month = "2024-10", resolved = false, actionTaken = "Nhắc nhở trước lớp"),
        ConductRecord(id = "rec05", studentId = "hs09", studentName = "Ngô Gia Bảo", groupNumber = 2, type = "VIOLATION", category = "Sử dụng điện thoại", points = -5, description = "Sử dụng điện thoại trong giờ Ngữ văn", date = "2024-10-05", month = "2024-10", resolved = true, actionTaken = "Tạm giữ điện thoại trả cuối buổi"),
        ConductRecord(id = "rec06", studentId = "hs05", studentName = "Vũ Đình Trọng", groupNumber = 1, type = "VIOLATION", category = "Không thuộc bài", points = -5, description = "Không soạn bài môn Lịch sử", date = "2024-10-08", month = "2024-10", resolved = false, actionTaken = "Chép phạt bài học"),
        ConductRecord(id = "rec07", studentId = "hs05", studentName = "Vũ Đình Trọng", groupNumber = 1, type = "VIOLATION", category = "Vi phạm đồng phục", points = -2, description = "Không đeo khăn quàng / phù hiệu trường", date = "2024-10-09", month = "2024-10", resolved = false, actionTaken = "Ghi sổ đầu bài, nhắc nhở"),
        ConductRecord(id = "rec08", studentId = "hs14", studentName = "Huỳnh Mỹ Duyên", groupNumber = 3, type = "PLUS", category = "Tham gia phong trào", points = 5, description = "Hỗ trợ chuẩn bị văn nghệ 20/10", date = "2024-10-08", month = "2024-10"),
        ConductRecord(id = "rec09", studentId = "hs21", studentName = "Hồ Nhật Nam", groupNumber = 4, type = "VIOLATION", category = "Nói chuyện riêng", points = -2, description = "Mất trật tự trong tiết Vật lý", date = "2024-10-04", month = "2024-10", resolved = true, actionTaken = "Nhắc nhở")
    )

    fun getReferenceSeatingChart(): List<SeatAssignment> = listOf(
        // Dãy 1 (Tổ 1)
        SeatAssignment("1_1_L", row = 1, col = 1, position = "L", studentName = "Hân", groupNumber = 1),
        SeatAssignment("1_1_R", row = 1, col = 1, position = "R", studentName = "Khôi", groupNumber = 1),
        SeatAssignment("1_2_L", row = 2, col = 1, position = "L", studentName = "H. Ngọc", groupNumber = 1),
        SeatAssignment("1_2_R", row = 2, col = 1, position = "R", studentName = "Phúc", groupNumber = 1),
        SeatAssignment("1_3_L", row = 3, col = 1, position = "L", studentName = "Băng", groupNumber = 1),
        SeatAssignment("1_3_R", row = 3, col = 1, position = "R", studentName = "Nghĩa", groupNumber = 1),
        SeatAssignment("1_4_L", row = 4, col = 1, position = "L", studentName = "Giàu", groupNumber = 1),
        SeatAssignment("1_4_R", row = 4, col = 1, position = "R", studentName = "Phát", groupNumber = 1),
        SeatAssignment("1_5_L", row = 5, col = 1, position = "L", studentName = "Nhân", groupNumber = 1),
        SeatAssignment("1_5_R", row = 5, col = 1, position = "R", studentName = "Danh", groupNumber = 1),

        // Dãy 2 (Tổ 2)
        SeatAssignment("2_1_L", row = 1, col = 2, position = "L", studentName = "Gia", groupNumber = 2),
        SeatAssignment("2_1_R", row = 1, col = 2, position = "R", studentName = "Hy", groupNumber = 2),
        SeatAssignment("2_2_L", row = 2, col = 2, position = "L", studentName = "Khoa", groupNumber = 2),
        SeatAssignment("2_2_R", row = 2, col = 2, position = "R", studentName = "Thanh Thảo", groupNumber = 2),
        SeatAssignment("2_3_L", row = 3, col = 2, position = "L", studentName = "M.Ngọc", groupNumber = 2),
        SeatAssignment("2_3_R", row = 3, col = 2, position = "R", studentName = "Thùy Tiên", groupNumber = 2),
        SeatAssignment("2_4_L", row = 4, col = 2, position = "L", studentName = "Đ.Duy", groupNumber = 2),
        SeatAssignment("2_4_R", row = 4, col = 2, position = "R", studentName = "K.Ngọc", groupNumber = 2),
        SeatAssignment("2_5_L", row = 5, col = 2, position = "L", studentName = "Thọ", groupNumber = 2),
        SeatAssignment("2_5_R", row = 5, col = 2, position = "R", studentName = "Nữ", groupNumber = 2),

        // Dãy 3 (Tổ 3)
        SeatAssignment("3_1_L", row = 1, col = 3, position = "L", studentName = "Thịnh", groupNumber = 3),
        SeatAssignment("3_1_R", row = 1, col = 3, position = "R", studentName = "Thị Thảo", groupNumber = 3),
        SeatAssignment("3_2_L", row = 2, col = 3, position = "L", studentName = "Quý", groupNumber = 3),
        SeatAssignment("3_2_R", row = 2, col = 3, position = "R", studentName = "Bình", groupNumber = 3),
        SeatAssignment("3_3_L", row = 3, col = 3, position = "L", studentName = "Nhiểu", groupNumber = 3),
        SeatAssignment("3_3_R", row = 3, col = 3, position = "R", studentName = "K.Hưng", groupNumber = 3),
        SeatAssignment("3_4_L", row = 4, col = 3, position = "L", studentName = "V.Lợi", groupNumber = 3),
        SeatAssignment("3_4_R", row = 4, col = 3, position = "R", studentName = "H.Huy", groupNumber = 3),
        SeatAssignment("3_5_L", row = 5, col = 3, position = "L", studentName = "V.Duy", groupNumber = 3),
        SeatAssignment("3_5_R", row = 5, col = 3, position = "R", studentName = "T.Vy", groupNumber = 3),

        // Dãy 4 (Tổ 4)
        SeatAssignment("4_1_L", row = 1, col = 4, position = "L", studentName = "Chí Viễn", groupNumber = 4),
        SeatAssignment("4_1_R", row = 1, col = 4, position = "R", studentName = "Gấm", groupNumber = 4),
        SeatAssignment("4_2_L", row = 2, col = 4, position = "L", studentName = "Lan Anh", groupNumber = 4),
        SeatAssignment("4_2_R", row = 2, col = 4, position = "R", studentName = "N.Duy", groupNumber = 4),
        SeatAssignment("4_3_L", row = 3, col = 4, position = "L", studentName = "N. Hưng", groupNumber = 4),
        SeatAssignment("4_3_R", row = 3, col = 4, position = "R", studentName = "Bảo", groupNumber = 4),
        SeatAssignment("4_4_L", row = 4, col = 4, position = "L", studentName = "Nhã Lam", groupNumber = 4),
        SeatAssignment("4_4_R", row = 4, col = 4, position = "R", studentName = "Tấn", groupNumber = 4),
        SeatAssignment("4_5_L", row = 5, col = 4, position = "L", studentName = "Yến Vy", groupNumber = 4)
    )
}
