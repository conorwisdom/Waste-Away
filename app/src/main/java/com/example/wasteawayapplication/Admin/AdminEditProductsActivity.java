package com.example.wasteawayapplication.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wasteawayapplication.Prevalent.Prevalent;
import com.example.wasteawayapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminEditProductsActivity extends AppCompatActivity {

    private Button applyChangesBtn;
    private EditText editName, editCategory, editDescription, editLocation;
    private ImageView backBtn, editImage;
    private ProgressDialog loadingBar;
    private Uri imageUri;
    private static final int GalleryPick = 1;
    private String productID = "";
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_products);

        applyChangesBtn = (Button) findViewById(R.id.btn_edit_product);
        editName = (EditText) findViewById(R.id.edit_product_name);
        editCategory = (EditText) findViewById(R.id.edit_product_category);
        editDescription = (EditText) findViewById(R.id.edit_product_description);
        editLocation = (EditText) findViewById(R.id.edit_product_location);
        backBtn = (ImageView) findViewById(R.id.iv_edit_back);
        editImage = (ImageView) findViewById(R.id.iv_edit_product_image);
        loadingBar = new ProgressDialog(this);

        productID = getIntent().getStringExtra("pid");
        productRef = FirebaseDatabase.getInstance().getReference().child("Rental Products").child(productID);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }

            private void OpenGallery() {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProductDetails();
            }
        });
    }

    private void updateProductDetails() {

        String productName = editName.getText().toString();
        String productCategory = editCategory.getText().toString();
        String productDescription = editDescription.getText().toString();
        String productLocation = editLocation.getText().toString();

        if (productName.equals("")) {
            Toast.makeText(this, "Product Name Required", Toast.LENGTH_SHORT).show();
        } else if (productCategory.equals("")) {
            Toast.makeText(this, "Product Category Required", Toast.LENGTH_SHORT).show();
        } else if (productDescription.equals("")) {
            Toast.makeText(this, "Product Description Required", Toast.LENGTH_SHORT).show();
        } else if (productLocation.equals("")) {
            Toast.makeText(this, "Product Location Required", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Update Product...");
            loadingBar.setMessage("Updating Product");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("name", productName);
            productMap.put("category", productCategory);
            productMap.put("description", productDescription);
            productMap.put("location", productLocation);
            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        loadingBar.dismiss();
                        Toast.makeText(AdminEditProductsActivity.this, "Product Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminEditProductsActivity.this, AdminHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            editImage.setImageURI(imageUri);
        }
    }
}