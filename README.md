# Ứng dụng Quản lý danh bạ liên hệ

Bài tập Android (Java + XML): đăng nhập, xem danh sách liên hệ, thêm, xem chi tiết, đánh dấu yêu thích, gọi điện và xóa liên hệ.

- **Sinh viên:** Họ Tên – MSSV
- **Package:** `com.example.quanlydanhba`

## Chức năng

| Màn hình | Nội dung |
|---|---|
| **Đăng nhập** (`LoginActivity`) | Kiểm tra tài khoản cố định. Báo lỗi khi để trống hoặc sai tài khoản. Đăng nhập thành công thì chuyển sang danh sách, bấm Back không quay lại màn đăng nhập. |
| **Danh bạ của tôi** (`MainActivity`) | Thống kê *Tổng liên hệ* và *Yêu thích*. `ListView` hiển thị avatar chữ cái đầu, họ tên, số điện thoại, nhóm và ★ yêu thích. Danh sách rỗng thì hiện "Chưa có liên hệ nào". |
| **Thêm liên hệ** (`AddContactActivity`) | Nhập họ tên, số điện thoại, nhóm (Spinner), giới tính (RadioGroup), yêu thích (CheckBox), ghi chú nhiều dòng. Kiểm tra tên trống, số điện thoại sai định dạng và số điện thoại trùng. |
| **Chi tiết liên hệ** (`ContactDetailActivity`) | Xem đầy đủ thông tin, bật/tắt yêu thích, gọi điện (mở màn hình quay số), xóa liên hệ. |

**Tài khoản mẫu:** `admin` / `123456`

📖 Hướng dẫn sử dụng chi tiết từng màn hình: xem [HUONG_DAN_SU_DUNG.md](HUONG_DAN_SU_DUNG.md).

## Công nghệ

- Android Studio, Java, giao diện XML, `findViewById`
- minSdk 24, compileSdk/targetSdk 34
- AndroidX AppCompat + Material Components (theme `Theme.MaterialComponents.Light.DarkActionBar`)
- Chuyển màn hình và truyền dữ liệu bằng `Intent`. Màn hình con trả kết quả về `MainActivity` qua Activity Result API (`registerForActivityResult`).
- Dữ liệu chỉ lưu trong `ArrayList<Contact>` khi app đang chạy, không dùng cơ sở dữ liệu hay `SharedPreferences`.

## Luồng dữ liệu

```
LoginActivity ──(đúng tài khoản, finish)──► MainActivity
                                              │  ▲
             danh sách SĐT hiện có ──────────┤  ├── RESULT_OK + Contact mới
                                              ▼  │
                                       AddContactActivity

                                    MainActivity
                                              │  ▲
                   Contact + position ────────┤  ├── "update" + Contact + position
                                              ▼  │   "delete" + position
                                     ContactDetailActivity
```

- Kết quả "update" được gửi ngay khi người dùng đổi trạng thái yêu thích. Vì vậy dù thoát bằng nút **Quay lại**, nút **Back** hệ thống hay **mũi tên** trên thanh tiêu đề, danh sách và thống kê vẫn được cập nhật.
- Số điện thoại hiển thị dạng `0912 345 678` cho dễ đọc, nhưng khi lưu, so sánh và gọi đều dùng chuỗi liền 10 số.

## Cấu trúc thư mục

```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/quanlydanhba/
│   ├── Contact.java                 # Lớp dữ liệu (Serializable)
│   ├── ContactAdapter.java          # ArrayAdapter cho ListView
│   ├── LoginActivity.java
│   ├── MainActivity.java
│   ├── AddContactActivity.java
│   └── ContactDetailActivity.java
└── res/
    ├── layout/    # activity_login, activity_main, item_contact,
    │              # activity_add_contact, activity_contact_detail
    ├── values/    # strings, colors, themes, arrays, dimens
    └── drawable/  # bg_avatar, bg_logo, bg_chip, bg_input, ic_phone
```

## Cách chạy

1. Clone repo về một thư mục có **đường dẫn không dấu**, ví dụ `C:\AndroidProjects\QuanLyDanhBa` (Gradle trên Windows có thể lỗi nếu đường dẫn chứa ký tự tiếng Việt):
   ```bash
   git clone https://github.com/<username>/MSSV_HoTen_QuanLyDanhBa.git
   ```
2. Mở Android Studio → **File › Open** → chọn thư mục vừa clone.
3. Đợi Gradle sync xong. Nếu chưa tự chạy thì vào **File › Sync Project with Gradle Files**.
4. Chọn emulator hoặc điện thoại thật, bấm **Run ▶**.

## Kiểm thử thủ công

| # | Thao tác | Kết quả mong đợi |
|---|---|---|
| 1 | Đăng nhập khi để trống | "Vui lòng nhập tên đăng nhập và mật khẩu" |
| 2 | Đăng nhập sai | "Sai tên đăng nhập hoặc mật khẩu" |
| 3 | Đăng nhập `admin` / `123456` | Vào danh sách. Tổng 3, Yêu thích 1 |
| 4 | Thêm liên hệ, để trống họ tên | "Họ tên không được để trống" |
| 5 | Thêm liên hệ, SĐT `12345` | "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0" |
| 6 | Thêm liên hệ, SĐT `0901234567` | "Số điện thoại đã tồn tại trong danh bạ" |
| 7 | Thêm liên hệ hợp lệ | Toast "Đã thêm liên hệ", danh sách và thống kê cập nhật |
| 8 | Chi tiết → đổi Yêu thích → thoát | ★ và số Yêu thích cập nhật |
| 9 | Chi tiết → Gọi điện | Mở màn hình quay số với đúng số điện thoại |
| 10 | Chi tiết → Xóa liên hệ | Liên hệ biến mất, thống kê giảm |
| 11 | Xóa hết liên hệ | Hiện "Chưa có liên hệ nào", thống kê 0 / 0 |

## Giới hạn

- Dữ liệu mất khi tắt app (đúng yêu cầu đề bài). Các Activity khóa hướng dọc nên xoay màn hình không làm mất dữ liệu. Tuy nhiên, đổi chế độ sáng/tối hoặc cỡ chữ của hệ thống trong lúc app đang mở sẽ đưa danh sách về dữ liệu mẫu.
- Tài khoản đăng nhập được ghi cố định trong code, chỉ dùng cho mục đích học tập.
