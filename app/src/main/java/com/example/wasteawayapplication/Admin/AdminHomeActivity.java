package com.example.wasteawayapplication.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.wasteawayapplication.User.LoginActivity;
import com.example.wasteawayapplication.User.MessageActivity;
import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.android.material.navigation.NavigationView;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Waste Away");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        opens nav drawer with menu button pressed
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//         inserts user name and username in nav header
        View headerView = navigationView.getHeaderView(0);
        TextView adminUsernameTextView = headerView.findViewById(R.id.admin_navhead_username);
        adminUsernameTextView.setText(Prevalent.currentOnlineUser.getUsername());

//        opens on the dashboard when app is started
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AdminDashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }

//    shows new fragment depending on which is selected in nav drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AdminDashboardFragment()).commit();
            break;
            case R.id.nav_message:
                Intent intent = new Intent(AdminHomeActivity.this, MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_inventory:
                Intent intent1 = new Intent(AdminHomeActivity.this, AdminInventoryActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_customers:
                Intent intent2 = new Intent(AdminHomeActivity.this, AdminCustomersActivity.class);
                startActivity(intent2);
                break;
                case R.id.nav_orders:
                    Intent intent3 = new Intent(AdminHomeActivity.this, AdminOrdersActivity.class);
                    startActivity(intent3);
            case R.id.nav_manage:
                Intent intent4 = new Intent(AdminHomeActivity.this, AdminManageProductsActivity.class);
                startActivity(intent4);
            case R.id.nav_approve:
                Intent intent5 = new Intent(AdminHomeActivity.this, AdminApproveProductsActivity.class);
                startActivity(intent5);
            case R.id.nav_add_products:
                Intent intent6 = new Intent(AdminHomeActivity.this, AdminAddProductsActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_logout:
                Intent intent7 = new Intent(AdminHomeActivity.this, LoginActivity.class);
                intent7.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent7);
                finish();
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