package com.example.quanlydanhba;

import java.io.Serializable;
import java.util.Locale;

/**
 * Một liên hệ trong danh bạ. Implements Serializable để truyền qua Intent.
 */
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String UNKNOWN_INITIAL = "?";
    private static final int PHONE_LENGTH = 10;

    private String name;
    private String phone;
    private String group;
    private String gender;
    private boolean favorite;
    private String note;

    public Contact(String name, String phone, String group, String gender, boolean favorite, String note) {
        this.name = name;
        this.phone = phone;
        this.group = group;
        this.gender = gender;
        this.favorite = favorite;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Chữ cái đầu của TÊN (từ cuối cùng của họ tên), viết hoa.
     * Ví dụ: "Trần Thị Bình" -> "B".
     */
    public String getInitial() {
        String trimmedName = name == null ? "" : name.trim();
        if (trimmedName.isEmpty()) {
            return UNKNOWN_INITIAL;
        }
        String[] words = trimmedName.split("\\s+");
        String lastWord = words[words.length - 1];
        return lastWord.substring(0, 1).toUpperCase(Locale.ROOT);
    }

    /**
     * Số điện thoại dạng "0912 345 678" chỉ để hiển thị;
     * dữ liệu lưu, so sánh và gọi vẫn dùng chuỗi 10 số liền (getPhone).
     */
    public String getFormattedPhone() {
        if (phone == null || phone.length() != PHONE_LENGTH) {
            return phone;
        }
        return phone.substring(0, 4) + " " + phone.substring(4, 7) + " " + phone.substring(7);
    }
}
