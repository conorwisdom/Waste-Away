package com.example.wasteawayapplication.User;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteawayapplication.Model.Products;
import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.example.wasteawayapplication.ViewHolder.UserProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ManageProductsActivity extends AppCompatActivity {

    private ImageView backBtn;
    private DatabaseReference userProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        backBtn = (ImageView) findViewById(R.id.iv_products_back);

        userProductsRef = FirebaseDatabase.getInstance().getReference().child("Exchange Products");

        recyclerView = (RecyclerView) findViewById(R.id.user_products_list);
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
                .setQuery(userProductsRef.orderByChild("username")
                        .equalTo(Prevalent.currentOnlineUser.getUsername()), Products.class).build();

        FirebaseRecyclerAdapter<Products, UserProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, UserProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserProductViewHolder holder, int position, @NonNull final Products model) {

                        holder.textProductName.setText(model.getName());
                        holder.textProductCategory.setText(model.getCategory());
                        holder.textProductDescription.setText(model.getDescription());
                        holder.textProductLocation.setText(model.getLocation());
                        holder.textProductState.setText("Status: " + model.getState());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String productID = model.getPid();

                                CharSequence options[] = new CharSequence[]{

                                        "Yes",
                                        "No"
                                };

                                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(ManageProductsActivity.this);
                                builder.setTitle("Would you like to remove this product?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {

                                            DeleteProduct(productID);
                                        }
                                        if (i == 1) {

                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public UserProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_exchange_view_layout, parent, false);
                        UserProductViewHolder holder = new UserProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void DeleteProduct(String productID) {
        userProductsRef.child(productID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(ManageProductsActivity.this, "Product Removed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}