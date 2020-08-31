package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wasteawayapplication.Model.Products;
import com.example.wasteawayapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ExchangeDetailsActivity extends AppCompatActivity {

    private ImageView backBtn, productImage;
    private TextView productName, productLocation, productDescription, username, userPhone, userComments;
    private String productID = "", state = "Normal";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_details);

        backBtn = (ImageView) findViewById(R.id.iv_exchange_back);
        productImage = (ImageView) findViewById(R.id.iv_exchange_image);
        productName = (TextView) findViewById(R.id.tv_exchange_name);
        productLocation = (TextView) findViewById(R.id.tv_exchange_location);
        productDescription = (TextView) findViewById(R.id.tv_exchange_description);
        username = (TextView) findViewById(R.id.tv_exchange_username);
        userPhone = (TextView) findViewById(R.id.tv_exchange_user_phone);
        userComments = (TextView) findViewById(R.id.tv_exchange_user_comments);
        productID = getIntent().getStringExtra("pid");

        getProductDetails(productID);

        FloatingActionButton fab = findViewById(R.id.fab_exchange);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExchangeDetailsActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getProductDetails(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Exchange Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getName());
                    productLocation.setText(products.getLocation());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                    username.setText(products.getUsername());
                    userPhone.setText(products.getPhone());
                    userComments.setText(products.getComments());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}