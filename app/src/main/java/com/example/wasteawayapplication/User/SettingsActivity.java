package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private EditText nameEditText, addressEditText, cityEditText, postcodeEditText, phoneEditText;
    private ImageView backBtn, confirmBtn;
    private Button changePasswordBtn, securityQuestionBtn;
    private ProgressDialog loadingBar;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storeProfilePictureRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nameEditText = (EditText) findViewById(R.id.et_settings_name);
        addressEditText = (EditText) findViewById(R.id.et_settings_address);
        cityEditText = (EditText) findViewById(R.id.et_settings_city);
        postcodeEditText = (EditText) findViewById(R.id.et_settings_postcode);
        phoneEditText = (EditText) findViewById(R.id.et_settings_phone);
        backBtn = (ImageView) findViewById(R.id.iv_settings_back);
        confirmBtn = (ImageView) findViewById(R.id.iv_settings_confirm);
        changePasswordBtn = (Button) findViewById(R.id.btn_change_password);
        securityQuestionBtn = (Button) findViewById(R.id.btn_settings_security);
        loadingBar = new ProgressDialog(this);

        userInfoDisplay(nameEditText, addressEditText, cityEditText, postcodeEditText, phoneEditText);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "settings");
                startActivity(intent);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    updateProfile();
                }
        });
    }

    private void updateProfile() {

        loadingBar.setTitle("Profile Updating...");
        loadingBar.setMessage("Profile is updating");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", nameEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        userMap.put("city", cityEditText.getText().toString());
        userMap.put("postcode", postcodeEditText.getText().toString());
        userMap.put("phone", phoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getUsername()).updateChildren(userMap);

        loadingBar.dismiss();

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void userInfoDisplay(final EditText nameEditText,
                                 final EditText addressEditText, final EditText cityEditText, final EditText postcodeEditText, final EditText phoneEditText) {

        DatabaseReference UsersRef =
                FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getUsername());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("username").exists()) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        String city = dataSnapshot.child("city").getValue().toString();
                        String postcode = dataSnapshot.child("postcode").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();

                        nameEditText.setText(name);
                        addressEditText.setText(address);
                        cityEditText.setText(city);
                        postcodeEditText.setText(postcode);
                        phoneEditText.setText(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}