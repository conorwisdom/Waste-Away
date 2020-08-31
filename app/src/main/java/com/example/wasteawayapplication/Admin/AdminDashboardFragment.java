package com.example.wasteawayapplication.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.wasteawayapplication.User.LoginActivity;
import com.example.wasteawayapplication.R;
import com.example.wasteawayapplication.User.MessageActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardFragment extends Fragment {

    public AdminDashboardFragment() {
//        required empty public constructor
    }

    private Button adminInventory, adminCustomers, adminOrders, adminApprove, adminManage, adminLogOut;
    private ImageView adminMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        adminMessage = (ImageView) view.findViewById(R.id.iv_admindash_message);
        adminInventory = (Button) view.findViewById(R.id.btn_admindash_inventory);
        adminCustomers = (Button) view.findViewById(R.id.btn_admindash_customers);
        adminOrders = (Button) view.findViewById(R.id.btn_admindash_orders);
        adminApprove = (Button) view.findViewById(R.id.btn_admindash_approve);
        adminManage = (Button) view.findViewById(R.id.btn_admindash_manage);
        adminLogOut = (Button) view.findViewById(R.id.btn_admindash_logout);

        adminMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getActivity(), MessageActivity.class);
                startActivity(intent);
            }
        });
        adminInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdminInventoryActivity.class);
                startActivity(intent);
            }
        });
        adminCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdminCustomersActivity.class);
                startActivity(intent);
            }
        });
        adminOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdminOrdersActivity.class);
                startActivity(intent);
            }
        });
        adminManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdminManageProductsActivity.class);
                startActivity(intent);
            }
        });
        adminApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdminApproveProductsActivity.class);
                startActivity(intent);
            }
        });


//        logs admin out and clears activities
        adminLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth fAuth;
                fAuth = FirebaseAuth.getInstance();
                fAuth.signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finishAffinity();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_admindash);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdminAddProductsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
