package com.example.wasteawayapplication.Admin;

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

import com.example.wasteawayapplication.Prevalent.Prevalent;
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

public class AdminAddProductsActivity extends AppCompatActivity {

    private String productName, productCategory, productDescription, productLocation, productDate, productTime, adminUsername;
    private ImageView backBtn, inputProductImage;
    private EditText inputProductName, inputProductCategory, inputProductDescription, inputProductLocation, inputUsername;
    private Button addNewProductBtn;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String productUniqueKey, downloadImageUrl;
    private StorageReference productImagesRef;
    private DatabaseReference rentalProductsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_products);

        backBtn = (ImageView) findViewById(R.id.iv_admin_add_back);
        inputProductImage = (ImageView) findViewById(R.id.iv_admin_select_product_image);
        inputProductName = (EditText) findViewById(R.id.product_layout_name);
        inputProductCategory = (EditText) findViewById(R.id.product_layout_category);
        inputProductDescription = (EditText) findViewById(R.id.product_layout_description);
        inputProductLocation = (EditText) findViewById(R.id.product_layout_location);
        inputUsername = (EditText) findViewById(R.id.product_layout_username);
        addNewProductBtn = (Button) findViewById(R.id.btn_add_product);
        loadingBar = new ProgressDialog(this);

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        rentalProductsRef = FirebaseDatabase.getInstance().getReference().child("Rental Products");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        inputProductImage.setOnClickListener(new View.OnClickListener() {
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

        addNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }

            //            stores product information in database when add product button is clicked
            private void ValidateProductData() {

                productName = inputProductName.getText().toString();
                productCategory = inputProductCategory.getText().toString();
                productDescription = inputProductDescription.getText().toString();
                productLocation = inputProductLocation.getText().toString();
                adminUsername = inputUsername.getText().toString();

                String username = Prevalent.currentOnlineUser.getUsername();
                if (ImageUri == null) {
                    Toast.makeText(AdminAddProductsActivity.this, "Product Image Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(productName)) {
                    Toast.makeText(AdminAddProductsActivity.this, "Product Name Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(productCategory)) {
                    Toast.makeText(AdminAddProductsActivity.this, "Product Category Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(productDescription)) {
                    Toast.makeText(AdminAddProductsActivity.this, "Product Description Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(productLocation)) {
                    Toast.makeText(AdminAddProductsActivity.this, "Product Location Required", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(adminUsername)) {
                    Toast.makeText(AdminAddProductsActivity.this, "Admin Username Required", Toast.LENGTH_SHORT).show();
                } else if (!adminUsername.equals(username)) {
                    Toast.makeText(AdminAddProductsActivity.this, "Username Must Match Signed In Admin", Toast.LENGTH_SHORT).show();
                } else {
                    StoreProductInformation();
                }
            }

            private void StoreProductInformation() {

                loadingBar.setTitle("Add Product...");
                loadingBar.setMessage("Adding New Product");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                //            records date and time product is uploaded to database
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                productDate = currentDate.format(calendar.getTime());
                SimpleDateFormat currentTime = new SimpleDateFormat(" HH:mm:ss a");
                productTime = currentTime.format(calendar.getTime());

//                creates product UID from date and time uploaded
                productUniqueKey = productDate + productTime;

                final StorageReference filePath = productImagesRef.child(ImageUri.getLastPathSegment()
                        + productUniqueKey + ".jpg");

                final UploadTask uploadTask = filePath.putFile(ImageUri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        String message = exception.toString();
                        Toast.makeText(AdminAddProductsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        generates an image url that is stored in firebase
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                downloadImageUrl = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadImageUrl = task.getResult().toString();
                                    SaveProductInfoToDatabase();
                                }
                            }

                            //                            saves product information to database
                            private void SaveProductInfoToDatabase() {
                                HashMap<String, Object> productMap = new HashMap<>();
                                productMap.put("pid", productUniqueKey);
                                productMap.put("date", productDate);
                                productMap.put("time", productTime);
                                productMap.put("image", downloadImageUrl);
                                productMap.put("category", productCategory);
                                productMap.put("name", productName);
                                productMap.put("description", productDescription);
                                productMap.put("location", productLocation);
                                productMap.put("username", adminUsername);

                                rentalProductsRef.child(productUniqueKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
//                                          sends admin back to dashboard
                                          Intent intent = new Intent(AdminAddProductsActivity.this, AdminHomeActivity.class);
                                          startActivity(intent);
                                            loadingBar.dismiss();
                                            Toast.makeText(AdminAddProductsActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
                                        } else {
                                            loadingBar.dismiss();
                                            String message = task.getException().toString();
                                            Toast.makeText(AdminAddProductsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
            ImageUri = data.getData();
            inputProductImage.setImageURI(ImageUri);
        }
    }
}