package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wasteawayapplication.Admin.AdminHomeActivity;
import com.example.wasteawayapplication.Model.Users;
import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText inputUsername, inputPassword;
    private Button loginBtn;
    private TextView createAccLink, forgotPwdLink, adminLoginLink, notAdminLoginLink;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    com.rey.material.widget.CheckBox checkboxRememberMe;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = (EditText) findViewById(R.id.et_reg_username);
        inputPassword = (EditText) findViewById(R.id.et_password);
        loginBtn = (Button) findViewById(R.id.btn_login);
        createAccLink = (TextView) findViewById(R.id.tv_create_acc);
        forgotPwdLink = (TextView) findViewById(R.id.tv_forgot_pwd);
        adminLoginLink = (TextView) findViewById(R.id.tv_admin_login);
        notAdminLoginLink = (TextView) findViewById(R.id.tv_not_admin_login);
        firebaseAuth = firebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        checkboxRememberMe = findViewById(R.id.cbox_rememberme);
        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        forgotPwdLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });

//        makes admin/not admin links invisible when other is pressed
        adminLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Admin Login");
                adminLoginLink.setVisibility(View.INVISIBLE);
                notAdminLoginLink.setVisibility(View.VISIBLE);
                checkboxRememberMe.setVisibility(View.INVISIBLE);
                parentDbName = "Admins";
            }
        });
        notAdminLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setText("Login");
                adminLoginLink.setVisibility(View.VISIBLE);
                notAdminLoginLink.setVisibility(View.INVISIBLE);
                checkboxRememberMe.setVisibility(View.VISIBLE);
                parentDbName = "Users";
            }
        });
        createAccLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void LoginUser() {
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

//        ensures user enters required credentials
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(LoginActivity.this, "Username is required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Logging In...");
            loadingBar.setMessage("Checking Account Credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(username, password);
        }
    }

    private void AllowAccessToAccount(final String username, final String password) {

        if (checkboxRememberMe.isChecked()) {
            Paper.book().write(Prevalent.UserUsernameKey, username);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(parentDbName).child(username).exists()) {
                    Users usersData = dataSnapshot.child(parentDbName).child(username).getValue(Users.class);

                    if (usersData.getUsername().equals(username)) {

                        if (usersData.getPassword().equals(password)) {

                            if (parentDbName.equals("Admins")) {
                                Toast.makeText(LoginActivity.this, "Admin Logged In", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            } else if (parentDbName.equals("Users")) {
                                Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "The username " + username + " does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
