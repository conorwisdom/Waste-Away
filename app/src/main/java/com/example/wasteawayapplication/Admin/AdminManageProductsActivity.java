package com.example.wasteawayapplication.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wasteawayapplication.Model.Products;
import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.example.wasteawayapplication.ViewHolder.AdminProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminManageProductsActivity extends AppCompatActivity {

    private ImageView backBtn;
    private DatabaseReference adminProductsRef;
    private RecyclerView recyclerView;
    private String productID = "";
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_products);

        adminProductsRef = FirebaseDatabase.getInstance().getReference().child("Rental Products");

        backBtn = (ImageView) findViewById(R.id.iv_admin_manage_back);
        productID = getIntent().getStringExtra("pid");
        recyclerView = (RecyclerView) findViewById(R.id.admin_manage_products_list);
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
                .setQuery(adminProductsRef.orderByChild("username")
                        .equalTo(Prevalent.currentOnlineUser.getUsername()), Products.class).build();

        FirebaseRecyclerAdapter<Products, AdminProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, AdminProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminProductViewHolder holder, int position, @NonNull final Products model) {

                        holder.textProductName.setText(model.getName());
                        holder.textProductCategory.setText(model.getCategory());
                        holder.textProductDescription.setText(model.getDescription());
                        holder.textProductLocation.setText(model.getLocation());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String productID = model.getPid();

                                CharSequence options[] = new CharSequence[] {

                                        "Edit",
                                        "Delete"

                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminManageProductsActivity.this);
                                builder.setTitle("Product Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            Intent intent = new Intent(AdminManageProductsActivity.this, AdminEditProductsActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                        if (i == 1) {
                                            DeleteProduct(productID);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_rental_view_layout, parent, false);
                        AdminProductViewHolder holder = new AdminProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void DeleteProduct(String productID) {
        adminProductsRef.child(productID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminManageProductsActivity.this, "Product Removed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}