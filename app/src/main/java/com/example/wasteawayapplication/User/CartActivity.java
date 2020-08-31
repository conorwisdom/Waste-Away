package com.example.wasteawayapplication.User;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wasteawayapplication.Model.Cart;
import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.example.wasteawayapplication.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button confirmCart;
    private TextView confirmMsg;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        confirmCart = (Button) findViewById(R.id.btn_confirm_cart);
        backBtn = (ImageView) findViewById(R.id.iv_cart_back);
        confirmMsg = (TextView) findViewById(R.id.tv_order_msg);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirmCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, OrdersActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderStatus();

//        displays cart items stored in database
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentOnlineUser.getUsername())
                        .child("Products"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.productName.setText(model.getName());
                holder.productLocation.setText(model.getLocation());
                holder.productQuantity.setText("Qty: " + model.getQuantity());

//                gives user option to edit or remove items from cart on click
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options [] = new CharSequence[] {
                                "Edit",
                                "Remove"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    Intent intent = new Intent(CartActivity.this, RentalDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if (i == 1) {
                                    cartListRef.child("User View").child(Prevalent.currentOnlineUser.getUsername())
                                            .child("Products").child(model.getPid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(CartActivity.this, "Item Removed", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

//            creates cart layout from xml
            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
//    user validations to ensure rentals are returned before ordering again
    private void CheckOrderStatus() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getUsername());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String dispatchedState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if(dispatchedState.equals("Pending")) {
                        recyclerView.setVisibility(View.GONE);
                        confirmMsg.setVisibility(View.VISIBLE);
                        confirmCart.setVisibility(View.GONE);
                    } else if(dispatchedState.equals("Confirmed")) {
                        confirmMsg.setText(userName + ", \n Your order has been processed. It will be dispatched shortly." + "\n Thanks," + "\n The Waste Away Team");
                        recyclerView.setVisibility(View.GONE);

                        confirmMsg.setVisibility(View.VISIBLE);
                        confirmCart.setVisibility(View.GONE);
                    } else if(dispatchedState.equals("Dispatched")) {
                        confirmMsg.setText(userName + ", \n Your order has been dispatch. It will arrive in due course." + "\n Thanks," + "\n The Waste Away Team");
                        recyclerView.setVisibility(View.GONE);

                        confirmMsg.setVisibility(View.VISIBLE);
                        confirmCart.setVisibility(View.GONE);
                    } else if(dispatchedState.equals("Received")) {
                        confirmMsg.setText(userName + ", \n You currently have a rental item. Return the item if you would like to rent another." + "\n Thanks," + "\n The Waste Away Team");
                        recyclerView.setVisibility(View.GONE);

                        confirmMsg.setVisibility(View.VISIBLE);
                        confirmCart.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}