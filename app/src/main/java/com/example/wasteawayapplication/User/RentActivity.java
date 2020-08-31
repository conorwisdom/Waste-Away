package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wasteawayapplication.Model.Products;
import com.example.wasteawayapplication.R;
import com.example.wasteawayapplication.ViewHolder.RentalViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class RentActivity extends AppCompatActivity {

    private ImageView backBtn;
    private DatabaseReference rentalProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);

        rentalProductsRef = FirebaseDatabase.getInstance().getReference().child("Rental Products");

        backBtn = (ImageView) findViewById(R.id.iv_rent_back);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(rentalProductsRef, Products.class).build();

//        binds data from the firebase database to the app
        FirebaseRecyclerAdapter<Products, RentalViewHolder> adapter = new FirebaseRecyclerAdapter<Products, RentalViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RentalViewHolder holder, int position, @NonNull final Products model) {
                holder.textProductName.setText(model.getName());
                holder.textProductCategory.setText(model.getCategory());
                holder.textProductDescription.setText(model.getDescription());
                holder.textProductLocation.setText(model.getLocation());
                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RentActivity.this, RentalDetailsActivity.class);
                        intent.putExtra("pid", model.getPid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public RentalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                inflates layout from the product items xml
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_details_layout, parent, false);
                RentalViewHolder holder = new RentalViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}