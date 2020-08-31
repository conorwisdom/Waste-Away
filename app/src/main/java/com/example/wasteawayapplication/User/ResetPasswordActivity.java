package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check = "";
    private TextView pageTitle, questionTitle;
    private EditText userUsername, securityQuestion1, securityQuestion2;
    private Button updateBtn;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");

        pageTitle = (TextView) findViewById(R.id.reset_password_title);
        questionTitle = (TextView) findViewById(R.id.security_questions_title);
        userUsername = (EditText) findViewById(R.id.et_find_username);
        securityQuestion1 = (EditText) findViewById(R.id.security_question1);
        securityQuestion2 = (EditText) findViewById(R.id.security_question2);
        updateBtn = (Button) findViewById(R.id.btn_security_update);
        backBtn = (ImageView) findViewById(R.id.iv_security_back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        userUsername.setVisibility(View.GONE);

        if (check.equals("settings")) {

            pageTitle.setText("Security Questions");
            questionTitle.setText("Please answer the following security questions");

            displayPreviousAnswers();

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setUserAnswers();
                }
            });

        } else if
        (check.equals("login")) {
            userUsername.setVisibility(View.VISIBLE);

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyUser();
                }
            });
        }
    }

    private void setUserAnswers() {

        String securityAnswer1 = securityQuestion1.getText().toString().toLowerCase();
        String securityAnswer2 = securityQuestion2.getText().toString().toLowerCase();

        if (securityQuestion1.equals("") && securityQuestion2.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Both questions must be answered", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(Prevalent.currentOnlineUser.getUsername());

            HashMap<String, Object> userdataMap = new HashMap<>();
            userdataMap.put("securityAnswer1", securityAnswer1);
            userdataMap.put("securityAnswer2", securityAnswer2);

            ref.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, "Security questions saved", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }
    }

    private void displayPreviousAnswers() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(Prevalent.currentOnlineUser.getUsername());

        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    String answer1 = dataSnapshot.child("securityAnswer1").getValue().toString();
                    String answer2 = dataSnapshot.child("securityAnswer2").getValue().toString();

                    securityQuestion1.setText(answer1);
                    securityQuestion2.setText(answer2);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verifyUser() {

        final String username = userUsername.getText().toString();
        final String securityAnswer1 = securityQuestion1.getText().toString().toLowerCase();
        final String securityAnswer2 = securityQuestion2.getText().toString().toLowerCase();

        if(!username.equals("") && !securityAnswer1.equals("") && !securityAnswer2.equals("")) {

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(username);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()) {

                        if(dataSnapshot.hasChild("Security Questions")) {
                            String answer1 = dataSnapshot.child("Security Questions").child("securityAnswer1").getValue().toString();
                            String answer2 = dataSnapshot.child("Security Questions").child("securityAnswer2").getValue().toString();

                            if (!answer1.equals(securityAnswer1)) {
                                Toast.makeText(ResetPasswordActivity.this, "The answer to security question 1 is wrong", Toast.LENGTH_SHORT).show();
                            } else if (!answer2.equals(securityAnswer2)) {
                                Toast.makeText(ResetPasswordActivity.this, "The answer to security question 2 is wrong", Toast.LENGTH_SHORT).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New Password");

                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Enter new password");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(!newPassword.getText().toString().equals("")) {
                                            ref.child("password").setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful()) {

                                                                Toast.makeText(ResetPasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();

                                                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.cancel();
                                    }
                                });
                                builder.show();
                            }
                        }

                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "The username " + username + " does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}