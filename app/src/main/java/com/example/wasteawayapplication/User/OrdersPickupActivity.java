package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class OrdersPickupActivity extends AppCompatActivity {

    private RadioButton checkboxDelivery, checkboxPickup;
    private Button confirmPickup;
    private Spinner dropdownBox;
    private TextView textLocation;
    private EditText pickupName, pickupUsername, pickupEmail, pickupPhone;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_pickup);

        checkboxDelivery = (RadioButton) findViewById(R.id.cbox_pickup_delivery);
        checkboxPickup = (RadioButton) findViewById(R.id.cbox_pickup);
        confirmPickup = (Button) findViewById(R.id.btn_confirm_pickup);
        dropdownBox = (Spinner) findViewById(R.id.dropdown_pickup);
        textLocation = (TextView) findViewById(R.id.tv_pickup_location);
        pickupName = (EditText) findViewById(R.id.et_pickup_name);
        pickupUsername = (EditText) findViewById(R.id.et_pickup_username);
        pickupEmail = (EditText) findViewById(R.id.et_pickup_email);
        pickupPhone = (EditText) findViewById(R.id.et_pickup_phone);
        backBtn = (ImageView) findViewById(R.id.iv_pickup_back);

        List<String> dropdown = new ArrayList<>();
        dropdown.add("Choose Pickup Location");
        dropdown.add("BT17 0EF");
        dropdown.add("Pickup Location 2");
        dropdown.add("Pickup Location 3");
        dropdown.add("Pickup Location 4");

        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dropdown);
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownBox.setAdapter(dropdownAdapter);

        dropdownBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String location = dropdownBox.getSelectedItem().toString();
                if (!location.equals("Choose Pickup Location")) {
                    textLocation.setText("Your Pickup Location: " + location);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        checkboxPickup.setChecked(true);
        checkboxPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrdersPickupActivity.this, OrdersPickupActivity.class);
                startActivity(intent);
            }
        });

        checkboxDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrdersPickupActivity.this, OrdersActivity.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirmPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });
    }

    private void Check() {
        View selectedView = dropdownBox.getSelectedView();
        if (selectedView == null) {
            Toast.makeText(this, "Pickup Location Required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pickupName.getText().toString())) {
            Toast.makeText(OrdersPickupActivity.this, "Name Required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pickupUsername.getText().toString())) {
            Toast.makeText(OrdersPickupActivity.this, "Username required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pickupEmail.getText().toString())) {
            Toast.makeText(OrdersPickupActivity.this, "Email Required", Toast.LENGTH_SHORT).show();
        } else {
            confirmOrder();
        }
    }

    private void confirmOrder() {

        final String saveCurrentDate, saveCurrentTime;

        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendarDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendarDate.getTime());

//                stores order in the database
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUser.getUsername());

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("pickup location", dropdownBox.getSelectedItem().toString());
        ordersMap.put("pickup name", pickupName.getText().toString());
        ordersMap.put("pickup username", pickupUsername.getText().toString());
        ordersMap.put("pickup email", pickupEmail.getText().toString());
        ordersMap.put("pickup phone", pickupPhone.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "Pending");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View").child(Prevalent.currentOnlineUser.getUsername())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(OrdersPickupActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(OrdersPickupActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });
                }
            }
        });
    }
}
