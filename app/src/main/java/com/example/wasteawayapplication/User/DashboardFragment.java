package com.example.wasteawayapplication.User;

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

import com.example.wasteawayapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class DashboardFragment extends Fragment {

    public DashboardFragment() {
//        required empty public constructor
    }

    private Button userRecycling, userManage, userExchange, userRent, userSettings, userLogout;
    private ImageView productSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        userRecycling = (Button) view.findViewById(R.id.btn_userdash_recycling);
        userManage = (Button) view.findViewById(R.id.btn_userdash_manage);
        userExchange = (Button) view.findViewById(R.id.btn_userdash_exchange);
        userRent = (Button) view.findViewById(R.id.btn_userdash_rent);
        productSearch = (ImageView) view.findViewById(R.id.iv_userdash_search);
        userSettings = (Button) view.findViewById(R.id.btn_userdash_settings);
        userLogout = (Button) view.findViewById(R.id.btn_userdash_logout);

        userRecycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecyclingActivity.class);
                startActivity(intent);
            }
        });
        userManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(getActivity(), ManageProductsActivity.class);
              startActivity(intent);
            }
        });
        userExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ExchangeActivity.class);
                startActivity(intent);
            }
        });
        userRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RentActivity.class);
                startActivity(intent);
            }
        });
        productSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchProductsActivity.class);
                startActivity(intent);
            }
        });
        userSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });
//        logs user out clears activities and automatic login
        userLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseAuth fAuth;
                fAuth = FirebaseAuth.getInstance();
                fAuth.signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finishAffinity();
                Paper.book().destroy();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_userdash);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddProductActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
