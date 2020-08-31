package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.wasteawayapplication.Model.Products;
import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RentalDetailsActivity extends AppCompatActivity {

    private Button addToCartBtn;
    private ElegantNumberButton quantityBtn;
    private ImageView backBtn, productImage;
    private TextView productName, productDescription, productLocation;
    private String productID = "", state = "Normal";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_details);

        addToCartBtn = (Button) findViewById(R.id.btn_productinfo_addtocart);
        quantityBtn = (ElegantNumberButton) findViewById(R.id.btn_productinfo_quantity);
        backBtn = (ImageView) findViewById(R.id.iv_productinfo_back);
        productImage = (ImageView) findViewById(R.id.iv_productinfo_image);
        productName = (TextView) findViewById(R.id.tv_productinfo_name);
        productDescription = (TextView) findViewById(R.id.tv_productinfo_description);
        productLocation = (TextView) findViewById(R.id.tv_productinfo_location);
        productID = getIntent().getStringExtra("pid");

        getProductDetails(productID);

        FloatingActionButton fab = findViewById(R.id.fab_productinfo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RentalDetailsActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        restricts user from making more orders while rental not returned
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (state.equals("Order Placed") || state.equals("Order Returned")
                    || state.equals("Order Dispatched") || state.equals("Order Received")) {
                    Toast.makeText(RentalDetailsActivity.this, "You cannot rent more products until current rentals are returned", Toast.LENGTH_SHORT).show();
                } else {
                    addingToCartList();
                }
            }

            private void addingToCartList() {
                String saveCurrentTime, saveCurrentDate;

                Calendar calendarDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                saveCurrentDate = currentDate.format(calendarDate.getTime());

                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                saveCurrentTime = currentTime.format(calendarDate.getTime());

                final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

//                adds users cart to the database
                final HashMap<String, Object> cartMap = new HashMap<>();
                cartMap.put("pid", productID);
                cartMap.put("name", productName.getText().toString());
                cartMap.put("location", productLocation.getText().toString());
                cartMap.put("quantity", quantityBtn.getNumber());
                cartMap.put("date", saveCurrentDate);
                cartMap.put("time", saveCurrentTime);

//                assigns cart to the specific user
                cartListRef.child("User View").child(Prevalent.currentOnlineUser.getUsername())
                        .child("Products").child(productID).updateChildren(cartMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getUsername())
                                            .child("Products").child(productID).updateChildren(cartMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RentalDetailsActivity.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();
    }

    private void getProductDetails(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Rental Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getName());
                    productDescription.setText(products.getDescription());
                    productLocation.setText(products.getLocation());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CheckOrderState() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getUsername());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String dispatchedState = dataSnapshot.child("state").getValue().toString();

                    if (dispatchedState.equals("Pending")) {
                        state = "Order Placed";
                    } else if (dispatchedState.equals("Confirmed")) {
                        state = "Order Confirmed";
                    } else if (dispatchedState.equals("Dispatched")) {
                        state = "Order Dispatched";
                    } else if (dispatchedState.equals("Received")) {
                        state = "Order Received";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}