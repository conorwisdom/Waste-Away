package com.example.wasteawayapplication.User;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteawayapplication.Model.Messages;
import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private DatabaseReference usersRef;

    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMsgText, recipientMsgText;
        public ImageView recipientProfileImage, msgSenderImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsgText = (TextView) itemView.findViewById(R.id.tv_msg_sender);
            recipientMsgText = (TextView) itemView.findViewById(R.id.tv_msg_recipient);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_chat_layout, viewGroup, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {

        Messages messages = userMessagesList.get(i);

        String fromUserID = messages.getFrom();
        String fromMsgType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (fromMsgType.equals("text")) {
            messageViewHolder.recipientMsgText.setVisibility(View.INVISIBLE);
            messageViewHolder.senderMsgText.setVisibility(View.INVISIBLE);

            if(fromUserID.equals(Prevalent.currentOnlineUser.getUsername())) {
                messageViewHolder.senderMsgText.setVisibility(View.VISIBLE);

                messageViewHolder.senderMsgText.setBackgroundResource(R.drawable.message_sender_background);
                messageViewHolder.senderMsgText.setTextColor(Color.BLACK);
                messageViewHolder.senderMsgText.setText((messages.getMessage()));
            } else {
                messageViewHolder.recipientMsgText.setVisibility(View.VISIBLE);

                messageViewHolder.recipientMsgText.setBackgroundResource(R.drawable.message_recipient_background);
                messageViewHolder.recipientMsgText.setTextColor(Color.BLACK);
                messageViewHolder.recipientMsgText.setText((messages.getMessage()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }



}
