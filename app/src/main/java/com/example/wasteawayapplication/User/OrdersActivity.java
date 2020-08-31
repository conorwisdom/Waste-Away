package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class OrdersActivity extends AppCompatActivity {

    private Button confirmOrderBtn;
    private EditText orderName, orderUsername, orderAddress, orderCity, orderPostcode, orderEmail, orderPhone;
    private ImageView backBtn;
    private RadioButton checkboxDelivery, checkboxPickup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        confirmOrderBtn = (Button) findViewById(R.id.btn_confirm_order);
        orderName = (EditText) findViewById(R.id.et_order_name);
        orderUsername = (EditText) findViewById(R.id.et_order_username);
        orderAddress = (EditText) findViewById(R.id.et_order_address);
        orderCity = (EditText) findViewById(R.id.et_order_city);
        orderPostcode = (EditText) findViewById(R.id.et_order_postcode);
        orderEmail = (EditText) findViewById(R.id.et_order_email);
        orderPhone = (EditText) findViewById(R.id.et_order_phone);
        backBtn = (ImageView) findViewById(R.id.iv_orders_back);
        checkboxDelivery = (RadioButton) findViewById(R.id.cbox_orders_delivery);
        checkboxPickup = (RadioButton) findViewById(R.id.cbox_orders_pickup);

        checkboxDelivery.setChecked(true);
        checkboxDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrdersActivity.this, OrdersActivity.class);
                startActivity(intent);
            }
        });

        checkboxPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrdersActivity.this, OrdersPickupActivity.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }

            private void Check() {
//                ensures user completes required fields
                if (TextUtils.isEmpty(orderName.getText().toString())) {
                    Toast.makeText(OrdersActivity.this, "Name Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(orderUsername.getText().toString())) {
                    Toast.makeText(OrdersActivity.this, "Username required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(orderAddress.getText().toString())) {
                    Toast.makeText(OrdersActivity.this, "Delivery Address Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(orderCity.getText().toString())) {
                    Toast.makeText(OrdersActivity.this, "Delivery City Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(orderPostcode.getText().toString())) {
                    Toast.makeText(OrdersActivity.this, "Delivery Postcode Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(orderEmail.getText().toString())) {
                    Toast.makeText(OrdersActivity.this, "Email Required", Toast.LENGTH_SHORT).show();
                } else {
                    confirmOrder();
                }
            }

            private void confirmOrder() {

//                records date and time order is made
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
                ordersMap.put("name", orderName.getText().toString());
                ordersMap.put("username", orderUsername.getText().toString());
                ordersMap.put("address", orderAddress.getText().toString());
                ordersMap.put("city", orderCity.getText().toString());
                ordersMap.put("postcode", orderPostcode.getText().toString());
                ordersMap.put("email", orderEmail.getText().toString());
                ordersMap.put("phone", orderPhone.getText().toString());
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
                                        Toast.makeText(OrdersActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(OrdersActivity.this, HomeActivity.class);
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
        });
    }
}