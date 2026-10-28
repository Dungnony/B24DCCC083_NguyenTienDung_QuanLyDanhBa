package com.example.quanlydanhba;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Dữ liệu chỉ nằm trong bộ nhớ khi app đang chạy
    private final ArrayList<Contact> contacts = new ArrayList<>();

    private ContactAdapter adapter;
    private ListView listContacts;
    private TextView tvTotalCount;
    private TextView tvFavoriteCount;

    // Chặn mở trùng màn hình con khi người dùng bấm liên tục
    private boolean isChildScreenOpening = false;

    private final ActivityResultLauncher<Intent> addContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::handleAddContactResult);

    private final ActivityResultLauncher<Intent> contactDetailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::handleContactDetailResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvFavoriteCount = findViewById(R.id.tvFavoriteCount);
        listContacts = findViewById(R.id.listContacts);
        Button btnAddContact = findViewById(R.id.btnAddContact);

        addSampleContacts();

        adapter = new ContactAdapter(this, contacts);
        listContacts.setAdapter(adapter);
        listContacts.setEmptyView(findViewById(R.id.tvEmpty));
        listContacts.setOnItemClickListener((parent, view, position, id) -> openContactDetail(position));

        btnAddContact.setOnClickListener(v -> openAddContact());

        updateStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChildScreenOpening = false;
    }

    private void addSampleContacts() {
        contacts.add(new Contact("Nguyễn Văn An", "0901234567",
                getString(R.string.group_friends), getString(R.string.gender_male), false, ""));
        contacts.add(new Contact("Trần Thị Bình", "0912345678",
                getString(R.string.group_family), getString(R.string.gender_female), true,
                "Chị gái, hay gọi buổi tối"));
        contacts.add(new Contact("Lê Minh Châu", "0987654321",
                getString(R.string.group_work), getString(R.string.gender_female), false, ""));
    }

    /** Đếm trực tiếp từ danh sách để thống kê luôn khớp dữ liệu. */
    private void updateStats() {
        int favoriteCount = 0;
        for (Contact contact : contacts) {
            if (contact.isFavorite()) {
                favoriteCount++;
            }
        }
        tvTotalCount.setText(String.valueOf(contacts.size()));
        tvFavoriteCount.setText(String.valueOf(favoriteCount));
    }

    private void openAddContact() {
        if (isChildScreenOpening) {
            return;
        }
        isChildScreenOpening = true;

        // Gửi danh sách SĐT hiện có để màn hình Thêm kiểm tra trùng
        ArrayList<String> existingPhones = new ArrayList<>();
        for (Contact contact : contacts) {
            existingPhones.add(contact.getPhone());
        }
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putStringArrayListExtra(AddContactActivity.EXTRA_EXISTING_PHONES, existingPhones);
        addContactLauncher.launch(intent);
    }

    private void openContactDetail(int position) {
        if (isChildScreenOpening) {
            return;
        }
        isChildScreenOpening = true;

        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra(ContactDetailActivity.EXTRA_CONTACT, contacts.get(position));
        intent.putExtra(ContactDetailActivity.EXTRA_POSITION, position);
        contactDetailLauncher.launch(intent);
    }

    private void handleAddContactResult(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() != RESULT_OK || data == null) {
            return;
        }
        // IntentCompat xử lý giúp API 33+ (getSerializableExtra cũ đã deprecated)
        Contact newContact = IntentCompat.getSerializableExtra(
                data, AddContactActivity.EXTRA_CONTACT, Contact.class);
        if (newContact == null) {
            return;
        }

        contacts.add(newContact);
        adapter.notifyDataSetChanged();
        updateStats();
        listContacts.setSelection(contacts.size() - 1);
        Toast.makeText(this, R.string.toast_contact_added, Toast.LENGTH_SHORT).show();
    }

    private void handleContactDetailResult(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() != RESULT_OK || data == null) {
            return;
        }
        String action = data.getStringExtra(ContactDetailActivity.EXTRA_ACTION);
        int position = data.getIntExtra(ContactDetailActivity.EXTRA_POSITION, -1);
        // Bảo vệ khỏi vị trí không hợp lệ để không bị IndexOutOfBoundsException
        if (position < 0 || position >= contacts.size()) {
            return;
        }

        if (ContactDetailActivity.ACTION_DELETE.equals(action)) {
            contacts.remove(position);
        } else if (ContactDetailActivity.ACTION_UPDATE.equals(action)) {
            Contact updatedContact = IntentCompat.getSerializableExtra(
                    data, ContactDetailActivity.EXTRA_CONTACT, Contact.class);
            if (updatedContact == null) {
                return;
            }
            contacts.set(position, updatedContact);
        } else {
            return;
        }

        adapter.notifyDataSetChanged();
        updateStats();
    }
}
