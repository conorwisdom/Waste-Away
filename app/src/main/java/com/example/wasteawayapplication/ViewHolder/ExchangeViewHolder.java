package com.example.wasteawayapplication.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteawayapplication.Interface.ItemClickListener;
import com.example.wasteawayapplication.R;

import org.w3c.dom.Text;

public class ExchangeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textProductName, textProductCategory, textProductLocation, textProductDescription;
    public TextView textUserUsername, textUserPhone, textUserComments;
    public ImageView imageView;
    public ItemClickListener listener;

    public ExchangeViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.exchange_layout_image);
        textProductName = (TextView) itemView.findViewById(R.id.exchange_layout_name);
        textProductCategory = (TextView) itemView.findViewById(R.id.exchange_layout_category);
        textProductLocation = (TextView) itemView.findViewById(R.id.exchange_layout_location);
        textProductDescription = (TextView) itemView.findViewById(R.id.exchange_layout_description);
        textUserUsername = (TextView) itemView.findViewById(R.id.exchange_layout_user_username);
        textUserPhone = (TextView) itemView.findViewById(R.id.exchange_layout_user_phone);
        textUserComments = (TextView) itemView.findViewById(R.id.exchange_layout_user_comments);

    }

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {

        listener.onClick(view, getAdapterPosition(), false);
    }
}
