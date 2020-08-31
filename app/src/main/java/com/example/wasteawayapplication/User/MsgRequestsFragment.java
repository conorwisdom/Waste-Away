package com.example.wasteawayapplication.User;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wasteawayapplication.Model.Users;
import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MsgRequestsFragment extends Fragment {

    private View requestsView;
    private RecyclerView requestsList;
    private DatabaseReference requestsRef, usersRef, contactsRef;
    private Button acceptBtn, rejectBtn;

    public MsgRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requestsView = inflater.inflate(R.layout.fragment_msg_requests, container, false);

        requestsList = (RecyclerView) requestsView.findViewById(R.id.requests_list);
        requestsList.setLayoutManager(new LinearLayoutManager(getContext()));

        requestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        acceptBtn = (Button) requestsView.findViewById(R.id.btn_request_accept);
        rejectBtn = (Button) requestsView.findViewById(R.id.btn_request_reject);

        return requestsView;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(requestsRef.child(Prevalent.currentOnlineUser.getUsername()), Users.class).build();

        FirebaseRecyclerAdapter<Users, requestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Users, requestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final requestsViewHolder holder, int position, @NonNull Users model) {

                        holder.itemView.findViewById(R.id.btn_request_accept).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.btn_request_reject).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(position).getKey();
                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String type = dataSnapshot.getValue().toString();

                                    if (type.equals("received")) {

                                        usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {

                                                    final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserUsername = dataSnapshot.child("username").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userUsername.setText(requestUserUsername);
                                                }

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        CharSequence options[] = new CharSequence[] {

                                                                "Accept",
                                                                "Cancel"
                                                        };

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle("Chat Request");

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                if (i == 0) {

                                                                    contactsRef.child(Prevalent.currentOnlineUser.getUsername()).child(list_user_id)
                                                                            .child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()) {

                                                                                contactsRef.child(list_user_id).child(Prevalent.currentOnlineUser.getUsername())
                                                                                        .child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful()) {

                                                                                            requestsRef.child(Prevalent.currentOnlineUser.getUsername()).child(list_user_id)
                                                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                    if (task.isSuccessful()) {

                                                                                                        requestsRef.child(list_user_id).child(Prevalent.currentOnlineUser.getUsername())
                                                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                if (task.isSuccessful()) {
                                                                                                                    Toast.makeText(getContext(), "Contact Saved", Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                }
                                                                                            });

                                                                                        }
                                                                                    }
                                                                                });

                                                                            }

                                                                        }
                                                                    });

                                                                }

                                                                if (i == 1) {

                                                                    requestsRef.child(Prevalent.currentOnlineUser.getUsername()).child(list_user_id)
                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()) {

                                                                                requestsRef.child(list_user_id).child(Prevalent.currentOnlineUser.getUsername())
                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful()) {
                                                                                            Toast.makeText(getContext(), "Contact Removed", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
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

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else if (type.equals("sent")) {
                                        Button request_send_btn = holder.itemView.findViewById(R.id.btn_request_accept);
                                        request_send_btn.setText("Request Sent");

                                        holder.itemView.findViewById(R.id.btn_request_reject).setVisibility(View.INVISIBLE);

                                        usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {

                                                    final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserUsername = dataSnapshot.child("username").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userUsername.setText(requestUserUsername);
                                                }

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        CharSequence options[] = new CharSequence[] {

                                                                "Cancel Chat Request"
                                                        };

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle("Request Sent");

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                if (i == 0) {

                                                                    requestsRef.child(Prevalent.currentOnlineUser.getUsername()).child(list_user_id)
                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()) {

                                                                                requestsRef.child(list_user_id).child(Prevalent.currentOnlineUser.getUsername())
                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful()) {
                                                                                            Toast.makeText(getContext(), "Request Cancelled", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
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

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    @NonNull
                    @Override
                    public requestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        requestsViewHolder holder = new requestsViewHolder(view);
                        return holder;
                    }
                };

        requestsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class requestsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userUsername;
        Button acceptBtn, rejectBtn;

        public requestsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userUsername = itemView.findViewById(R.id.user_profile_username);
            acceptBtn = itemView.findViewById(R.id.btn_request_accept);
            rejectBtn = itemView.findViewById(R.id.btn_request_reject);

        }
    }
}