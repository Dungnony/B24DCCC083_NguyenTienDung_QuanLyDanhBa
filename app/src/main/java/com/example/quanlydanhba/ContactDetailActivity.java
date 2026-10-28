package com.example.quanlydanhba;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;

public class ContactDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT = "com.example.quanlydanhba.EXTRA_CONTACT";
    public static final String EXTRA_POSITION = "com.example.quanlydanhba.EXTRA_POSITION";
    public static final String EXTRA_ACTION = "com.example.quanlydanhba.EXTRA_ACTION";

    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";

    private static final int INVALID_POSITION = -1;
    private static final String TEL_SCHEME = "tel:";

    private Contact contact;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // IntentCompat xử lý giúp API 33+ (getSerializableExtra cũ đã deprecated)
        contact = IntentCompat.getSerializableExtra(getIntent(), EXTRA_CONTACT, Contact.class);
        position = getIntent().getIntExtra(EXTRA_POSITION, INVALID_POSITION);
        // Thiếu dữ liệu thì không có gì để hiển thị: đóng màn hình thay vì crash
        if (contact == null || position == INVALID_POSITION) {
            finish();
            return;
        }

        bindContact();

        Button btnCall = findViewById(R.id.btnCall);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDeleteContact = findViewById(R.id.btnDeleteContact);
        btnCall.setOnClickListener(v -> dialPhone());
        btnBack.setOnClickListener(v -> finish());
        btnDeleteContact.setOnClickListener(v -> deleteContact());
    }

    // Mũi tên trên thanh tiêu đề: chỉ đóng màn hình, giữ nguyên kết quả đã setResult
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void bindContact() {
        TextView tvAvatar = findViewById(R.id.tvDetailAvatar);
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvPhone = findViewById(R.id.tvDetailPhone);
        TextView tvGroup = findViewById(R.id.tvDetailGroup);
        TextView tvGender = findViewById(R.id.tvDetailGender);
        TextView tvNote = findViewById(R.id.tvDetailNote);
        CheckBox cbFavorite = findViewById(R.id.cbDetailFavorite);

        tvAvatar.setText(contact.getInitial());
        ContactAdapter.applyAvatarColor(tvAvatar, position);
        tvName.setText(contact.getName());
        tvPhone.setText(contact.getFormattedPhone());
        tvGroup.setText(contact.getGroup());
        tvGender.setText(contact.getGender());

        String note = contact.getNote();
        if (note == null || note.trim().isEmpty()) {
            tvNote.setText(R.string.no_note);
        } else {
            tvNote.setText(note);
        }

        // Set trạng thái trước rồi mới gắn listener, để không gửi kết quả khi chưa đổi gì
        cbFavorite.setChecked(contact.isFavorite());
        cbFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            contact.setFavorite(isChecked);
            setUpdateResult();
        });
    }

    /**
     * Gửi kết quả ngay khi đổi yêu thích, nên dù thoát bằng nút Quay lại,
     * nút Back hệ thống hay mũi tên trên thanh tiêu đề thì Main vẫn cập nhật.
     */
    private void setUpdateResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_ACTION, ACTION_UPDATE);
        resultIntent.putExtra(EXTRA_CONTACT, contact);
        resultIntent.putExtra(EXTRA_POSITION, position);
        setResult(RESULT_OK, resultIntent);
    }

    private void deleteContact() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_ACTION, ACTION_DELETE);
        resultIntent.putExtra(EXTRA_POSITION, position);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // ACTION_DIAL chỉ mở màn hình quay số nên không cần quyền CALL_PHONE
    private void dialPhone() {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(TEL_SCHEME + contact.getPhone()));
        try {
            startActivity(dialIntent);
        } catch (ActivityNotFoundException e) {
            // Một số máy ảo/máy tính bảng không có ứng dụng gọi điện
            Toast.makeText(this, R.string.error_no_dialer, Toast.LENGTH_SHORT).show();
        }
    }
}
