package com.example.quanlydanhba;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import java.util.List;

/**
 * Adapter hiển thị từng liên hệ trong ListView theo layout item_contact.xml.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {

    // Bảng màu avatar, chọn xoay vòng theo vị trí trong danh sách
    private static final int[] AVATAR_COLORS = {
            R.color.avatar_1,
            R.color.avatar_2,
            R.color.avatar_3,
            R.color.avatar_4,
            R.color.avatar_5
    };

    public ContactAdapter(@NonNull Context context, @NonNull List<Contact> contacts) {
        super(context, R.layout.item_contact, contacts);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        ViewHolder holder;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
            holder = new ViewHolder(itemView);
            itemView.setTag(holder);
        } else {
            // Tái sử dụng view cũ để cuộn mượt hơn
            holder = (ViewHolder) itemView.getTag();
        }

        Contact contact = getItem(position);
        if (contact != null) {
            holder.bind(contact, position);
        }
        return itemView;
    }

    /** Tô màu nền avatar theo vị trí; dùng chung cho danh sách và màn hình chi tiết. */
    public static void applyAvatarColor(@NonNull View avatarView, int position) {
        int colorRes = AVATAR_COLORS[position % AVATAR_COLORS.length];
        int color = ContextCompat.getColor(avatarView.getContext(), colorRes);
        ViewCompat.setBackgroundTintList(avatarView, ColorStateList.valueOf(color));
    }

    private static class ViewHolder {
        final TextView tvAvatar;
        final TextView tvName;
        final TextView tvPhone;
        final TextView tvGroup;
        final TextView tvFavoriteStar;

        ViewHolder(View itemView) {
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvGroup = itemView.findViewById(R.id.tvGroup);
            tvFavoriteStar = itemView.findViewById(R.id.tvFavoriteStar);
        }

        void bind(Contact contact, int position) {
            tvAvatar.setText(contact.getInitial());
            applyAvatarColor(tvAvatar, position);
            tvName.setText(contact.getName());
            tvPhone.setText(contact.getFormattedPhone());
            tvGroup.setText(contact.getGroup());
            // Phải set cả 2 trạng thái vì view được tái sử dụng
            tvFavoriteStar.setVisibility(contact.isFavorite() ? View.VISIBLE : View.GONE);
        }
    }
}
