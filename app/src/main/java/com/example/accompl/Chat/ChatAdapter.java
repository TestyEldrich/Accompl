package com.example.accompl.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accompl.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<ChatMessage> chatMessages;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_SENT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_container_sent_message, parent, false);
            return new SentMessageViewHolder(view);
        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_container_received_message, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }else{
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_RECEIVED;
        }else{
            return VIEW_TYPE_SENT;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        void setData(ChatMessage chatMessage){
            TextView textMessage = (TextView) mView.findViewById(R.id.textMessage);
            TextView textDateTime = (TextView) mView.findViewById(R.id.textDateTime);

            textMessage.setText(chatMessage.message);
            textDateTime.setText(chatMessage.dateTime);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        void setData(ChatMessage chatMessage){
            TextView textMessage = (TextView) mView.findViewById(R.id.textMessage);
            TextView textDateTime = (TextView) mView.findViewById(R.id.textDateTime);

            textMessage.setText(chatMessage.message);
            textDateTime.setText(chatMessage.dateTime);
        }
    }
}
