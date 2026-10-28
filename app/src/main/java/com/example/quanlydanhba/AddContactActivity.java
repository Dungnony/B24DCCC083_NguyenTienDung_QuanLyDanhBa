package com.example.quanlydanhba;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class AddContactActivity extends AppCompatActivity {

    public static final String EXTRA_EXISTING_PHONES = "com.example.quanlydanhba.EXTRA_EXISTING_PHONES";
    public static final String EXTRA_CONTACT = "com.example.quanlydanhba.EXTRA_NEW_CONTACT";

    // 10 chữ số, bắt đầu bằng 0
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");

    private EditText edtName;
    private EditText edtPhone;
    private Spinner spinnerGroup;
    private RadioGroup radioGroupGender;
    private CheckBox cbAddFavorite;
    private EditText edtNote;

    private ArrayList<String> existingPhones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        spinnerGroup = findViewById(R.id.spinnerGroup);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        cbAddFavorite = findViewById(R.id.cbAddFavorite);
        edtNote = findViewById(R.id.edtNote);
        Button btnSaveContact = findViewById(R.id.btnSaveContact);

        ArrayList<String> phones = getIntent().getStringArrayListExtra(EXTRA_EXISTING_PHONES);
        existingPhones = phones != null ? phones : new ArrayList<>();

        btnSaveContact.setOnClickListener(v -> saveContact());
    }

    // Mũi tên trên thanh tiêu đề: chỉ đóng màn hình, không tạo lại MainActivity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveContact() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // Kiểm tra lần lượt, dừng ở lỗi đầu tiên
        if (name.isEmpty()) {
            showFieldError(edtName, R.string.error_name_empty);
            return;
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            showFieldError(edtPhone, R.string.error_phone_invalid);
            return;
        }
        if (existingPhones.contains(phone)) {
            showFieldError(edtPhone, R.string.error_phone_duplicate);
            return;
        }

        Contact contact = new Contact(
                name,
                phone,
                spinnerGroup.getSelectedItem().toString(),
                getSelectedGender(),
                cbAddFavorite.isChecked(),
                edtNote.getText().toString().trim());

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_CONTACT, contact);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private String getSelectedGender() {
        int checkedId = radioGroupGender.getCheckedRadioButtonId();
        if (checkedId == R.id.radioFemale) {
            return getString(R.string.gender_female);
        }
        if (checkedId == R.id.radioOther) {
            return getString(R.string.gender_other);
        }
        return getString(R.string.gender_male);
    }

    private void showFieldError(EditText field, @StringRes int messageRes) {
        field.setError(getString(messageRes));
        field.requestFocus();
    }
}
