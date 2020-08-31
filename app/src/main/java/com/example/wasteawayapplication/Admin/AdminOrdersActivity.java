package com.example.wasteawayapplication.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteawayapplication.Admin.AdminOrderProductsActivity;
import com.example.wasteawayapplication.Model.AdminOrders;
import com.example.wasteawayapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminOrdersActivity extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        backBtn = (ImageView) findViewById(R.id.iv_admin_orders_back);
        ordersList = findViewById(R.id.admin_orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));

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

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef.orderByChild("state")
                                .equalTo("Pending"), AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {
                        holder.userName.setText("Name: " + model.getName());
                        holder.userUsername.setText("Username: " + model.getUsername());
                        holder.userEmail.setText("Email: " + model.getEmail());
                        holder.userPhone.setText("Phone: " + model.getPhone());
                        holder.userLocation.setText("Location: " + model.getAddress() + ", " + model.getCity() + ", " + model.getPostcode());
                        holder.userDateTime.setText("Ordered: " + model.getDate() + "  " + model.getTime());

                        holder.showOrders.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uID = getRef(position).getKey();

                                Intent intent = new Intent(AdminOrdersActivity.this, AdminOrderProductsActivity.class);
                                intent.putExtra("uid", uID);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String productID = model.getUsername();
                                CharSequence options[] = new CharSequence[]{
                                        "Yes",
                                        "No"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminOrdersActivity.this);
                                builder.setTitle("Has the product been dispatched?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(i == 0) {
                                            DispatchProduct(productID);
                                        } else {
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_orders_layout, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView userName, userUsername, userEmail, userPhone, userLocation, userDateTime;
        public Button showOrders;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userUsername = itemView.findViewById(R.id.order_user_username);
            userEmail = itemView.findViewById(R.id.order_user_email);
            userPhone = itemView.findViewById(R.id.order_user_phone);
            userLocation = itemView.findViewById(R.id.order_user_location);
            userDateTime = itemView.findViewById(R.id.order_datetime);
            showOrders = itemView.findViewById(R.id.btn_show_orders);
        }
    }

    private void DispatchProduct(String productID) {
        ordersRef.child(productID).child("state")
                .setValue("Dispatched").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminOrdersActivity.this, "Product Dispatched", Toast.LENGTH_SHORT).show();
            }
        });
    }
}