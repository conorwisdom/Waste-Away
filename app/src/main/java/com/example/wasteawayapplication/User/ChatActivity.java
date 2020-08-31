package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteawayapplication.Model.Messages;
import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ImageView userImage;
    private String msgRecipientID, msgRecipientName, msgRecipientUsername;
    private TextView userName, userUsername;
    private ImageButton sendMsgBtn;
    private EditText msgInput;
    private DatabaseReference rootRef;
    private Toolbar chatToolbar;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView usersMessagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabase.getInstance().getReference();

        msgRecipientID = getIntent().getExtras().get("visit_user_id").toString();
        msgRecipientName = getIntent().getExtras().get("visit_user_name").toString();
        msgRecipientUsername = getIntent().getExtras().get("visit_user_username").toString();

        InitalizeControllers();

        userName.setText(msgRecipientName);
        userUsername.setText(msgRecipientUsername);

//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });
    }

    private void InitalizeControllers() {

        chatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userImage = (ImageView) findViewById(R.id.chat_profile_image);
        userName = (TextView) findViewById(R.id.chat_profile_name);
        userUsername = (TextView) findViewById(R.id.chat_profile_username);

        sendMsgBtn = (ImageButton) findViewById(R.id.btn_send_msg);
        msgInput = (EditText) findViewById(R.id.user_message_input);

        messageAdapter = new MessageAdapter(messagesList);
        usersMessagesList = (RecyclerView) findViewById(R.id.user_messages_list);
        linearLayoutManager = new LinearLayoutManager(this);
        usersMessagesList.setLayoutManager(linearLayoutManager);
        usersMessagesList.setAdapter(messageAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("Messages").child(Prevalent.currentOnlineUser.getUsername()).child(msgRecipientID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();

                        usersMessagesList.smoothScrollToPosition(usersMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void SendMessage() {

        String msgText = msgInput.getText().toString();

        if (TextUtils.isEmpty(msgText)) {
            Toast.makeText(this, "Message Required", Toast.LENGTH_SHORT).show();
        } else {
            String msgSenderRef = "Messages/" + Prevalent.currentOnlineUser.getUsername() + "/" + msgRecipientID;
            String msgRecipientRef = "Messages/" + msgRecipientID + "/" + Prevalent.currentOnlineUser.getUsername();

            DatabaseReference userMsgKeyRef = rootRef.child("Message")
                    .child(Prevalent.currentOnlineUser.getUsername()).child(msgRecipientID).push();

            String msgPushID = userMsgKeyRef.getKey();

            Map msgTextBody = new HashMap();
            msgTextBody.put("message", msgText);
            msgTextBody.put("type", "text");
            msgTextBody.put("from", Prevalent.currentOnlineUser.getUsername());

            Map msgBodyDetails = new HashMap();
            msgBodyDetails.put(msgSenderRef + "/" + msgPushID, msgTextBody);
            msgBodyDetails.put(msgRecipientRef + "/" + msgPushID, msgTextBody);

            rootRef.updateChildren(msgBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    msgInput.setText("");
                }
            });

        }
    }
}