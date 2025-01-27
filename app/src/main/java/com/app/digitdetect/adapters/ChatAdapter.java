package com.app.digitdetect.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.digitdetect.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_USER = 0;
    public static final int TYPE_SERVER = 1;

    private final Context context;
    private final List<ChatMessage> chatMessages;

    public ChatAdapter(Context context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_user_message, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_server_message, parent, false);
            return new ServerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        if (message.getType() == TYPE_USER) {
            ((UserViewHolder) holder).userMessage.setText(message.getText());
        } else {
            ((ServerViewHolder) holder).serverImage.setImageBitmap(message.getImage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userMessage;

        public UserViewHolder(View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.userMessage);
        }
    }

    public static class ServerViewHolder extends RecyclerView.ViewHolder {
        ImageView serverImage;

        public ServerViewHolder(View itemView) {
            super(itemView);
            serverImage = itemView.findViewById(R.id.serverImage);
        }
    }

    public static class ChatMessage {
        private final int type; // 0 for user, 1 for server
        private final String text;
        private final Bitmap image;

        public ChatMessage(int type, String text, Bitmap image) {
            this.type = type;
            this.text = text;
            this.image = image;
        }

        public int getType() {
            return type;
        }

        public String getText() {
            return text;
        }

        public Bitmap getImage() {
            return image;
        }
    }
}
