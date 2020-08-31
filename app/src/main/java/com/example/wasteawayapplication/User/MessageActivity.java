package com.example.wasteawayapplication.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.example.wasteawayapplication.R;
import com.google.android.material.tabs.TabLayout;

public class MessageActivity extends AppCompatActivity {

    private ViewPager msgViewPager;
    private TabLayout msgTabLayout;
    private RecyclerView userMsgList;
    private MsgTabsAccessorAdapter msgTabsAccessorAdapter;

    private ImageView backBtn;
    private ImageButton findUsersBtn;
    private EditText userMsgInput;

    private String msgRecipientID, msgRecipientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

//        msgRecipientID = getIntent().getExtras().get("Users").toString();
//        msgRecipientName = getIntent().getExtras().get("Users").toString();

        backBtn = (ImageView) findViewById(R.id.iv_messages_back);
        findUsersBtn = (ImageButton) findViewById(R.id.btn_find_users);

        msgViewPager = (ViewPager) findViewById(R.id.messages_tabs_pager);
        msgTabsAccessorAdapter = new MsgTabsAccessorAdapter(getSupportFragmentManager());
        msgViewPager.setAdapter(msgTabsAccessorAdapter);
        msgTabLayout = (TabLayout) findViewById(R.id.messages_tab_layout);
        msgTabLayout.setupWithViewPager(msgViewPager);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, MsgFindUsersActivity.class);
                startActivity(intent);
            }
        });

    }
}