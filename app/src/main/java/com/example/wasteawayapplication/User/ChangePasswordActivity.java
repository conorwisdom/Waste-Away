package com.example.wasteawayapplication.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView backBtn;
    private EditText newPassword, confirmPassword;
    private Button updateBtn;
    private ProgressDialog loadingBar;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("password");

        backBtn = (ImageView) findViewById(R.id.iv_change_back);
        updateBtn = (Button) findViewById(R.id.btn_update_password);
        newPassword = (EditText) findViewById(R.id.new_password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        loadingBar = new ProgressDialog(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateUserPassword();
            }
        });
    }

    private void updateUserPassword() {

        final String updatedPassword = newPassword.getText().toString();
        final String confirmedPassword = confirmPassword.getText().toString();

        if (TextUtils.isEmpty(updatedPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Password Required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmedPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Password Confirmation Required", Toast.LENGTH_SHORT).show();
        } else if (updatedPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else if (!updatedPassword.equals(confirmedPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Passwords Must Match", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Changing Password...");
            loadingBar.setMessage("Changing User Password");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("password", newPassword.getText().toString());
            ref.child(Prevalent.currentOnlineUser.getUsername()).updateChildren(userMap);

            Intent intent = new Intent(ChangePasswordActivity.this, HomeActivity.class);
            Toast.makeText(this, "Password Updated", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}