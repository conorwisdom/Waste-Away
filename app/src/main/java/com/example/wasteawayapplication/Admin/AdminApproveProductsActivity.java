package com.example.wasteawayapplication.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wasteawayapplication.Model.Products;
import com.example.wasteawayapplication.R;
import com.example.wasteawayapplication.ViewHolder.RentalViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminApproveProductsActivity extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_products);

        backBtn = (ImageView) findViewById(R.id.iv_approve_back);

        recyclerView = (RecyclerView) findViewById(R.id.admin_approve_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Exchange Products");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductsRef.orderByChild("state")
                        .equalTo("Not Approved"), Products.class).build();

        FirebaseRecyclerAdapter<Products, RentalViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, RentalViewHolder>(options) {
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
                                final String productID = model.getPid();

                                CharSequence options[] = new CharSequence[]{

                                        "Yes",
                                        "No"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminApproveProductsActivity.this);
                                builder.setTitle("Would you like to approve this product?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(i == 0) {

                                            VerifyProduct(productID);
                                        }
                                        if(i == 1) {

                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RentalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_details_layout, parent, false);
                        RentalViewHolder holder = new RentalViewHolder(view);
                        return holder;

                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void VerifyProduct(String productID) {

        unverifiedProductsRef.child(productID).child("state")
                .setValue("Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminApproveProductsActivity.this, "Product Approved", Toast.LENGTH_SHORT).show();
            }
        });

    }
}