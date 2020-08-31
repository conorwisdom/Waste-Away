package com.example.wasteawayapplication.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wasteawayapplication.Model.Users;
import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

public class MsgChatsFragment extends Fragment {

    private View chatsView;
    private RecyclerView chatsList;
    private DatabaseReference chatsRef, usersRef;


    public MsgChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         chatsView = inflater.inflate(R.layout.fragment_msg_chats, container, false);

         chatsList = (RecyclerView) chatsView.findViewById(R.id.chats_list);
         chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
         chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts")
                 .child(Prevalent.currentOnlineUser.getUsername());
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

         return chatsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(chatsRef, Users.class).build();

        FirebaseRecyclerAdapter<Users, chatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Users, chatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final chatsViewHolder holder, int position, @NonNull Users model) {

                        final String usersIDs = getRef(position).getKey();

                        usersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists()) {

                                        final String retrieveName = dataSnapshot.child("name").getValue().toString();
                                        final String retrieveUsername = dataSnapshot.child("username").getValue().toString();

                                        holder.userName.setText(retrieveName);
                                        holder.userUsername.setText(retrieveUsername);

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                Intent intent = new Intent (getContext(), ChatActivity.class);
                                                intent.putExtra("visit_user_id", usersIDs);
                                                intent.putExtra("visit_user_name", retrieveName);
                                                intent.putExtra("visit_user_username", retrieveUsername);
                                                startActivity(intent);
                                            }
                                        });

                                    }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public chatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new chatsViewHolder(view);
                    }
                };
        chatsList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class chatsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userUsername;

        public chatsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userUsername = itemView.findViewById(R.id.user_profile_username);
        }
    }
}