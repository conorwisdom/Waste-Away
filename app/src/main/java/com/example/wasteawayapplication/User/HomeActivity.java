package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;

import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.android.material.navigation.NavigationView;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Waste Away");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        opens nav drawer when menu button pressed
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //        inserts users name and username in nav header
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_navhead_name);
        TextView userUsernameTextView = headerView.findViewById(R.id.user_navhead_username);
        userNameTextView.setText(Prevalent.currentOnlineUser.getName());
        userUsernameTextView.setText(Prevalent.currentOnlineUser.getUsername());

//        opens on dashboard after login
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }

    //    shows new activity/fragment depending on which is selected in nav drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DashboardFragment()).commit();
                break;
            case R.id.nav_message:
                Intent intent = new Intent(HomeActivity.this, MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_search:
                Intent intent1 = new Intent(HomeActivity.this, SearchProductsActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_recycling:
                Intent intent2 = new Intent(HomeActivity.this, RecyclingActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_exchange:
                Intent intent3 = new Intent(HomeActivity.this, ExchangeActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_rent:
                Intent intent4 = new Intent(HomeActivity.this, RentActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_donate:
                Intent intent5 = new Intent(HomeActivity.this, DonateProductActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_add_product:
                Intent intent6 = new Intent(HomeActivity.this, AddProductActivity.class);
                startActivity(intent6);
            case R.id.nav_manage:
                Intent intent7 = new Intent(HomeActivity.this, ManageProductsActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_settings:
                Intent intent8 = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent8);
                break;
            case R.id.nav_logout:
                Intent intent9 = new Intent(HomeActivity.this, LoginActivity.class);
                intent9.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent9);
                finish();
                Paper.book().destroy();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //    closes nav drawer when phone back button is pressed
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
}
}
