# 📘 Honda English - Ứng dụng học tiếng Anh qua flashcard và bài kiểm tra

Honda English là ứng dụng di động Android giúp người dùng học và ôn từ vựng tiếng Anh một cách hiệu quả thông qua flashcard, bài kiểm tra cá nhân hóa và theo dõi tiến độ học tập.

## 🚀 Tính năng chính

### 👨‍🎓 Chức năng dành cho người học
- Đăng ký và đăng nhập tài khoản an toàn
- Chọn danh mục từ vựng phù hợp và học từ mới qua flashcard (hiển thị từ tiếng Anh + nghĩa tiếng Việt)
- Ôn tập từ vựng đã học
- Làm bài kiểm tra với các dạng khác nhau để kiểm tra mức độ ghi nhớ
- Đặt và nhận nhắc nhở học tập hàng ngày
- Theo dõi tiến độ học tập và xem thống kê kết quả cá nhân (số câu đúng/tổng câu, từ đã thuộc, ...)
- Cập nhật thông tin cá nhân và mục tiêu học tập

### 👨‍🏫 Chức năng dành cho giáo viên
- Đầy đủ các chức năng của người học
- Thêm, chỉnh sửa, xóa danh mục và từ vựng trong hệ thống
- Báo cáo và thống kê kết quả học tập của học sinh

### ⚙️ Chức năng hệ thống
- Xác thực người dùng an toàn bằng **JWT** (JSON Web Token)
- Cung cấp **RESTful API** để giao tiếp giữa ứng dụng di động và backend
- Lưu trữ và quản lý dữ liệu bằng **MySQL**

## 🛠 Công nghệ sử dụng

### 📱 Frontend (Android)
- Ngôn ngữ: Java
- Kiến trúc: Activity + Fragment
- Thư viện: Retrofit + Gson (gọi API), SharedPreferences (lưu thông tin người dùng)
- UI: XML layout, ConstraintLayout, RecyclerView

### 🌐 Backend (Spring Boot)
- Ngôn ngữ: Java 17+
- Framework: Spring Boot, Spring Security + OAuth2 Resource Server + JWT
- Database: JPA/Hibernate (MySQL)
- Thư viện: Lombok, MapStruct
- Bảo mật: Phân quyền dựa trên vai trò (RBAC)

## 📂 Cấu trúc dự án
```text
EnglishVocabularyApp/
├── Honda_English_App/
│   ├── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── com/example/honda_english/
│   │   │   │   │       ├── activity/
│   │   │   │   │       ├── adapter/
│   │   │   │   │       ├── api/
│   │   │   │   │       ├── fragment/
│   │   │   │   │       ├── model/
│   │   │   │   │       ├── receiver/
│   │   │   │   │       └── util/
│   │   │   │   └── res/
│   │   └── build.gradle
│   └── settings.gradle
│
├── Honda_English_Backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/honda/englishapp/
│   │   │   │       ├── configuration/
│   │   │   │       ├── controller/
│   │   │   │       ├── dto/
│   │   │   │       ├── entity/
│   │   │   │       ├── enums/
│   │   │   │       ├── exception/
│   │   │   │       ├── mapper/
│   │   │   │       ├── repository/
│   │   │   │       └── service/
│   │   │   │       └── validator/
│   │   │   └── resources/
│   │   │       └── application.yaml
│   └── pom.xml
│
└── README.md
```
Tác giả

Tunganh192

Cảm ơn bạn đã xem dự án!