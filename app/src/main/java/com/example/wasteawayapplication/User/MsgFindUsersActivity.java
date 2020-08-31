package com.example.wasteawayapplication.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteawayapplication.Model.Users;
import com.example.wasteawayapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MsgFindUsersActivity extends AppCompatActivity {

    private ImageView backBtn;

    private RecyclerView findUsersRecyclerView;
    private DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_find_users);

        backBtn = (ImageView) findViewById(R.id.iv_findusers_back);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        findUsersRecyclerView = (RecyclerView) findViewById(R.id.find_users_list);
        findUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(usersRef, Users.class)
                        .build();

        final FirebaseRecyclerAdapter<Users, FindUsersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Users, FindUsersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindUsersViewHolder holder, final int position, @NonNull Users model)
                    {
                        holder.userUsername.setText(model.getUsername());
                        holder.userName.setText(model.getName());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                String username = getRef(position).getKey();

                                Intent intent = new Intent(MsgFindUsersActivity.this, ProfileActivity.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindUsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        FindUsersViewHolder viewHolder = new FindUsersViewHolder(view);
                        return viewHolder;
                    }
                };

        findUsersRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindUsersViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userUsername;

        public FindUsersViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userUsername = itemView.findViewById(R.id.user_profile_username);
        }
    }
}