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
import android.widget.TextView;

import com.example.wasteawayapplication.Model.AdminOrders;
import com.example.wasteawayapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminCustomersActivity extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView customersList;
    private DatabaseReference customersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_customers);

        customersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        backBtn = (ImageView) findViewById(R.id.iv_admin_customers_back);
        customersList = findViewById(R.id.admin_customers_list);
        customersList.setLayoutManager(new LinearLayoutManager(this));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(customersRef.orderByChild("state")
                        .equalTo("Dispatched"), AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders, AdminCustomersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminCustomersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminCustomersViewHolder holder, final int position, @NonNull AdminOrders model) {
                        holder.customerName.setText("Name: " + model.getName());
                        holder.customerUsername.setText("Username: " + model.getUsername());
                        holder.customerEmail.setText("Email Address: " + model.getEmail());
                        holder.customerPhone.setText("Contact Number: " + model.getPhone());
                        holder.customerLocation.setText("Location: " + model.getAddress() + ", " + model.getCity() + ", " + model.getPostcode());
                        holder.orderDateTime.setText("Ordered: " + model.getDate() + ", " + model.getTime());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[] {
                                        "Yes",
                                        "No"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminCustomersActivity.this);
                                builder.setTitle("Has the customer returned the item?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            String uID = getRef(position).getKey();
                                            RemoveOrder(uID);
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
                    public AdminCustomersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_customers_layout, parent, false);
                        return new AdminCustomersViewHolder(view);
                    }
                };
        customersList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminCustomersViewHolder extends RecyclerView.ViewHolder {

        public TextView customerName, customerUsername, customerEmail, customerPhone, customerLocation, orderDateTime;

        public AdminCustomersViewHolder(@NonNull View view) {
            super(view);

            customerName = view.findViewById(R.id.order_customer_name);
            customerUsername = view.findViewById(R.id.order_customer_username);
            customerEmail = view.findViewById(R.id.order_customer_email);
            customerPhone = view.findViewById(R.id.order_customer_phone);
            customerLocation = view.findViewById(R.id.order_customer_location);
            orderDateTime = view.findViewById(R.id.order_date_time);
        }
    }

    private void RemoveOrder(String uID) {
        customersRef.child(uID).removeValue();
    }
}