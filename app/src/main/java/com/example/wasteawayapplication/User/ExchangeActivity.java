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
import com.example.wasteawayapplication.ViewHolder.ExchangeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ExchangeActivity extends AppCompatActivity {

    private ImageView backBtn;
    private DatabaseReference exchangeProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        exchangeProductsRef = FirebaseDatabase.getInstance().getReference().child("Exchange Products");

        backBtn = (ImageView) findViewById(R.id.iv_exchange_back);
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

    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(exchangeProductsRef.orderByChild("state").equalTo("Approved"), Products.class).build();

//        binds data from the firebase database to the app
        FirebaseRecyclerAdapter<Products, ExchangeViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ExchangeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ExchangeViewHolder holder, int position, @NonNull final Products model) {
                holder.textProductName.setText(model.getName());
                holder.textProductCategory.setText(model.getCategory());
                holder.textProductLocation.setText(model.getLocation());
                holder.textProductDescription.setText(model.getDescription());

                holder.textUserUsername.setVisibility(View.GONE);
                holder.textUserPhone.setVisibility(View.GONE);
                holder.textUserComments.setVisibility(View.GONE);
                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ExchangeActivity.this, ExchangeDetailsActivity.class);
                        intent.putExtra("pid", model.getPid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ExchangeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                inflates layout from the product items xml
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exchange_details_layout, parent, false);
                ExchangeViewHolder holder = new ExchangeViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}