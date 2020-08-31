package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText inputName, inputUsername, inputEmail,inputAddress, inputPostcode, inputCity, inputPassword, inputConfirmPwd;
    private Button registerBtn;
    private TextView loginLink;
    com.rey.material.widget.CheckBox checkboxAge;
    private ProgressDialog loadingBar;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.TYPE_STATUS_BAR);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();

        inputName = (EditText) findViewById(R.id.et_reg_name);
        inputUsername = (EditText) findViewById(R.id.et_reg_username);
        inputEmail = (EditText) findViewById(R.id.et_reg_email);
        inputAddress = (EditText) findViewById(R.id.et_reg_address);
        inputPostcode = (EditText) findViewById(R.id.et_reg_postcode);
        inputCity = (EditText) findViewById(R.id.et_reg_city);
        inputPassword = (EditText) findViewById(R.id.et_reg_password);
        inputConfirmPwd = (EditText) findViewById(R.id.et_reg_confirmpass);
        registerBtn = (Button) findViewById(R.id.btn_register);
        loginLink = (TextView) findViewById(R.id.tv_reg_login);
        checkboxAge = findViewById(R.id.cbox_age);
        loadingBar = new ProgressDialog(this);

        //         redirects user to Login page when hyperlink is clicked
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }


    private void CreateAccount() {

        final String name = inputName.getText().toString();
        final String username = inputUsername.getText().toString();
        final String email = inputEmail.getText().toString();
        final String address = inputAddress.getText().toString();
        final String postcode = inputPostcode.getText().toString();
        final String city = inputCity.getText().toString();
        final String password = inputPassword.getText().toString();
        final String confirmpwd = inputConfirmPwd.getText().toString();

//        ensures user enters required details
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Full name is required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email address is required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmpwd)) {
            Toast.makeText(this, "Password confirmation is required", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmpwd)) {
            Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show();
        } else if (!checkboxAge.isChecked()){
            Toast.makeText(this, "You must be 18 years or older to register", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Verifying Account Credentials...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            fAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            final DatabaseReference rootref;
                            rootref = FirebaseDatabase.getInstance().getReference();

                            rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (!(dataSnapshot.child("Users").child(username).exists())) {
                                        HashMap<String, Object> userdataMap = new HashMap<>();
                                        userdataMap.put("name", name);
                                        userdataMap.put("username", username);
                                        userdataMap.put("email", email);
                                        userdataMap.put("address", address);
                                        userdataMap.put("postcode", postcode);
                                        userdataMap.put("city", city);
                                        userdataMap.put("password", password);

                                        rootref.child("Users").child(username).updateChildren(userdataMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseInstanceId.getInstance().getInstanceId()
                                                                    .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                                                        @Override
                                                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                                                            String deviceToken = instanceIdResult.getToken();

                                                                            rootref.child(Prevalent.currentOnlineUser.getUsername())
                                                                                    .child("device_token").setValue(deviceToken);
                                                                        }
                                                                    });

                                                            Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                                            loadingBar.dismiss();

                                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                        } else {
                                                            loadingBar.dismiss();
                                                            Toast.makeText(RegisterActivity.this, "Network Error: Please Try Again", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Username Already Exists", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, RegisterActivity.class);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    });
        }
    }
}