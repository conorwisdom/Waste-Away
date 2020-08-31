package com.example.wasteawayapplication.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteawayapplication.Interface.ItemClickListener;
import com.example.wasteawayapplication.R;

public class AdminProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textProductName, textProductCategory, textProductDescription, textProductLocation;
    public ImageView imageView;
    public ItemClickListener listener;

    public AdminProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.admin_product_layout_image);
        textProductName = (TextView) itemView.findViewById(R.id.admin_product_layout_name);
        textProductCategory = (TextView) itemView.findViewById(R.id.admin_product_layout_category);
        textProductDescription = (TextView) itemView.findViewById(R.id.admin_product_layout_description);
        textProductLocation = (TextView) itemView.findViewById(R.id.admin_product_layout_location);
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {

        listener.onClick(view, getAdapterPosition(), false);
    }
}