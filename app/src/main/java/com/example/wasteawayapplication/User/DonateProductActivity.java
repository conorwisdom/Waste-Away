package com.example.wasteawayapplication.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wasteawayapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DonateProductActivity extends AppCompatActivity {

    private String productName, productCategory, productDescription, productDate, productTime;
    private String productUniqueKey, imageDownloadUrl;
    private ImageView backBtn, donateProductImage;
    private EditText inputProductName, inputProductCategory, inputProductDescription;
    private Button donateProductBtn;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private ProgressDialog loadingBar;
    private StorageReference productImagesRef;
    private DatabaseReference donatedProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_product);

        donateProductImage = (ImageView) findViewById(R.id.iv_donate_product);
        backBtn = (ImageView) findViewById(R.id.iv_donate_back);
        inputProductName = (EditText) findViewById(R.id.donate_product_name);
        inputProductCategory = (EditText) findViewById(R.id.donate_product_category);
        inputProductDescription = (EditText) findViewById(R.id.donate_product_description);
        donateProductBtn = (Button) findViewById(R.id.btn_donate_product);
        loadingBar = new ProgressDialog(this);

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        donatedProductsRef = FirebaseDatabase.getInstance().getReference().child("Donated Products");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        donateProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }

            //            allows user to upload product photo from phone storage when image clicked
            private void OpenGallery() {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });

        donateProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductInformation();
            }

            private void ValidateProductInformation() {

                productName = inputProductName.getText().toString();
                productCategory = inputProductCategory.getText().toString();
                productDescription = inputProductDescription.getText().toString();

                if (imageUri == null) {
                    Toast.makeText(DonateProductActivity.this, "Product Image Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(productName)) {
                    Toast.makeText(DonateProductActivity.this, "Product Name Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(productCategory)) {
                    Toast.makeText(DonateProductActivity.this, "Product Category Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(productDescription)) {
                    Toast.makeText(DonateProductActivity.this, "Product Description Required", Toast.LENGTH_SHORT).show();
                } else {
                    StoreProductInformation();
                }
            }

            private void StoreProductInformation() {

                loadingBar.setTitle("Add Product...");
                loadingBar.setMessage("Adding New Product");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                productDate = currentDate.format(calendar.getTime());
                SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm:ss a");
                productTime = currentTime.format(calendar.getTime());

                productUniqueKey = productDate + productTime;

                final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment()
                        + productUniqueKey + ".jpg");

                final UploadTask uploadTask = filePath.putFile(imageUri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        String message = exception.toString();
                        Toast.makeText(DonateProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                imageDownloadUrl = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    imageDownloadUrl = task.getResult().toString();
                                    SaveProductInfoToDatabase();
                                }
                            }

                            private void SaveProductInfoToDatabase() {
                                HashMap<String, Object> productMap = new HashMap<>();
                                productMap.put("pid", productUniqueKey);
                                productMap.put("date", productDate);
                                productMap.put("time", productTime);
                                productMap.put("image", imageDownloadUrl);
                                productMap.put("category", productCategory);
                                productMap.put("name", productName);
                                productMap.put("description", productDescription);

                                donatedProductsRef.child(productUniqueKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Intent intent = new Intent(DonateProductActivity.this, HomeActivity.class);
                                            startActivity(intent);

                                            loadingBar.dismiss();
                                            Toast.makeText(DonateProductActivity.this, "Product Donated", Toast.LENGTH_SHORT).show();
                                        } else {
                                            loadingBar.dismiss();
                                            String message = task.getException().toString();
                                            Toast.makeText(DonateProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            donateProductImage.setImageURI(imageUri);
        }
    }
}