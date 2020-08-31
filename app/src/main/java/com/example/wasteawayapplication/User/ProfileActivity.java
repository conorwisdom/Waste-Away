package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private String recipientUserID, current_state;

    private ImageView backBtn;
    private TextView profileName, profileUsername, profileEmail, profileAddress, profilePhone;
    private Button sendMsgRequestBtn, declineMsgRequestBtn;

    private DatabaseReference profileUserRef, chatRequestRef, contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        backBtn = (ImageView) findViewById(R.id.iv_profile_back);
        profileName = (TextView) findViewById(R.id.tv_profile_name);
        profileUsername = (TextView) findViewById(R.id.tv_profile_username);
        profileEmail = (TextView) findViewById(R.id.tv_profile_email);
        profileAddress = (TextView) findViewById(R.id.tv_profile_address);
        profilePhone = (TextView) findViewById(R.id.tv_profile_phone);
        sendMsgRequestBtn = (Button) findViewById(R.id.btn_send_message_request);
        declineMsgRequestBtn = (Button) findViewById(R.id.btn_decline_message_request);

        recipientUserID = getIntent().getExtras().get("username").toString();

        current_state = "new";

        retrieveUserInfo();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void retrieveUserInfo() {
        profileUserRef.child(recipientUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    String userProfileName = dataSnapshot.child("name").getValue().toString();
                    String userProfileUsername = dataSnapshot.child("username").getValue().toString();
                    String userProfileEmail = dataSnapshot.child("email").getValue().toString();
                    String userProfileAddress = dataSnapshot.child("address").getValue().toString();
                    String userProfileCity = dataSnapshot.child("city").getValue().toString();
                    String userProfilePostcode = dataSnapshot.child("postcode").getValue().toString();
                    String userProfilePhone = dataSnapshot.child("phone").getValue().toString();

                    profileName.setText("Name: " + userProfileName);
                    profileUsername.setText("Username: " + userProfileUsername);
                    profileEmail.setText("Email: " + userProfileEmail);
                    profileAddress.setText("Address: " + userProfileAddress + ", " + userProfileCity + ", "  + userProfilePostcode);
                    profilePhone.setText("Contact Number: " + userProfilePhone);
                    
                    ManageChatRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequests() {

        chatRequestRef.child(Prevalent.currentOnlineUser.getUsername())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(recipientUserID)) {
                            String request_type = dataSnapshot.child(recipientUserID)
                                    .child("request_type").getValue().toString();

                            if(request_type.equals("sent")) {
                                current_state = "request_sent";
                                sendMsgRequestBtn.setText("Cancel Message Request");
                            } else if (request_type.equals("received")){

                                current_state = "request_received";
                                sendMsgRequestBtn.setText("Accept Message Request");

                                declineMsgRequestBtn.setVisibility(View.VISIBLE);
                                declineMsgRequestBtn.setEnabled(true);
                                declineMsgRequestBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        CancelChatRequest();
                                    }
                                });
                            }
                        } else {
                            contactsRef.child(Prevalent.currentOnlineUser.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                                    if(dataSnapshot.hasChild(recipientUserID)) {
                                        current_state = "contacts";
                                        sendMsgRequestBtn.setText("Remove Contact");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if(!(Prevalent.currentOnlineUser.getUsername()).equals(recipientUserID)) {

            sendMsgRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sendMsgRequestBtn.setEnabled(false);
                    
                    if(current_state.equals("new")) {
                        SendChatRequest();
                    }

                    if(current_state.equals("request_sent")) {
                        CancelChatRequest();
                    }

                    if(current_state.equals("request_received")) {
                        AcceptChatRequest();
                    }

                    if(current_state.equals("contacts")) {
                        RemoveChatContact();
                    }
                }
            });

        } else {
            sendMsgRequestBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveChatContact() {

        contactsRef.child(Prevalent.currentOnlineUser.getUsername()).child(recipientUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            contactsRef.child(recipientUserID).child(Prevalent.currentOnlineUser.getUsername()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {

                                                sendMsgRequestBtn.setEnabled(true);
                                                current_state = "new";
                                                sendMsgRequestBtn.setText("Send Message Request");

                                                declineMsgRequestBtn.setVisibility(View.INVISIBLE);
                                                declineMsgRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }

                    }
                });

    }

    private void AcceptChatRequest() {

        contactsRef.child(Prevalent.currentOnlineUser.getUsername()).child(recipientUserID).child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            contactsRef.child(recipientUserID).child(Prevalent.currentOnlineUser.getUsername()).child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {

                                                chatRequestRef.child(Prevalent.currentOnlineUser.getUsername()).child(recipientUserID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            chatRequestRef.child(recipientUserID).child(Prevalent.currentOnlineUser.getUsername()).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            sendMsgRequestBtn.setEnabled(true);
                                                                            current_state = "contacts";
                                                                            sendMsgRequestBtn.setText("Remove Contact");

                                                                            declineMsgRequestBtn.setVisibility(View.INVISIBLE);
                                                                            declineMsgRequestBtn.setEnabled(false);

                                                                        }
                                                                    });

                                                        }
                                                    }
                                                });

                                            }

                                        }
                                    });


                        }

                    }
                });

    }

    private void CancelChatRequest() {

        chatRequestRef.child(Prevalent.currentOnlineUser.getUsername()).child(recipientUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            chatRequestRef.child(recipientUserID).child(Prevalent.currentOnlineUser.getUsername()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {

                                                sendMsgRequestBtn.setEnabled(true);
                                                current_state = "new";
                                                sendMsgRequestBtn.setText("Send Message Request");

                                                declineMsgRequestBtn.setVisibility(View.INVISIBLE);
                                                declineMsgRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }

                    }
                });
    }

    private void SendChatRequest() {

        chatRequestRef.child(Prevalent.currentOnlineUser.getUsername()).child(recipientUserID).child("request_type")
                .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    chatRequestRef.child(recipientUserID).child(Prevalent.currentOnlineUser.getUsername())
                            .child("request_type").setValue("received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {
                                        HashMap<String, String> chatNotification = new HashMap<>();
                                        chatNotification.put("from", Prevalent.currentOnlineUser.getUsername());
                                        chatNotification.put("type", "request");

                                        sendMsgRequestBtn.setEnabled(true);
                                        current_state = "request_sent";
                                        sendMsgRequestBtn.setText("Cancel Message Request");
                                    }
                                }
                            });
                }
            }
        });

    }
}


